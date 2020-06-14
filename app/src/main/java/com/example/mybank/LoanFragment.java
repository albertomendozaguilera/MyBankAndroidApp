package com.example.mybank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.mybank.restclient.controllers.PaymentWaysController;
import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnPaymentWaysResponse;
import com.example.mybank.restclient.interfaces.PostService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoanFragment extends Fragment {

    private EditText etQuantity, etMonths;
    private Spinner accountsSpinner, paymentWaysSpinner;
    private Button btSolicitar;
    private UserDTO user;
    private ArrayList<String> accountsIban, paymentWaysDescription;
    private PaymentWaysController paymentWaysController;
    private ArrayList<PaymentWayDTO> paymentWayDTOS;
    int accountId;
    SharedPreferences preferences;

    public LoanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = (UserDTO) getArguments().getSerializable("user");
        }
        accountsIban = new ArrayList();
        for (AccountDTO account : user.getAccountsList()){
            accountsIban.add(account.getIban());
        }

        paymentWaysController = new PaymentWaysController();
        paymentWaysDescription = new ArrayList();

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loan, container, false);
        etQuantity = view.findViewById(R.id.etQuantity);
        etMonths = view.findViewById(R.id.etMonths);
        accountsSpinner = view.findViewById(R.id.accountsSpinner);
        paymentWaysSpinner = view.findViewById(R.id.paymentWaysSpinner);
        btSolicitar = view.findViewById(R.id.btSolicitar);
        btSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                PostService postService = retrofit.create(PostService.class);

                getSelectedAccountId();

                Call<Void> call = postService.requestLoan(user.getAccountsList().get(accountId));

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getActivity().getApplicationContext(), "Http Status: " + response.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getActivity().getApplicationContext(), "Http Status: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        getPaymentWays(paymentWaysController);
        ArrayAdapter<String> accountsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, accountsIban);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        accountsSpinner.setAdapter(accountsAdapter);
        return view;
    }

    private void getPaymentWays(PaymentWaysController paymentWaysController) {
        paymentWaysController.getPaymentWaysInfo(new OnPaymentWaysResponse() {
            @Override
            public void getPaymentWaysDTO(List<PaymentWayDTO> paymentWayDTO) {
                paymentWayDTOS = (ArrayList<PaymentWayDTO>) paymentWayDTO;
                for(PaymentWayDTO paymentWayDTOItem : paymentWayDTOS){
                    paymentWaysDescription.add(paymentWayDTOItem.getDescription());
                }
                ArrayAdapter<String> paymentWaysAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, paymentWaysDescription);
                paymentWaysSpinner.setAdapter(paymentWaysAdapter);


            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }

}
