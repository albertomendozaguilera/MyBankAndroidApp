package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mybank.restclient.AccountDTO;
import com.example.mybank.restclient.GetService;
import com.example.mybank.restclient.UserDTO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity implements View.OnClickListener{

    boolean a = true;
    Fragment[] fragmentsList;
    Button btHome, btInfo, btLoan, btTransfer, btUser;
    FragmentManager fm;
    ArrayList transactionsList;
    ArrayList <AccountDTO> accountsList;
    //RecyclerView recyclerView;
    //RecyclerView.Adapter adapter;
    UserDTO user;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountsList = new ArrayList();

        user = new UserDTO();
        getUserInfo();
        while(a) {
            System.out.println("nombre: " + user.getName());
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        fragmentsList = new Fragment[5];
        fragmentsList[0] = new HomeFragment();
        fragmentsList[0].setArguments(bundle);
        fragmentsList[1] = new InfoFragment();
        //fragmentsList[1].setArguments(bundle);
        fragmentsList[2] = new LoanFragment();
        //fragmentsList[2].setArguments(bundle);
        fragmentsList[3] = new TransferFragment();
        //fragmentsList[3].setArguments(bundle);
        fragmentsList[4] = new UserFragment();
        //fragmentsList[4].setArguments(bundle);

        btHome = findViewById(R.id.btHome);
        btInfo = findViewById(R.id.btInfo);
        btLoan = findViewById(R.id.btLoan);
        btTransfer = findViewById(R.id.btTransfer);
        btUser = findViewById(R.id.btUser);

        btHome.setOnClickListener(this);
        btInfo.setOnClickListener(this);
        btLoan.setOnClickListener(this);
        btTransfer.setOnClickListener(this);
        btUser.setOnClickListener(this);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFragment, fragmentsList[0]);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fm.beginTransaction();

        switch (v.getId()){
            case R.id.btHome:
                ft.replace(R.id.mainFragment, fragmentsList[0]);
                ft.commit();
                break;
            case R.id.btInfo:
                ft.replace(R.id.mainFragment, fragmentsList[1]);
                ft.commit();
                break;
            case R.id.btLoan:
                ft.replace(R.id.mainFragment, fragmentsList[2]);
                ft.commit();
                break;
            case R.id.btTransfer:
                ft.replace(R.id.mainFragment, fragmentsList[3]);
                ft.commit();
                break;
            case R.id.btUser:
                ft.replace(R.id.mainFragment, fragmentsList[4]);
                ft.commit();
                break;
        }
    }

    private void getUserInfo(){
        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<List<UserDTO>> call = getService.getUserFromId(mAuth.getUid());

        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.body().size() == 1) {
                    user = response.body().get(0);
                    a = false;
                    System.out.println("a: " + a);
                    System.out.println(user.getName());
                }else{
                    Toast.makeText(getApplicationContext(), "Error al cargar los datos", Toast.LENGTH_LONG).show();
                    System.out.println("IndexOutOfBoundException");
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), " los servicios se encuentran en mantenimiento en estos momentos, intentelo de nuevo mas tarde", Toast.LENGTH_LONG).show();
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
    }

    /*private ArrayList<AccountDTO> getAccountInfo(String id){
        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<List<AccountDTO>> call = getService.getBankAccountInfo(id);

        call.enqueue(new Callback<List<AccountDTO>>() {
            @Override
            public void onResponse(Call<List<AccountDTO>> call, Response<List<AccountDTO>> response) {
                if (response.body().size()>0) {
                    AccountDTO ac;
                    for (AccountDTO account : response.body()){
                        account.getTransactionsList()
                        ac = new AccountDTO();
                        ac.setUserDTO(user);
                        ac.setIban(account.getIban());
                        ac.setName(account.getName());
                        ac.setTransactionsList(getAccountTransactions(account.getIban()));
                        accountsList.add(ac);
                        System.out.println(accountsList.get(0));
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Error al cargar los datos", Toast.LENGTH_LONG).show();
                    System.out.println("IndexOutOfBoundException");
                }
            }

            @Override
            public void onFailure(Call<List<AccountDTO>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Nuestros servicios se encuentran en mantenimiento en estos momentos, intentelo de nuevo mas tarde", Toast.LENGTH_LONG).show();
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
        //System.out.println(accountsList.get(0));

        return accountsList;
    }*/

    /*private ArrayList getAccountTransactions(String iban){
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
                System.out.println(transactionsList.get(0));
            }

            @Override
            public void onFailure(Call<List<PaymentTransactionsDTO>> call, Throwable t) {
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
        return transactionsList;
    }*/


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
