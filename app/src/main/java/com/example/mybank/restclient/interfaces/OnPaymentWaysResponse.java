package com.example.mybank.restclient.interfaces;

import com.example.mybank.restclient.dto.PaymentWayDTO;

import java.util.List;

public interface OnPaymentWaysResponse {

    void getPaymentWaysDTO(List<PaymentWayDTO> paymentWayDTO);

    void onError(Throwable t);
}
