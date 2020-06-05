package com.example.mybank;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mybank.restclient.controllers.UserController;
import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnUserInfoResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class Main extends AppCompatActivity implements View.OnClickListener{

    Fragment[] fragmentsList;
    Button btHome, btInfo, btLoan, btTransfer, btUser;
    FragmentManager fm;
    ArrayList <AccountDTO> accountsList;
    UserDTO user = new UserDTO();
    FirebaseAuth mAuth;
    UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userController = new UserController();

        setContentView(R.layout.activity_main);

        accountsList = new ArrayList();

        userController.getUserInfo(mAuth, new OnUserInfoResponse() {
            @Override
            public void getUserDTO(UserDTO userDTO) {
                user = userDTO;

                createViewWithFragments();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getApplicationContext(), " los servicios se encuentran en mantenimiento en estos momentos, intentelo de nuevo mas tarde", Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });

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


    private void createViewWithFragments() {
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
    public void onBackPressed() {
        finishAffinity();
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
