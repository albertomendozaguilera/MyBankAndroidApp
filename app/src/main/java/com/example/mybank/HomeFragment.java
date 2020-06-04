package com.example.mybank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybank.restclient.AccountDTO;
import com.example.mybank.restclient.GetService;
import com.example.mybank.restclient.PaymentTransactionsDTO;
import com.example.mybank.restclient.UserDTO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    UserDTO user;
    List <PaymentTransactionsDTO> transactionsList;
    TextView balance, seeAll;
    String iban;
    ConstraintLayout l;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
        }
        //setTexto(""+ user.getName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View frag = inflater.inflate(R.layout.fragment_home, container, false);

        l = frag.findViewById(R.id.homeLayout);
        balance = frag.findViewById(R.id.tvBalance);
        seeAll = frag.findViewById(R.id.seeAll);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext().getApplicationContext(), TransactionsList.class);
                i.putExtra("iban", iban);
                startActivity(i);
            }
        });

        //getBankAccountInfo();

        return frag;
    }

    public void setTexto(String balance){
        this.balance.setText(balance);
    }

    private void getBankAccountInfo(){
        mAuth = FirebaseAuth.getInstance();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GetService getService = retrofit.create(GetService.class);
            Call<List<AccountDTO>> call = getService.getBankAccountInfo(mAuth.getUid());

            call.enqueue(new Callback<List<AccountDTO>>() {
                @Override
                public void onResponse(Call<List<AccountDTO>> call, Response<List<AccountDTO>> response) {
                    if (response.body().size()>0) {
                        l.setForeground(null);
                        setTexto("" + response.body().get(0).getBalance() + "€");
                        iban = response.body().get(0).getIban();

                    }else{
                        getActivity().finish();
                        Toast.makeText(getContext(), "Error al cargar los datos", Toast.LENGTH_LONG).show();
                        System.out.println("IndexOutOfBoundException");
                    }
                }

                @Override
                public void onFailure(Call<List<AccountDTO>> call, Throwable t) {
                    getActivity().finish();
                    Toast.makeText(getContext(), "Nuestros servicios se encuentran en mantenimiento en estos momentos, intentelo de nuevo mas tarde", Toast.LENGTH_LONG).show();
                    System.out.println(t.getCause());
                    System.out.println(t.getMessage());
                }
            });
    }


    /*private void getAccountTransactions(String iban){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<List<PaymentTransactionsDTO>> call = getService.getPaymentTransactions(iban);

        transactionsList = new ArrayList<>();

        call.enqueue(new Callback<List<PaymentTransactionsDTO>>() {
            @Override
            public void onResponse(Call<List<PaymentTransactionsDTO>> call, Response<List<PaymentTransactionsDTO>> response) {
                for (int i = 0; i < response.body().size(); i++) {
                    System.out.println(response.body().get(i).toString());
                    transactionsList.add(response.body().get(i));
                }
            }

            @Override
            public void onFailure(Call<List<PaymentTransactionsDTO>> call, Throwable t) {
                System.out.println(t.getCause());
                System.out.println(t.getMessage());
            }
        });
    }*/
}
