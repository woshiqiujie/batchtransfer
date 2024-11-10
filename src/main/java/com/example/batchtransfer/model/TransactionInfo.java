package com.example.batchtransfer.model;

public class TransactionInfo {
    private String serialNo;
    private String recipientWalletID;
    private String recipientWalletName;
    private String recipientUserName;
    private double transferAmount;
    private String paymentPurpose;
    private String purposeDescription;
    private String transactionNote;
    private String summary;

    // Getters and Setters
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getRecipientWalletID() {
        return recipientWalletID;
    }

    public void setRecipientWalletID(String recipientWalletID) {
        this.recipientWalletID = recipientWalletID;
    }

    public String getRecipientWalletName() {
        return recipientWalletName;
    }

    public void setRecipientWalletName(String recipientWalletName) {
        this.recipientWalletName = recipientWalletName;
    }

    public String getRecipientUserName() {
        return recipientUserName;
    }

    public void setRecipientUserName(String recipientUserName) {
        this.recipientUserName = recipientUserName;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getPaymentPurpose() {
        return paymentPurpose;
    }

    public void setPaymentPurpose(String paymentPurpose) {
        this.paymentPurpose = paymentPurpose;
    }

    public String getPurposeDescription() {
        return purposeDescription;
    }

    public void setPurposeDescription(String purposeDescription) {
        this.purposeDescription = purposeDescription;
    }

    public String getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        this.transactionNote = transactionNote;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
