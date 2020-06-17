package com.example.mybank.restclient.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountDTO implements Serializable {
    private UserDTO userDTO;
    private String iban;
    private String name;
    private double balance;
    private ArrayList<PaymentTransactionsDTO> transactionsDTOList;
    private ArrayList<LoanDTO> loansDTOList;

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<PaymentTransactionsDTO> getTransactionsDTOList() {
        return transactionsDTOList;
    }

    public void setTransactionsDTOList(ArrayList<PaymentTransactionsDTO> transactionsDTOList) {
        this.transactionsDTOList = transactionsDTOList;
    }

    public ArrayList<LoanDTO> getLoansDTOList() {
        return loansDTOList;
    }

    public void setLoansDTOList(ArrayList<LoanDTO> loansDTOList) {
        this.loansDTOList = loansDTOList;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "userDTO=" + userDTO +
                ", iban='" + iban + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
