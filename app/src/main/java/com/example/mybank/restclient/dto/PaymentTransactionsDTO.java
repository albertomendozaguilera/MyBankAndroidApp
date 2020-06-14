package com.example.mybank.restclient.dto;


import java.io.Serializable;

public class PaymentTransactionsDTO implements Serializable {
    String id;
    String iban;
    double quantity;
    String concept;
    String datetime;
    String destinyAccount;
    String originAccount;
    String beneficiary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
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
                ", iban='" + iban + '\'' +
                ", quantity=" + quantity +
                ", concept='" + concept + '\'' +
                ", datetime=" + datetime +
                ", destinyAccount='" + destinyAccount + '\'' +
                ", originAccount='" + originAccount + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                '}';
    }
}
