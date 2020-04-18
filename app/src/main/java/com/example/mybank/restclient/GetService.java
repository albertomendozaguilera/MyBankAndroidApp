package com.example.mybank.restclient;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetService {

    @GET("/bankaccountinfo")
    Call<BankAccountInfo> getBankAccountInfo(@Query("user") String user);

}
