package com.example.mybank.restclient.interfaces;

import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.dto.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetService {

    @GET("/user/byid")
    Call <UserDTO> getUserFromId(@Query("userId") String userId);

    @GET("/account/allfromuserid")
    Call<List<AccountDTO>> getBankAccountInfo(@Query("userId") String userId);

    @GET("/transactions/myaccount")
    Call<List<PaymentTransactionsDTO>> getPaymentTransactions(@Query("userId") String iban);

    @GET("/paymentways/all")
    Call<List<PaymentWayDTO>> getPaymentWays();
}
