package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mybank.restclient.dto.UserDTO;

import java.util.ArrayList;

public class LoansList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerLoanAdapter adapter;
    private ArrayList loanList;
    private UserDTO user;
    private int selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loans_list);

        Intent i = getIntent();
        selectedAccount = i.getIntExtra("selectedAccount", 0);
        user = (UserDTO) i.getSerializableExtra("user");

        System.out.println(selectedAccount);
        loanList = new ArrayList();

        recyclerView = findViewById(R.id.recyclerLoans);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        loanList = user.getAccountsList().get(selectedAccount).getLoansDTOList();
        adapter = new RecyclerLoanAdapter(loanList, getApplicationContext(), selectedAccount, user);
        recyclerView.setAdapter(adapter);
    }
}
