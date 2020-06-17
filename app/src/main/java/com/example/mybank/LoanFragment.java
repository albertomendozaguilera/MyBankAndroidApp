package com.example.mybank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.mybank.restclient.constants.Constants;
import com.example.mybank.restclient.controllers.PaymentWaysController;
import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.LoanDTO;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.OnPaymentWaysResponse;
import com.example.mybank.restclient.interfaces.PostService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoanFragment extends Fragment {

    private EditText etQuantity, etConcept;
    private Spinner accountsSpinner, paymentWaysSpinner;
    private Button btRequest, btSeeAllLoans;
    private LottieAnimationView loadingAnimation;
    private UserDTO user;
    private ArrayList<String> accountsIban, paymentWaysDescription;
    private PaymentWaysController paymentWaysController;
    private ArrayList<PaymentWayDTO> paymentWayDTOS, paymentWayDTOSOUT;
    private int accountIdSpinner, accountId;
    private SharedPreferences preferences;

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

        getSelectedAccountId();

        etQuantity = view.findViewById(R.id.etQuantity);
        etConcept = view.findViewById(R.id.etConcept);
        accountsSpinner = view.findViewById(R.id.accountsSpinner);
        paymentWaysSpinner = view.findViewById(R.id.paymentWaysSpinner);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);

        btRequest = view.findViewById(R.id.btRequest);
        btRequest.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        btRequest.setBackgroundResource(R.drawable.custom_button);
                        btRequest.setTextColor(Color.parseColor("#FFC107"));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        btRequest.setBackgroundResource(R.drawable.custom_button_pressed);
                        btRequest.setTextColor(Color.parseColor("#0B8FBA"));
                        btRequest.setVisibility(View.INVISIBLE);
                        loadingAnimation.playAnimation();
                        loadingAnimation.setRepeatCount(LottieDrawable.INFINITE);
                        addLoan(paymentWaysDescription.get(paymentWaysSpinner.getSelectedItemPosition()));
                        break;
                    }
                }
                return false;
            }
        });

        btSeeAllLoans = view.findViewById(R.id.btSeeAllLoans);
        btSeeAllLoans.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        btSeeAllLoans.setBackgroundResource(R.drawable.custom_button);
                        btSeeAllLoans.setTextColor(Color.parseColor("#FFC107"));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        btSeeAllLoans.setBackgroundResource(R.drawable.custom_button_pressed);
                        btSeeAllLoans.setTextColor(Color.parseColor("#0B8FBA"));
                        Intent i = new Intent(v.getContext().getApplicationContext(), LoansList.class);
                        i.putExtra("user", user);
                        i.putExtra("selectedAccount", accountId);
                        startActivity(i);
                        break;
                    }
                }
                return false;
            }
        });

        paymentWayDTOSOUT = new ArrayList();
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
                    paymentWayDTOSOUT.add(paymentWayDTOItem);
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

    private void addLoan(final String description){
        paymentWaysController.getPaymentWaysInfo(new OnPaymentWaysResponse() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void getPaymentWaysDTO(List<PaymentWayDTO> paymentWayDTO) {
                paymentWayDTOS = (ArrayList<PaymentWayDTO>) paymentWayDTO;

                if (checkFields()){
                    String concept = etConcept.getText().toString();
                    double quantity = Double.parseDouble(etQuantity.getText().toString());

                    for (PaymentWayDTO paymentWayDTOItem : paymentWayDTOS) {
                        if (paymentWayDTOItem.getDescription().equals(description)) {
                            LoanDTO loanDTO = new LoanDTO();

                            UserDTO accountUser = new UserDTO();
                            accountUser.setBlacklist(user.isBlacklist());
                            accountUser.setEmail(user.getEmail());
                            accountUser.setId(user.getId());
                            accountUser.setName(user.getName());

                            getSelectedAccountIdSpinner();

                            user.getAccountsList().get(accountIdSpinner).setUserDTO(accountUser);


                            loanDTO.setAccountDTO(user.getAccountsList().get(accountIdSpinner));
                            loanDTO.setLoanNum(null);
                            loanDTO.setDescription(concept);
                            loanDTO.setPaymentWay(paymentWayDTOItem.getCod());
                            loanDTO.setQuantity(quantity);
                            loanDTO.setReceiptQuantity((((quantity * paymentWayDTOItem.getComission()) / 100) + quantity) / paymentWayDTOItem.getNumReceipts());
                            loanDTO.setReceiptsList(null);

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDateTime now = LocalDateTime.now();
                            loanDTO.setDate(dtf.format(now));


                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(Constants.HEROKU_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            PostService postService = retrofit.create(PostService.class);


                            Call<Void> call = postService.requestLoan(loanDTO);

                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    loadingAnimation.setAnimation(R.raw.check_ok);
                                    loadingAnimation.setRepeatCount(0);
                                    loadingAnimation.playAnimation();
                                    System.out.println("Http Status: " + response.code());
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    loadingAnimation.setAnimation(R.raw.error);
                                    loadingAnimation.setRepeatCount(0);
                                    loadingAnimation.playAnimation();
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_try_later, Toast.LENGTH_LONG).show();
                                    System.out.println("Http Status: " + t.getMessage());
                                }
                            });
                        }
                    }
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

    private boolean checkFields(){
        if (TextUtils.isEmpty(etConcept.getText())){
            Toast.makeText(getActivity().getApplicationContext(), R.string.enter_concept, Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etQuantity.getText())){
            Toast.makeText(getActivity().getApplicationContext(), R.string.enter_quantity, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void getSelectedAccountIdSpinner() {
        for (accountIdSpinner = 0; accountIdSpinner < user.getAccountsList().size(); accountIdSpinner++){
            if (accountsSpinner.getSelectedItem().equals(user.getAccountsList().get(accountIdSpinner).getIban())){
                break;
            }
        }
    }

    private void getSelectedAccountId() {
        for (accountId = 0; accountId < user.getAccountsList().size(); accountId++){
            if (preferences.getString("selectedAccount", "false").equals(user.getAccountsList().get(accountId).getIban())){
                break;
            }
        }
    }
}
