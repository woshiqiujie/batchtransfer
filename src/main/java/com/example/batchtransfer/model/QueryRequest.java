package com.example.batchtransfer.model;

import java.time.LocalDate;

public class QueryRequest {

    private String environment;
    private String batchNo;
    private String payerWallet;
    private String fileType;

    // 使用 LocalDate 类型接收日期
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters and setters
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getPayerWallet() {
        return payerWallet;
    }

    public void setPayerWallet(String payerWallet) {
        this.payerWallet = payerWallet;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
