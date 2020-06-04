package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mybank.restclient.GetService;
import com.example.mybank.restclient.PaymentTransactionsDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionsList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    ArrayList transactionsList;
    String iban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        Intent i = getIntent();
        iban = i.getStringExtra("iban");

        transactionsList = new ArrayList();

        recyclerView = findViewById(R.id.transactionsListRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        getAccountTransactions(iban);

    }

    private void getAccountTransactions(String iban){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<List<PaymentTransactionsDTO>> call = getService.getPaymentTransactions(iban);

        call.enqueue(new Callback<List<PaymentTransactionsDTO>>() {
            @Override
            public void onResponse(Call<List<PaymentTransactionsDTO>> call, Response<List<PaymentTransactionsDTO>> response) {
                transactionsList = (ArrayList) response.body();
                System.out.println(response.body().get(0).getDatetime().substring(5, 7));
                adapter = new RecyclerAdapter(transactionsList, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<PaymentTransactionsDTO>> call, Throwable t) {
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
    }
}
