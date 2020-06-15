package com.example.mybank.restclient.interfaces;

import com.example.mybank.restclient.dto.AccountDTO;
import com.example.mybank.restclient.dto.LoanDTO;
import com.example.mybank.restclient.dto.PaymentTransactionsDTO;
import com.example.mybank.restclient.dto.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostService {

    @POST("/user/adduser")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<UserDTO> addUser(@Body UserDTO userDTO);

    @POST("/loans/addloan")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<Void> requestLoan(@Body LoanDTO loanDTO);

    @POST("/transactions/addtransaction")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<Void> addTransaction(@Body PaymentTransactionsDTO paymentTransactionsDTO);

}
