package com.example.mybank;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.UserDTO;

import java.util.ArrayList;

public class IncomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList<PaymentTransactionsDTO> transactionsList;
    private UserDTO user;
    private int month;
    private int accountId;
    private SharedPreferences preferences;

    public IncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
            month = getArguments().getInt("month");
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        recyclerView = view.findViewById(R.id.recyclerIncome);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);

        transactionsList = new ArrayList();

        getSelectedAccountId();

        createList();

        adapter = new RecyclerAdapter(transactionsList, getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }

    private void createList(){
        for (PaymentTransactionsDTO transaction : user.getAccountsList().get(accountId).getTransactionsDTOList()){
            if (transaction.getDatetime().substring(5, 7).contains(""+month) && transaction.getQuantity() >= 0){
                transactionsList.add(transaction);
            }
        }
    }
}
