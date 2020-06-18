package com.example.mybank;

import android.content.Intent;
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
import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.PostService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTransferByAccount extends AppCompatActivity {

    private UserDTO user;
    private int accountId;
    private SharedPreferences preferences;
    private EditText etIban, etQuantity, etBeneficiary, etConcept;
    private Button btAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transfer_by_account);

        Bundle extras = getIntent().getExtras();
        user = (UserDTO) extras.getSerializable("user");

        UserDTO accountUser = new UserDTO();
        accountUser.setBlacklist(user.isBlacklist());
        accountUser.setEmail(user.getEmail());
        accountUser.setId(user.getId());
        accountUser.setName(user.getName());

        user.getAccountsList().get(accountId).setUserDTO(accountUser);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        getSelectedAccountId();

        etIban = findViewById(R.id.etIban);
        etQuantity = findViewById(R.id.etQuantity);
        etBeneficiary = findViewById(R.id.etBeneficiary);
        etConcept = findViewById(R.id.etConcept);
        btAddTransaction = findViewById(R.id.btAddTransaction);
        /*btAddTransaction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    addUser();
                }
            }
        });*/
        btAddTransaction.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        btAddTransaction.setBackgroundResource(R.drawable.custom_button);
                        btAddTransaction.setTextColor(Color.parseColor("#FFC107"));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        btAddTransaction.setBackgroundResource(R.drawable.custom_button_pressed);
                        btAddTransaction.setTextColor(Color.parseColor("#0B8FBA"));
                        if (checkFields()) {
                            addUser();
                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

    private boolean checkFields(){
        if (TextUtils.isEmpty(etIban.getText())){
            Toast.makeText(this, "Debes introducir un iban", Toast.LENGTH_LONG).show();
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
        if (TextUtils.isEmpty(etBeneficiary.getText())){
            Toast.makeText(this, "Debes introducir un beneficiario", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addUser(){
        PaymentTransactionsDTO transactionDTO = new PaymentTransactionsDTO();
        transactionDTO.setId(null);
        transactionDTO.setAccountDTO(null);
        transactionDTO.setDestinyAccount(etIban.getText().toString());
        transactionDTO.setQuantity(Double.parseDouble(etQuantity.getText().toString())*-1);
        transactionDTO.setBeneficiary(etBeneficiary.getText().toString());
        transactionDTO.setConcept(etConcept.getText().toString());
        transactionDTO.setOriginAccount(preferences.getString("selectedAccount", "false"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        transactionDTO.setDatetime(dtf.format(now));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LOCALHOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService postService = retrofit.create(PostService.class);
        Call<Void> call = postService.addTransaction(transactionDTO);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                finishAffinity();
                Intent i = new Intent(getApplicationContext(), Main.class);
                startActivity(i);
                //Toast.makeText(getBaseContext(), "Http Status: " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //Toast.makeText(getBaseContext(), "Http Status: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }
}
