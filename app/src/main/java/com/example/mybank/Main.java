package com.example.mybank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mybank.restclient.controllers.PaymentWaysController;
import com.example.mybank.restclient.controllers.UserController;
import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.LoanDTO;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnPaymentWaysResponse;
import com.example.mybank.restclient.interfaces.OnUserInfoResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class Main extends AppCompatActivity implements View.OnClickListener{

    private Fragment[] fragmentsList;
    private LottieAnimationView loadingView, btHome, btInfo, btLoan, btTransfer, btUser;
    private FragmentManager fm;
    private ArrayList <AccountDTO> accountsList;
    private UserDTO user = new UserDTO();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UserController userController;
    private PaymentWaysController paymentWaysController;
    private Bundle bundle = new Bundle();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingView = findViewById(R.id.loadingView);
        loadingView.bringToFront();
        loadingView.playAnimation();


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        userController = new UserController();

        //accountsList = new ArrayList();

        userController.getUserInfo(mAuth.getUid(), new OnUserInfoResponse() {
            @Override
            public void getUserDTO(UserDTO userDTO) {
                user = userDTO;
                createViewWithFragments();
                loadingView.setRepeatCount(0);
                loadingView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                loadingView.setAnimation(R.raw.error_in_cloud);
                loadingView.setRepeatCount(1);
                loadingView.playAnimation();
                Toast.makeText(getApplicationContext(), " los servicios se encuentran en mantenimiento en estos momentos, intentelo de nuevo mas tarde", Toast.LENGTH_LONG).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishActivity();
                    }
                }, 5000);
            }
        });
    }

    private void finishActivity (){
        Intent i = new Intent(getApplicationContext(), LogIn.class);
        this.finish();
        startActivity(i);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.settings:
                Toast.makeText(this, "HOLA", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        switch (v.getId()){
            case R.id.btHome:
                btHome.playAnimation();
                ft.replace(R.id.mainFragment, fragmentsList[0]);
                ft.commit();
                break;
            case R.id.btInfo:
                btInfo.playAnimation();
                ft.replace(R.id.mainFragment, fragmentsList[1]);
                ft.commit();
                break;
            case R.id.btLoan:
                btLoan.playAnimation();
                ft.replace(R.id.mainFragment, fragmentsList[2]);
                ft.commit();
                break;
            case R.id.btTransfer:
                btTransfer.playAnimation();
                ft.replace(R.id.mainFragment, fragmentsList[3]);
                ft.commit();
                break;
            case R.id.btUser:
                btUser.playAnimation();
                ft.replace(R.id.mainFragment, fragmentsList[4]);
                ft.commit();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    private void createViewWithFragments() {
        bundle.putSerializable("user", user);

        setFragmentConfiguration(bundle);

        setButtonsViewById();

        setButtonsClickListener();

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.mainFragment, fragmentsList[0]);
        ft.commit();
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
        fragmentsList[1].setArguments(bundle);
        fragmentsList[2] = new LoanFragment();
        fragmentsList[2].setArguments(bundle);
        fragmentsList[3] = new TransferFragment();
        fragmentsList[3].setArguments(bundle);
        fragmentsList[4] = new UserFragment();
        fragmentsList[4].setArguments(bundle);
    }
}
