package com.example.mybank.restclient.dto;


import java.io.Serializable;

public class PaymentTransactionsDTO implements Serializable {
    private String id;
    private AccountDTO accountDTO;
    private double quantity;
    private String concept;
    private String datetime;
    private String destinyAccount;
    private String originAccount;
    private String beneficiary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public void setAccountDTO(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDestinyAccount() {
        return destinyAccount;
    }

    public void setDestinyAccount(String destinyAccount) {
        this.destinyAccount = destinyAccount;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(String originAccount) {
        this.originAccount = originAccount;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    @Override
    public String toString() {
        return "PaymentTransactionsDTO{" +
                "id='" + id + '\'' +
                ", accountDTO='" + accountDTO + '\'' +
                ", quantity=" + quantity +
                ", concept='" + concept + '\'' +
                ", datetime=" + datetime +
                ", destinyAccount='" + destinyAccount + '\'' +
                ", originAccount='" + originAccount + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                '}';
    }
}
