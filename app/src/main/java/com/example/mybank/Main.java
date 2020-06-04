package com.example.mybank;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mybank.restclient.AccountDTO;
import com.example.mybank.restclient.GetService;
import com.example.mybank.restclient.UserDTO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

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
    UserDTO user = new UserDTO();
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountsList = new ArrayList();

        getUserInfo();

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        setFragmentConfiguration(bundle);

        setButtonsViewById();

        setButtonsClickListener();

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
        Call<UserDTO> call = getService.getUserFromId(mAuth.getUid());

        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.body() != null) {
                    getUserInfoFromResponse(response.body());
                }else{
                    Toast.makeText(getApplicationContext(), "Error al cargar los datos", Toast.LENGTH_LONG).show();
                    System.out.println("IndexOutOfBoundException");
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(), " los servicios se encuentran en mantenimiento en estos momentos, intentelo de nuevo mas tarde", Toast.LENGTH_LONG).show();
                System.out.println(t.getCause());
                System.out.println(t.getMessage());

            }
        });
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void getUserInfoFromResponse(UserDTO userDTO){
        this.user = userDTO;
    }

    private void setButtonsClickListener() {
        btHome.setOnClickListener(this);
        btInfo.setOnClickListener(this);
        btLoan.setOnClickListener(this);
        btTransfer.setOnClickListener(this);
        btUser.setOnClickListener(this);
    }

    private void setButtonsViewById() {
        btHome = findViewById(R.id.btHome);
        btInfo = findViewById(R.id.btInfo);
        btLoan = findViewById(R.id.btLoan);
        btTransfer = findViewById(R.id.btTransfer);
        btUser = findViewById(R.id.btUser);
    }

    private void setFragmentConfiguration(Bundle bundle) {
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
    }
}
