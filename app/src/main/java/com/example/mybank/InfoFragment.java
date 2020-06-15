package com.example.mybank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mybank.restclient.controllers.PaymentWaysController;
import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnPaymentWaysResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class InfoFragment extends Fragment implements View.OnClickListener {

    private final String [] MONTHLIST = {"ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC"};
    private ArrayList actualMonthList, list1, list2, list3, list4;
    private ArrayList<PaymentTransactionsDTO> last4MonthsTransactionList;
    private Calendar calendar = Calendar.getInstance();
    private int actualMonth = calendar.get(Calendar.MONTH)+1;
    private int selectedMonth = actualMonth;
    private double income, expenses, savings;
    private TextView tvSavings, tvIncome, tvExpenses, tvMonth1, tvMonth2, tvMonth3, tvMonth4, tvSelectedMonth;
    private ImageView month1inc, month2inc, month3inc, month4inc, month1Exp, month2Exp, month3Exp, month4Exp;
    private int [] last4Months = {actualMonth, (actualMonth -1), (actualMonth -2), (actualMonth -3)};;
    private UserDTO user;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    TabLayoutMediator tabLayoutMediator;
    private int accountId;
    private ConstraintLayout layoutForeground;
    private SharedPreferences preferences;
    private final int MAXHEIGHT = 250;
    private final int MINHEIGHT = 0;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            viewPagerAdapter = new ViewPagerAdapter(this, user, selectedMonth);
            getSelectedAccountId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        findViewsById(view);

        viewPager.setAdapter(viewPagerAdapter);

        if (tabLayoutMediator == null) {
            tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            switch (position) {
                                case 0:
                                    tab.setText("INGRESOS");
                                    break;
                                case 1:
                                    tab.setText("GASTOS");
                                    break;
                            }
                        }
                    });
            tabLayoutMediator.attach();
        }

        if (user.getAccountsList().get(accountId).getTransactionsDTOList().size() != 0) {
            getTransactionsInfo();

            generateGraphs();
        }else{

        }
        return view;
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }

    private void changeTransactionsForSelectedMonth(){
        viewPagerAdapter = new ViewPagerAdapter(this, user, selectedMonth);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void findViewsById(View view) {
        //layoutForeground = view.findViewById(R.id.);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.pager);
        tvSavings = view.findViewById(R.id.tvSavings);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpenses = view.findViewById(R.id.tvExpenses);
        tvMonth1 = view.findViewById(R.id.tvMonth1);
        tvMonth1.setOnClickListener(this);
        tvMonth2 = view.findViewById(R.id.tvMonth2);
        tvMonth2.setOnClickListener(this);
        tvMonth3 = view.findViewById(R.id.tvMonth3);
        tvMonth3.setOnClickListener(this);
        tvMonth4 = view.findViewById(R.id.tvMonth4);
        tvMonth4.setOnClickListener(this);
        tvSelectedMonth = view.findViewById(R.id.tvSelectedMonth);
        month1inc = view.findViewById(R.id.month1Inc);
        month1Exp = view.findViewById(R.id.month1exp);
        month2inc = view.findViewById(R.id.month2Inc);
        month2Exp = view.findViewById(R.id.month2exp);
        month3inc = view.findViewById(R.id.month3Inc);
        month3Exp = view.findViewById(R.id.month3exp);
        month4inc = view.findViewById(R.id.month4Inc);
        month4Exp = view.findViewById(R.id.month4exp);
    }

    private void getTransactionsInfo(){
        tvMonth1.setText(MONTHLIST[actualMonth -3]);
        tvMonth2.setText(MONTHLIST[actualMonth -2]);
        tvMonth3.setText(MONTHLIST[actualMonth -1]);
        tvMonth4.setText(MONTHLIST[actualMonth]);

        last4MonthsTransactionList =  new ArrayList();

        for (PaymentTransactionsDTO transaction : user.getAccountsList().get(accountId).getTransactionsDTOList()) {
            for (int i = 0; i <= 3; i++) {
                if (transaction.getDatetime().substring(5, 7).contains(""+last4Months[i])){
                    last4MonthsTransactionList.add(transaction);
                }
            }
        }
        calculateSelectedMonth(last4MonthsTransactionList);
    }


    private void generateGraphs() {
        double biggerTransaction = 0;
        list1 = new ArrayList<PaymentTransactionsDTO>();
        list2 = new ArrayList<PaymentTransactionsDTO>();
        list3 = new ArrayList<PaymentTransactionsDTO>();
        list4 = new ArrayList<PaymentTransactionsDTO>();
        for (PaymentTransactionsDTO transaction : last4MonthsTransactionList){
            if (transaction.getQuantity() > biggerTransaction){
                biggerTransaction = transaction.getQuantity();
            }else if(transaction.getQuantity() * -1 > biggerTransaction){
                biggerTransaction = transaction.getQuantity();
            }
        }
        for (PaymentTransactionsDTO transaction : last4MonthsTransactionList){
            if (transaction.getDatetime().substring(5, 7).contains(""+last4Months[0])){
                list1.add(transaction);
            }else if(transaction.getDatetime().substring(5, 7).contains(""+last4Months[1])){
                list2.add(transaction);
            }else if(transaction.getDatetime().substring(5, 7).contains(""+last4Months[2])){
                list3.add(transaction);
            }else{
                list4.add(transaction);
            }
        }

        a(list1, biggerTransaction);
        b(list2, biggerTransaction);
        c(list3, biggerTransaction);
        d(list4, biggerTransaction);
    }

    private void a(ArrayList<PaymentTransactionsDTO>transactionsList, double bigger){
        double incomee = 0;
        double expensess = 0;
        for (PaymentTransactionsDTO transaction : transactionsList){
            if (transaction.getQuantity()>=0){
                incomee += transaction.getQuantity();
            }else{
                expensess += transaction.getQuantity();
            }
        }
        month4inc.getLayoutParams().height = (int)((incomee*MAXHEIGHT)/bigger);
        month4Exp.getLayoutParams().height = (int)(((expensess*MAXHEIGHT)/bigger)*-1);
    }
    private void b(ArrayList<PaymentTransactionsDTO>transactionsList, double bigger){
        double incomee = 0;
        double expensess = 0;
        for (PaymentTransactionsDTO transaction : transactionsList){
            if (transaction.getQuantity()>=0){
                incomee += transaction.getQuantity();
            }else{
                expensess += transaction.getQuantity();
            }
        }
        month3inc.getLayoutParams().height = (int)((incomee*MAXHEIGHT)/bigger);
        month3Exp.getLayoutParams().height = (int)(((expensess*MAXHEIGHT)/bigger)*-1);
    }
    private void c(ArrayList<PaymentTransactionsDTO>transactionsList, double bigger){
        double incomee = 0;
        double expensess = 0;
        for (PaymentTransactionsDTO transaction : transactionsList){
            if (transaction.getQuantity()>=0){
                incomee += transaction.getQuantity();
            }else{
                expensess += transaction.getQuantity();
            }
        }
        month2inc.getLayoutParams().height = (int)((incomee*MAXHEIGHT)/bigger);
        month2Exp.getLayoutParams().height = (int)(((expensess*MAXHEIGHT)/bigger)*-1);
    }
    private void d(ArrayList<PaymentTransactionsDTO>transactionsList, double bigger){
        double incomee = 0;
        double expensess = 0;
        for (PaymentTransactionsDTO transaction : transactionsList){
            if (transaction.getQuantity()>=0){
                incomee += transaction.getQuantity();
            }else{
                expensess += transaction.getQuantity();
            }
        }
        month1inc.getLayoutParams().height = (int)((incomee*MAXHEIGHT)/bigger);
        month1Exp.getLayoutParams().height = (int)(((expensess*MAXHEIGHT)/bigger)*-1);
    }


    private void calculateSelectedMonth(ArrayList <PaymentTransactionsDTO> last4MonthsList){
        income = 0;
        expenses = 0;
        savings = 0;
        actualMonthList = new ArrayList<PaymentTransactionsDTO>();

        for (PaymentTransactionsDTO transaction : last4MonthsList){
            if (transaction.getDatetime().substring(5, 7).contains(""+ selectedMonth)){
                actualMonthList.add(transaction);
                if (transaction.getQuantity()>=0){
                    income += transaction.getQuantity();
                }else{
                    expenses += transaction.getQuantity();
                }
            }
        }

        tvSelectedMonth.setText("   "+MONTHLIST[selectedMonth]);
        savings = income + expenses;
        DecimalFormat df = new DecimalFormat("###.##");
        tvIncome.setText(""+income);
        tvExpenses.setText(""+expenses);
        if (savings > 0) {
            tvSavings.setText("Has ahorrado " + df.format(savings) + "€");
        }else if(savings < 0){
            tvSavings.setText("Te has pasado " + df.format(savings) + "€");
        }else{
            tvSavings.setText("No has ahorrado nada, pero tampoco te has pasado");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvMonth1:
                selectedMonth = last4Months[3];
                calculateSelectedMonth(last4MonthsTransactionList);
                changeTransactionsForSelectedMonth();
                break;
            case R.id.tvMonth2:
                selectedMonth = last4Months[2];
                calculateSelectedMonth(last4MonthsTransactionList);
                changeTransactionsForSelectedMonth();
                break;
            case R.id.tvMonth3:
                selectedMonth = last4Months[1];
                calculateSelectedMonth(last4MonthsTransactionList);
                changeTransactionsForSelectedMonth();
                break;
            case R.id.tvMonth4:
                selectedMonth = last4Months[0];
                calculateSelectedMonth(last4MonthsTransactionList);
                changeTransactionsForSelectedMonth();
                break;
        }
    }
}
