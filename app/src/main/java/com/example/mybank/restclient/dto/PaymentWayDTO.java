package com.example.mybank.restclient.dto;

public class PaymentWayDTO {
    private String cod;
    private String description;
    private int numReceipts;
    private double comission;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumReceipts() {
        return numReceipts;
    }

    public void setNumReceipts(int numReceipts) {
        this.numReceipts = numReceipts;
    }

    public double getComission() {
        return comission;
    }

    public void setComission(double comission) {
        this.comission = comission;
    }
}
