package com.example.mybank.restclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetService {

    @GET("/account/allfromuserid")
    Call<List<AccountDTO>> getBankAccountInfo(@Query("userId") String userId);

}
