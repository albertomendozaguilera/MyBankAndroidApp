package com.example.mybank.restclient.controllers;

import com.example.mybank.restclient.dto.UserDTO;
import com.example.mybank.restclient.interfaces.GetService;
import com.example.mybank.restclient.interfaces.OnUserInfoResponse;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserController {

    public void getUserInfo(FirebaseAuth mAuth, final OnUserInfoResponse callback){
        mAuth = FirebaseAuth.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService getService = retrofit.create(GetService.class);
        Call<UserDTO> call = getService.getUserFromId(mAuth.getUid());

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
