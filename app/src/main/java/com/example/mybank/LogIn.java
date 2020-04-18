package com.example.mybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LogIn extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    //private TextView tvLogout;
    private ImageView btFingerprint;
    private Button login;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        //tvLogout = findViewById(R.id.tvLogout);
        //tvLogout.setOnClickListener(this);
        login = findViewById(R.id.btLogin);
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "succed", Toast.LENGTH_LONG).show();
                        }
                    });
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
                                            //guarda el nuevo usuario en mysql
                                            String url = "http://iesayala.ddns.net/albertomendoza/php/insertarcontacto.php/?id=" + mAuth.getCurrentUser().getUid() + "&email=" + etEmail.getText() + "&password=" + etPassword.getText();

                                            cargarUrl(url);

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

    public void cargarUrl(String url){
        try {
            URL u = new URL("");
            u.openStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()){
            case R.id.tvLogout:
                mAuth.signOut();
                break;
        }*/
    }
}
