package com.example.batchtransfer.model;

import java.time.LocalDateTime;
import java.util.List;

public class SummaryInfo {
    private String batchNo;
    private String payerWallet;
    private String payerWalletName;
    private String transferTimeFlag;
    private LocalDateTime scheduledProcessingTime;
    private int totalTransactions;
    private double totalAmount;
    private String payerContractNo;
    private String partnerBankCode;
    private String businessType;
    private String businessCategory;
    private List<TransactionInfo> transactions;
    private String environment;
    private String centerFlag;
    private String institutionCode;
    private String fileType;

    // Getters and Setters
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getPayerWallet() { return payerWallet; }
    public void setPayerWallet(String payerWallet) { this.payerWallet = payerWallet; }
    public String getPayerWalletName() { return payerWalletName; }
    public void setPayerWalletName(String payerWalletName) { this.payerWalletName = payerWalletName; }
    public String getTransferTimeFlag() { return transferTimeFlag; }
    public void setTransferTimeFlag(String transferTimeFlag) { this.transferTimeFlag = transferTimeFlag; }
    public LocalDateTime getScheduledProcessingTime() { return scheduledProcessingTime; }
    public void setScheduledProcessingTime(LocalDateTime scheduledProcessingTime) { this.scheduledProcessingTime = scheduledProcessingTime; }
    public int getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getPayerContractNo() { return payerContractNo; }
    public void setPayerContractNo(String payerContractNo) { this.payerContractNo = payerContractNo; }
    public String getPartnerBankCode() { return partnerBankCode; }
    public void setPartnerBankCode(String partnerBankCode) { this.partnerBankCode = partnerBankCode; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(String businessCategory) { this.businessCategory = businessCategory; }
    public List<TransactionInfo> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionInfo> transactions) { this.transactions = transactions; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getCenterFlag() { return centerFlag; }
    public void setCenterFlag(String centerFlag) { this.centerFlag = centerFlag; }
    public String getInstitutionCode() { return institutionCode; }
    public void setInstitutionCode(String institutionCode) { this.institutionCode = institutionCode; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}
