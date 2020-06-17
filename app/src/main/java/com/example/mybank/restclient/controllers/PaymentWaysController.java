package com.example.mybank.restclient.controllers;

import com.example.mybank.restclient.constants.Constants;
import com.example.mybank.restclient.dto.PaymentWayDTO;
import com.example.mybank.restclient.interfaces.GetService;
import com.example.mybank.restclient.interfaces.OnPaymentWaysResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentWaysController {
    public void getPaymentWaysInfo(final OnPaymentWaysResponse callback){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HEROKU_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call <List<PaymentWayDTO>> call = getService.getPaymentWays();

        call.enqueue(new Callback<List<PaymentWayDTO>>() {
            @Override
            public void onResponse(Call<List<PaymentWayDTO>> call, Response<List<PaymentWayDTO>> response) {
                if (response.isSuccessful()) {
                    callback.getPaymentWaysDTO(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<PaymentWayDTO>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
