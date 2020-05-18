package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mybank.restclient.AccountDTO;
import com.example.mybank.restclient.GetService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity implements View.OnClickListener{

    Fragment[] fragmentsList;
    Button btHome, btInfo, btLoan, btTransfer, btUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentsList = new Fragment[5];
        fragmentsList[0] = new HomeFragment();
        fragmentsList[1] = new InfoFragment();
        fragmentsList[2] = new LoanFragment();
        fragmentsList[3] = new TransferFragment();
        fragmentsList[4] = new UserFragment();

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

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFragment, fragmentsList[0]);
        ft.commit();
        getBankAccountInfos();
    }

    @Override
    public void onClick(View v) {
        int selectedBt;
        FragmentManager fm = getSupportFragmentManager();
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


    private void getBankAccountInfos(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<List<AccountDTO>> call = getService.getBankAccountInfo("1000Android");

        call.enqueue(new Callback<List<AccountDTO>>() {
            @Override
            public void onResponse(Call<List<AccountDTO>> call, Response<List<AccountDTO>> response) {
                //Toast.makeText(getBaseContext(), response.body().getId(), Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(), "Nombre: "+response.body().getName(), Toast.LENGTH_LONG).show();
                //Toast.makeText(getBaseContext(), "Dinero: "+String.valueOf(response.body().getMoney()), Toast.LENGTH_LONG).show();
                for (int i = 0; i < response.body().size(); i++) {
                    System.out.println(response.body().get(i).toString());
                }
            }

            @Override
            public void onFailure(Call<List<AccountDTO>> call, Throwable t) {
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
