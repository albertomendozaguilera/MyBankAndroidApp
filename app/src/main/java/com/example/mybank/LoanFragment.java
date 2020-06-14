package com.example.mybank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.mybank.restclient.controllers.PaymentWaysController;
import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnPaymentWaysResponse;

import java.util.ArrayList;
import java.util.List;

public class LoanFragment extends Fragment {

    private EditText etQuantity, etMonths;
    private Spinner accountsSpinner, paymentWaysSpinner;
    private Button btSolicitar;
    private UserDTO user;
    private ArrayList<String> accountsIban, paymentWaysDescription;
    private PaymentWaysController paymentWaysController;
    private ArrayList<PaymentWayDTO> paymentWayDTOS;

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
        getPaymentWays(paymentWaysController);
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
        /*btSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                PostService postService = retrofit.create(PostService.class);
                Call<UserDTO> call = postService.requestLoan();

                call.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        Toast.makeText(getActivity().getApplicationContext(), "Http Status: " + response.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        Toast.makeText(getActivity().getApplicationContext(), "Http Status: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });*/

        ArrayAdapter<String> accountsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, accountsIban);
        ArrayAdapter<String> paymentWaysAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, paymentWaysDescription);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        accountsSpinner.setAdapter(accountsAdapter);
        paymentWaysSpinner.setAdapter(paymentWaysAdapter);
        return view;
    }

    private void getPaymentWays(PaymentWaysController paymentWaysController) {
        paymentWaysController.getPaymentWaysInfo(new OnPaymentWaysResponse() {
            @Override
            public void getPaymentWaysDTO(List<PaymentWayDTO> paymentWayDTO) {
                paymentWayDTOS = (ArrayList<PaymentWayDTO>) paymentWayDTO;
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

}
