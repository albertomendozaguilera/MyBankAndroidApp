package com.example.mybank.restclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetService {

    @GET("/user/byid")
    Call <List<UserDTO>> getUserFromId(@Query("userId") String userId);

    @GET("/account/allfromuserid")
    Call<List<AccountDTO>> getBankAccountInfo(@Query("userId") String userId);

    @GET("/transactions/myaccount")
    Call<List<PaymentTransactionsDTO>> getPaymentTransactions(@Query("userId") String iban);
}
