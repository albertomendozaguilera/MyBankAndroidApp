package com.example.mybank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mybank.restclient.PaymentTransactionsDTO;

import java.util.ArrayList;
import java.util.Calendar;


public class InfoFragment extends Fragment {

    final String [] monthsList = {"ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC"};
    ArrayList actualMonthList, last4MonthsList;
    double income, expenses, savings;
    TextView tvSavings, tvIncome, tvExpenses, tvMonth1, tvMonth2, tvMonth3, tvMonth4, tvMonth44;
    Main m;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        tvSavings = view.findViewById(R.id.tvSavings);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpenses = view.findViewById(R.id.tvExpenses);
        tvMonth1 = view.findViewById(R.id.tvMonth1);
        tvMonth2 = view.findViewById(R.id.tvMonth2);
        tvMonth3 = view.findViewById(R.id.tvMonth3);
        tvMonth4 = view.findViewById(R.id.tvMonth4);
        tvMonth44 = view.findViewById(R.id.tvMonth44);

        getTransactionsInfo();

        calculateActualMonth(actualMonthList);

        return view;
    }

    private void getTransactionsInfo(){
        Calendar calendar = Calendar.getInstance();
        int actualMonth = calendar.get(Calendar.MONTH);

        tvMonth1.setText(monthsList[actualMonth-3]);
        tvMonth2.setText(monthsList[actualMonth-2]);
        tvMonth3.setText(monthsList[actualMonth-1]);
        tvMonth4.setText(monthsList[actualMonth]);
        tvMonth44.setText(tvMonth4.getText());

        String [] last4Months = {""+actualMonth, ""+(actualMonth-1), ""+(actualMonth-2), ""+(actualMonth-3)};
        actualMonthList = new ArrayList<PaymentTransactionsDTO>();
        last4MonthsList =  new ArrayList<PaymentTransactionsDTO>();
        m = (Main)getActivity();
        System.out.println(m.user.getAccountsList().get(0).getTransactionsDTOList());
        /*for (PaymentTransactionsDTO pt : m.user.getAccountsList().get(0).getTransactionsList()) {
            if (pt.getDatetime().substring(5, 7).contains(""+actualMonth)){
                actualMonthList.add(pt);
            }
            for (int i = 0; i < 3; i++) {
                if (pt.getDatetime().substring(5, 7).contains(last4Months[i])){
                    last4MonthsList.add(pt);
                }
            }
        }*/
    }


    private void calculateActualMonth(ArrayList<PaymentTransactionsDTO>list){
        income = 0;
        expenses = 0;
        savings = 0;
        for (PaymentTransactionsDTO pt : list){
            if (pt.getQuantity()>=0){
                income += pt.getQuantity();
            }else{
                expenses += pt.getQuantity();
            }
        }
        savings = income - expenses;
        tvIncome.setText(""+income);
        tvExpenses.setText(""+expenses);
        tvSavings.setText(""+savings);
    }
}
