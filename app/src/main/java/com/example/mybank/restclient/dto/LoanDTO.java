package com.example.mybank.restclient.dto;

import java.util.ArrayList;

public class LoanDTO {
    private String loanNum;
    private AccountDTO accountDTO;
    private String date;
    private String description;
    private double quantity;
    private double receiptQuantity;
    private String paymentWay;
    private ArrayList<ReceiptDTO> receiptsList;

    public String getLoanNum() {
        return loanNum;
    }

    public void setLoanNum(String loanNum) {
        this.loanNum = loanNum;
    }

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public void setAccountDTO(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getReceiptQuantity() {
        return receiptQuantity;
    }

    public void setReceiptQuantity(double receiptQuantity) {
        this.receiptQuantity = receiptQuantity;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
    }

    public ArrayList<ReceiptDTO> getReceiptsList() {
        return receiptsList;
    }

    public void setReceiptsList(ArrayList<ReceiptDTO> receiptsList) {
        this.receiptsList = receiptsList;
    }
}
