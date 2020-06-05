package com.example.mybank.restclient.interfaces;

import com.example.mybank.restclient.dto.UserDTO;

public interface OnUserInfoResponse {

    void getUserDTO(UserDTO userDTO);

    void onError(Throwable t);
}
