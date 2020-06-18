package com.example.mybank;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.GetService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList transactionsList;
    private UserDTO user;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        Intent i = getIntent();
        user = (UserDTO) i.getSerializableExtra("user");
        accountId = i.getIntExtra("accountId", 0);

        transactionsList = new ArrayList();

        recyclerView = findViewById(R.id.transactionsListRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);


        transactionsList = user.getAccountsList().get(accountId).getTransactionsDTOList();
        adapter = new RecyclerAdapter(transactionsList, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
