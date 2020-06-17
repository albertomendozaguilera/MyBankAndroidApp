package com.example.mybank.restclient.controllers;

import com.example.mybank.restclient.constants.Constants;
import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.GetService;
import com.example.mybank.restclient.interfaces.OnUserInfoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserController {

    public void getUserInfo(String id, final OnUserInfoResponse callback){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HEROKU_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<UserDTO> call = getService.getUserFromId(id);

        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    callback.getUserDTO(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                callback.onError(t);

            }
        });
    }
}
