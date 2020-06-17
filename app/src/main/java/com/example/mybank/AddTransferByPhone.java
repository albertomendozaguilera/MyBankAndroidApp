package com.example.mybank;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.mybank.restclient.constants.Constants;
import com.example.mybank.restclient.controllers.UserController;
import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnUserInfoResponse;
import com.example.mybank.restclient.interfaces.PostService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTransferByPhone extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private UserController userController;
    private EditText etPhone, etQuantity, etConcept;
    private Button btAddTransactionByPhone;
    private UserDTO mainUser, destinyUser;
    private int accountId;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transfer_by_phone);
        mAuth = FirebaseAuth.getInstance();
        findViewsById();

        Bundle extras = getIntent().getExtras();
        mainUser = (UserDTO) extras.getSerializable("user");
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getSelectedAccountId();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando");
    }


    private void findViewsById(){
        etPhone = findViewById(R.id.etPhone);
        etQuantity = findViewById(R.id.etQuantity);
        etConcept = findViewById(R.id.etConcept);
        btAddTransactionByPhone = findViewById(R.id.btAddTransactionByPhone);
        /*btAddTransactionByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    checkUser();
                }
            }
        });*/
        btAddTransactionByPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        btAddTransactionByPhone.setBackgroundResource(R.drawable.custom_button);
                        btAddTransactionByPhone.setTextColor(Color.parseColor("#FFC107"));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        btAddTransactionByPhone.setBackgroundResource(R.drawable.custom_button_pressed);
                        btAddTransactionByPhone.setTextColor(Color.parseColor("#0B8FBA"));
                        if (checkFields()) {
                            checkUser();
                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

    private boolean checkFields(){
        if (TextUtils.isEmpty(etPhone.getText())){
            Toast.makeText(this, "Debes introducir un numero de telefono", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etQuantity.getText())){
            Toast.makeText(this, "Debes introducir una cantidad", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etConcept.getText())){
            Toast.makeText(this, "Debes introducir un concepto", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void getUser(String id){
        userController.getUserInfo(id, new OnUserInfoResponse() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void getUserDTO(UserDTO userDTO) {
                destinyUser = userDTO;
                addTransaction(destinyUser);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }


    private void checkUser(){
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users");
        // Read from the database
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean checkOk = false;
                progressDialog.show();
                for (DataSnapshot dataSnapshott : dataSnapshot.getChildren()){
                    if (dataSnapshott.child("phone").getValue()!=null && dataSnapshott.child("phone").getValue().equals(etPhone.getText())){
                        getUser(dataSnapshott.getKey());
                        checkOk = true;
                        progressDialog.dismiss();
                        break;
                    }
                }
                progressDialog.dismiss();
                if (!checkOk){
                    Toast.makeText(getApplicationContext(), "Ese numero no está vinculado a ninguna cuenta o no existe", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addTransaction(UserDTO userDTO){
        PaymentTransactionsDTO transactionDTO = new PaymentTransactionsDTO();
        transactionDTO.setId(null);
        transactionDTO.setAccountDTO(mainUser.getAccountsList().get(accountId));
        transactionDTO.setDestinyAccount(userDTO.getAccountsList().get(0).getIban());
        transactionDTO.setQuantity(Double.parseDouble(etQuantity.getText().toString()));
        transactionDTO.setBeneficiary(userDTO.getName());
        transactionDTO.setConcept(etConcept.getText().toString());
        transactionDTO.setOriginAccount(preferences.getString("selectedAccount", "false"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        transactionDTO.setDatetime(dtf.format(now));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HEROKU_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService postService = retrofit.create(PostService.class);
        Call<Void> call = postService.addTransaction(transactionDTO);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getBaseContext(), "Http Status: " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Http Status: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < mainUser.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(mainUser.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }
}
