package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mybank.restclient.dto.UserDTO;

import java.util.ArrayList;

public class ReceiptsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerReceiptAdapter adapter;
    private ArrayList receiptList;
    private UserDTO user;
    private int selectedAccount;
    private int selectedLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts_list);

        Intent i = getIntent();
        user = (UserDTO) i.getSerializableExtra("user");
        selectedAccount = i.getIntExtra("selectedAccount", 0);
        selectedLoan = i.getIntExtra("selectedLoan", 0);


        receiptList = new ArrayList();

        recyclerView = findViewById(R.id.recyclerReceipt);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        receiptList = user.getAccountsList().get(selectedAccount).getLoansDTOList().get(selectedLoan).getReceiptsList();
        adapter = new RecyclerReceiptAdapter(receiptList, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
}
