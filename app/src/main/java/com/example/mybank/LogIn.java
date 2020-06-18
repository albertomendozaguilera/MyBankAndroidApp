package com.example.mybank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mybank.restclient.constants.Constants;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.PostService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogIn extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText etUsername, etEmail, etPassword;
    private CardView cardBankLogo;
    private LottieAnimationView authenticationSuccess, loadingLogo;
    private TextView btLogout;
    private ImageView btFingerprint;
    private Button login;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();

        authenticationSuccess = findViewById(R.id.authenticationSuccess);
        authenticationSuccess.setVisibility(View.INVISIBLE);

        cardBankLogo = findViewById(R.id.cardBankLogo);
        loadingLogo = findViewById(R.id.bankLogo);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btLogout = findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
        login = findViewById(R.id.btLogin);
        btFingerprint = findViewById(R.id.btFingerprint);
        etUsername.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);
        btFingerprint.setVisibility(View.INVISIBLE);

        showLogo();

        progressDialog = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null){
            if (sharedPreferences.getBoolean("session", false) == true) {
                progressDialog.setMessage(getResources().getString(R.string.loging_in));
                progressDialog.show();
                Intent i = new Intent(getApplicationContext(), Main.class);
                startActivity(i);
                progressDialog.dismiss();
            }else {
                login.setText(R.string.bt_signin);
                btFingerprint.setVisibility(View.VISIBLE);
                login.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                login.setBackgroundResource(R.drawable.custom_button);
                                login.setTextColor(Color.parseColor("#FFC107"));
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                login.setBackgroundResource(R.drawable.custom_button_pressed);
                                login.setTextColor(Color.parseColor("#0B8FBA"));
                                signIn();
                                break;
                            }
                        }
                        return false;
                    }
                });
                btFingerprint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fingerprint();
                    }
                });
            }
        }else{
            login.setText(R.string.bt_signup);
            etUsername.setVisibility(View.VISIBLE);
            etEmail.setVisibility(View.VISIBLE);
            login.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            login.setBackgroundResource(R.drawable.custom_button);
                            login.setTextColor(Color.parseColor("#FFC107"));
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            login.setBackgroundResource(R.drawable.custom_button_pressed);
                            login.setTextColor(Color.parseColor("#0B8FBA"));
                            signUp();
                            break;
                        }
                    }
                    return false;
                }
            });
        }

    }

    //crea un dialog para introducir una huella
    public void fingerprint(){
        final Executor executor = Executors.newSingleThreadExecutor();
        //final LogIn activity = this;

        if (Build.VERSION.SDK_INT >= 28) {
            final BiometricPrompt bp = new BiometricPrompt.Builder(this)
                    .setTitle(getResources().getString(R.string.fingerprint))
                    .setNegativeButton(getResources().getString(R.string.cancel), executor, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).build();

            bp.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    startApp();
                }
            });
        }
    }

    //comprobar contraseña para usuario ya logeado
    public void signIn(){
        final String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.loging_in));
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startApp();
                } else {
                    Toast.makeText(LogIn.this, R.string.error_loging_in, Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();}
        });
    }


    //logear usuario o crear uno nuevo si no existe
    public void signUp(){
        final String username = etUsername.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, R.string.enter_username, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage(getResources().getString(R.string.loging_in));
        progressDialog.show();
        System.out.println("hola");
        //intenta iniciar sesion
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    startApp();
                }else{
                    System.out.println("adios");
                    progressDialog.dismiss();
                    //si no existe el usuario pregunta si quiere crear uno nuevo
                        AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                        builder.setMessage(R.string.new_user)
                                .setTitle(R.string.user_not_exist);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.setMessage(getResources().getString(R.string.check_in));
                                progressDialog.show();

                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            addUserToDatabase(mAuth.getUid(), username, email);
                                            //registra y guarda el el nuevo usuario en firebase
                                            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users");
                                            DatabaseReference dbuser = dbr.child(mAuth.getCurrentUser().getUid());
                                            dbuser.child("email").setValue(email);
                                            dbuser.child("password").setValue(password);

                                            //inicia la main activity
                                            startApp();

                                        }else{
                                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                                Toast.makeText(LogIn.this, R.string.user_already_exist, Toast.LENGTH_LONG).show();
                                            }
                                            Toast.makeText(LogIn.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btLogout:
                mAuth.signOut();
                System.out.println(sharedPreferences.getString("selectedAccount", "hola"));
                editor.putString("selectedAccount", "null");
                editor.commit();
                System.out.println(sharedPreferences.getString("selectedAccount", "hola"));
                editor.putBoolean("session", false);
                Intent i = new Intent(getApplicationContext(), LogIn.class);
                finish();
                startActivity(i);
                break;
        }
    }

    private void addUserToDatabase(String id, String name, String email){
        UserDTO userDTO = new UserDTO();
        userDTO.setBlacklist(false);
        userDTO.setEmail(email);
        userDTO.setName(name);
        userDTO.setId(id);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LOCALHOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService postService = retrofit.create(PostService.class);
        Call<UserDTO> call = postService.addUser(userDTO);

        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                System.out.println("Http Status: " + response.code());
                Intent i = new Intent(getApplicationContext(), LogIn.class);
                finish();
                startActivity(i);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                System.out.println("Http Status: " + t.getMessage());
            }
        });

    }

    private void showLogo(){
        cardBankLogo.bringToFront();
        btLogout.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingLogo.setRepeatCount(0);
                cardBankLogo.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
                btLogout.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    private void startApp(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                authenticationSuccess.setVisibility(View.VISIBLE);
                authenticationSuccess.bringToFront();
                login.setVisibility(View.INVISIBLE);
            }
        });
        authenticationSuccess.setRepeatCount(1);
        authenticationSuccess.playAnimation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), Main.class);
                startActivity(i);
            }
        }, 1500);
    }
}
