package com.example.batchtransfer.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_wallets")
public class PaymentWallet {

    @Id
    @Column(name = "wallet_id_account", length = 60)
    private String walletIdAccount; // 钱包ID/账户

    @Column(name = "wallet_account_name", length = 60)
    private String walletAccountName; // 钱包/账户名称

    @Column(name = "contract_number", length = 34)
    private String contractNumber; // 签约协议号

    @Column(name = "bank_code", length = 14)
    private String bankCode; // 合作银行编号

    @NotBlank(message = "环境字段不能为空")
    @Pattern(regexp = "01|02", message = "环境字段必须为01-定版 或 02-预演")
    @Column(name = "environment", length = 2, nullable = false)
    private String environment;  // 环境字段，01 或 02，必输

    @Column(name = "remarks", length = 60)
    @Size(max = 60, message = "备注字段不能超过 60 个字符")
    private String remarks;  // 备注字段，非必输

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 创建时间，数据库会自动填充

    @Column(name = "transfer_type", length = 2, nullable = false)
    private String transferType; // 转出方类型 (00-对公钱包, 01-对公账户)

    // Getters and setters
    public String getWalletIdAccount() {
        return walletIdAccount;
    }

    public void setWalletIdAccount(String walletIdAccount) {
        this.walletIdAccount = walletIdAccount;
    }

    public String getWalletAccountName() {
        return walletAccountName;
    }

    public void setWalletAccountName(String walletAccountName) {
        this.walletAccountName = walletAccountName;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
