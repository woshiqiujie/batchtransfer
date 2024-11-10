package com.example.batchtransfer.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "batch_transfer_summary")
public class BatchTransferSummary {

    @Id
    @Column(name = "batch_no", length = 35)
    private String batchNo; // 批次号，主键

    @Column(name = "payer_wallet", length = 16, nullable = false)
    private String payerWallet; // 付款方钱包

    @Column(name = "payer_wallet_name", length = 60, nullable = false)
    private String payerWalletName; // 付款方钱包名称

    @Column(name = "total_transactions", nullable = false)
    private int totalTransactions; // 总交易笔数

    @Column(name = "total_amount", precision = 22, scale = 2, nullable = false)
    private BigDecimal totalAmount; // 总金额

    @Column(name = "payer_contract_no", length = 34, nullable = false)
    private String payerContractNo; // 付款方签约协议号

    @Column(name = "partner_bank_code", length = 14, nullable = false)
    private String partnerBankCode; // 合作银行编号

    @Column(name = "business_type", length = 3, nullable = false)
    private String businessType; // 业务类型

    @Column(name = "business_category", length = 8, nullable = false)
    private String businessCategory; // 业务种类

    @Column(name = "environment", length = 2, nullable = false)
    private String environment; // 环境字段

    @Column(name = "center_flag", length = 2, nullable = false)
    private String centerFlag; // 中心标志

    @Column(name = "file_type", length = 2, nullable = false)
    private String fileType; // 文件类型

    @Column(name = "file_name")
    private String fileName; // 文件名称

    @Column(name = "resp_status", length = 1)
    private String respStatus = "3"; // 文件处理状态，默认为处理中

    // 修改为 LocalDate 类型，用于存储当前日期
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate = LocalDate.now(); // 当前日期，类型为 LocalDate

    // 修改为 LocalTime 类型，用于存储当前时间，精确到分钟
    @Column(name = "creation_time", nullable = false)
    private LocalTime creationTime = LocalTime.now().withSecond(0).withNano(0); // 当前时间，类型为 LocalTime，精确到分钟

    // Getters and setters

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

    public String getPayerWalletName() {
        return payerWalletName;
    }

    public void setPayerWalletName(String payerWalletName) {
        this.payerWalletName = payerWalletName;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayerContractNo() {
        return payerContractNo;
    }

    public void setPayerContractNo(String payerContractNo) {
        this.payerContractNo = payerContractNo;
    }

    public String getPartnerBankCode() {
        return partnerBankCode;
    }

    public void setPartnerBankCode(String partnerBankCode) {
        this.partnerBankCode = partnerBankCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCenterFlag() {
        return centerFlag;
    }

    public void setCenterFlag(String centerFlag) {
        this.centerFlag = centerFlag;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRespStatus() {
        return respStatus;
    }

    public void setRespStatus(String respStatus) {
        this.respStatus = respStatus;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }
}
