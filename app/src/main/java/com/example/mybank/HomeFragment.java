package com.example.mybank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.UserDTO;

public class HomeFragment extends Fragment {

    UserDTO user;
    TableLayout tl;
    TextView balance, seeAll;
    ConstraintLayout l;
    Button btSettings, btAccounts;
    int accountId;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = preferences.edit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View frag = inflater.inflate(R.layout.fragment_home, container, false);

        findViewsById(frag);

        l.setForeground(null);

        if (preferences.getString("selectedAccount", "false").equals("false")){
            editor.putString("selectedAccount", user.getAccountsList().get(0).getIban());
            editor.commit();
        }
        getSelectedAccountId();
        setBalance(accountId);
        setFirstTransactions(accountId);

        return frag;
    }

    private void findViewsById(View frag) {
        tl = frag.findViewById(R.id.tableLayout);
        l = frag.findViewById(R.id.homeLayout);
        balance = frag.findViewById(R.id.tvBalance);
        seeAll = frag.findViewById(R.id.seeAll);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext().getApplicationContext(), TransactionsList.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });
        btSettings = frag.findViewById(R.id.btSettings);
        btSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupSettings(btSettings);
            }
        });
        btAccounts = frag.findViewById(R.id.btAccounts);
        btAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupAccounts(btAccounts);
            }
        });
    }

    public void showPopupSettings(View v) {
        PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.settings:
                        Intent intent = new Intent(getActivity().getApplicationContext(), PreferencesActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void showPopupAccounts(View v) {
        final PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);

        for (AccountDTO account : user.getAccountsList()){
            popup.getMenu().add(account.getIban());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                editor.putString("selectedAccount", item.getTitle().toString());
                editor.commit();
                getSelectedAccountId();
                setBalance(accountId);
                setFirstTransactions(accountId);
                return true;
            }
        });
        popup.show();
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }

    private void setBalance(int account){
        balance.setText(user.getAccountsList().get(account).getBalance() + " €");
    }

    private void setFirstTransactions(int account){
        tl.removeAllViews();
        for (PaymentTransactionsDTO transaction : user.getAccountsList().get(account).getTransactionsDTOList()){

            //NEW ROW
            TableRow tr = new TableRow(getActivity().getApplicationContext());
            tr.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //CONCEPT TEXTVIEW
            TextView tv1 = new TextView(getActivity().getApplicationContext());
            tv1.setGravity(Gravity.LEFT);
            tv1.setTextSize(24);
            tv1.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f));

            //QUANTITY TEXTVIEW
            TextView tv2 = new TextView(getActivity().getApplicationContext());
            tv2.setGravity(Gravity.LEFT);
            tv2.setTextSize(24);
            tv1.setText(transaction.getConcept());

            //SET TEXTS
            if (transaction.getQuantity() > 0) {
                tv2.setText("+" + transaction.getQuantity() + "€");
                tv2.setTextColor(Color.GREEN);
            }else{
                tv2.setText(transaction.getQuantity() + "€");
            }

            //ADD TEXTS TO ROW
            tr.addView(tv1);
            tr.addView(tv2);
            tl.addView(tr);
        }
    }
}
