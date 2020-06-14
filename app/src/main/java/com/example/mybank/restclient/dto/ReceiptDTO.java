package com.example.mybank.restclient.dto;

public class ReceiptDTO {
    private AccountDTO accountDTO;
    private LoanDTO loanDTO;
    private int receiptNum;
    private String receiptDate;
    private double receiptAmount;
    private String paymentDate;
    private String payed;

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public void setAccountDTO(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    public LoanDTO getLoanDTO() {
        return loanDTO;
    }

    public void setLoanDTO(LoanDTO loanDTO) {
        this.loanDTO = loanDTO;
    }

    public int getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(int receiptNum) {
        this.receiptNum = receiptNum;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public double getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPayed() {
        return payed;
    }

    public void setPayed(String payed) {
        this.payed = payed;
    }
}
