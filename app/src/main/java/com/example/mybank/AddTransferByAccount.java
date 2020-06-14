package com.example.mybank;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    UserDTO user;
    int accountId;
    SharedPreferences preferences;
    EditText etIban, etQuantity, etBeneficiary, etConcept;
    Button btAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transfer_by_account);

        Bundle extras = getIntent().getExtras();
        user = (UserDTO) extras.getSerializable("user");
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        getSelectedAccountId();

        etIban = findViewById(R.id.etIban);
        etQuantity = findViewById(R.id.etQuantity);
        etBeneficiary = findViewById(R.id.etBeneficiary);
        etConcept = findViewById(R.id.etConcept);
        btAddTransaction = findViewById(R.id.btAddTransaction);
        btAddTransaction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                PaymentTransactionsDTO transactionDTO = new PaymentTransactionsDTO();
                transactionDTO.setId("" + getLastTransaction());
                transactionDTO.setIban(preferences.getString("selectedAccount", "false"));
                transactionDTO.setDestinyAccount(etIban.getText().toString());
                transactionDTO.setQuantity(Double.parseDouble(etQuantity.getText().toString()));
                transactionDTO.setBeneficiary(etBeneficiary.getText().toString());
                transactionDTO.setConcept(etConcept.getText().toString());
                transactionDTO.setOriginAccount(preferences.getString("selectedAccount", "false"));

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                transactionDTO.setDatetime(dtf.format(now));

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                PostService postService = retrofit.create(PostService.class);
                Call<PaymentTransactionsDTO> call = postService.addTransaction(transactionDTO);

                call.enqueue(new Callback<PaymentTransactionsDTO>() {
                    @Override
                    public void onResponse(Call<PaymentTransactionsDTO> call, Response<PaymentTransactionsDTO> response) {
                        Toast.makeText(getBaseContext(), "Http Status: " + response.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<PaymentTransactionsDTO> call, Throwable t) {
                        Toast.makeText(getBaseContext(), "Http Status: " + t.getCause(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private int getLastTransaction(){
        int lastTransaction = 0;
        for (PaymentTransactionsDTO transaction : user.getAccountsList().get(accountId).getTransactionsDTOList()){
            if (Integer.parseInt(transaction.getId()) > lastTransaction){
                lastTransaction = Integer.parseInt(transaction.getId());
            }
        }
        return lastTransaction;
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }
}
