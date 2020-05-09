package com.example.mybank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybank.restclient.BankAccountInfo;
import com.example.mybank.restclient.GetService;
import com.example.mybank.restclient.PostService;
import com.example.mybank.restclient.User;
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
    private EditText etEmail, etPassword;
    private TextView btLogout;
    private ImageView btFingerprint;
    private Button login, getButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btLogout = findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
        login = findViewById(R.id.btLogin);
        getButton = findViewById(R.id.button);
        getButton.setOnClickListener(this);
        btFingerprint = findViewById(R.id.btFingerprint);
        etEmail.setVisibility(View.INVISIBLE);
        btFingerprint.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null){

            login.setText("SignIn");
            btFingerprint.setVisibility(View.VISIBLE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
            btFingerprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fingerprint();
                }
            });
            fingerprint();
        }else{
            login.setText("SignUp");
            etEmail.setVisibility(View.VISIBLE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signUp();
                }
            });
        }

    }

    //crea un dialog para introducir una huella
    public void fingerprint(){
        final Executor executor = Executors.newSingleThreadExecutor();
        final LogIn activity = this;

        if (Build.VERSION.SDK_INT >= 28) {
            final BiometricPrompt bp = new BiometricPrompt.Builder(this)
                    .setTitle("fingerprint")
                    .setNegativeButton("cancel", executor, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).build();

            bp.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    startActivity(i);
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "succed", Toast.LENGTH_LONG).show();
                        }
                    });*/
                }
            });
        }
    }

    //comprobar contraseña para usuario ya logeado
    public void signIn(){
        final String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "error  pass", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("iniciando sesion...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    startActivity(i);
                    Toast.makeText(LogIn.this, "sesion iniciada", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LogIn.this, "error al iniciar sesion", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }


    //logear usuario o crear uno nuevo si no existe
    public void signUp(){
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Debes introducir un email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Debes introducir una contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("iniciando sesion...");
        progressDialog.show();
        //intenta iniciar sesion
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), Main.class);
                    startActivity(i);
                    Toast.makeText(LogIn.this, "sesion iniciada", Toast.LENGTH_LONG).show();
                }else{
                    progressDialog.dismiss();
                    //si no existe pregunta si quiere crear uno nuevo
                        AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                        builder.setMessage("¿quieres crear uno nuevo?")
                                .setTitle("El usuario no existe");
                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.setMessage("registrando...");
                                progressDialog.show();

                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            //registra y guarda el el nuevo usuario en firebase
                                            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("users");
                                            DatabaseReference dbuser = dbr.child(mAuth.getCurrentUser().getUid());
                                            dbuser.child("email").setValue(email);
                                            dbuser.child("password").setValue(password);
                                            //
                                            //guarda el nuevo usuario en oracle
                                            //

                                            Intent i = new Intent(getApplicationContext(), Main.class);
                                            startActivity(i);
                                            Toast.makeText(LogIn.this, "usuario creado", Toast.LENGTH_LONG).show();
                                            addUserToDatabase();

                                        }else{
                                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                                Toast.makeText(LogIn.this, "Ese usuario ya existe", Toast.LENGTH_LONG).show();
                                            }
                                            Toast.makeText(LogIn.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
            case R.id.button:
                getBankAccountInfos();
                break;
            case R.id.btLogout:
                mAuth.signOut();
                Intent i = new Intent(getApplicationContext(), LogIn.class);
                finish();
                startActivity(i);
                break;
        }
    }

    private void getBankAccountInfos(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<BankAccountInfo> call = getService.getBankAccountInfo("Alberto");

        call.enqueue(new Callback<BankAccountInfo>() {
            @Override
            public void onResponse(Call<BankAccountInfo> call, Response<BankAccountInfo> response) {
                    //Toast.makeText(getBaseContext(), response.body().getId(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), "Nombre: "+response.body().getName(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), "Dinero: "+String.valueOf(response.body().getMoney()), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<BankAccountInfo> call, Throwable t) {
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
    }

    private void addUserToDatabase(){
        User user = new User();
        user.setBlacklist(true);
        user.setEmail("donsufles@gmail.com");
        user.setName("Don Sufles");
        user.setId("1010Android");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService postService = retrofit.create(PostService.class);
        Call<User> call = postService.addUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(getBaseContext(), "Http Status: " + response.code(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Http Status: " + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
