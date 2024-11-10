package com.example.batchtransfer.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "receiver_info")
public class ReceiverInfo {

    // 收款钱包ID/手机号/身份证号（长度校验）
    @Id
    @Column(name = "receiver_id", length = 19)
    @Size(min = 1, max = 19, message = "收款ID长度必须为1到19个字符")
    private String receiverId;

    // 用户名称（长度校验）
    @Column(name = "user_name", length = 60)
    @Size(min = 1, max = 60, message = "用户名称长度必须为1到60个字符")
    private String userName;

    // 转入方类型 (01-钱包代发；02-手机号代发；03-身份证代发)
    @Column(name = "transfer_type", length = 2, nullable = false)
    @Pattern(regexp = "01|02|03", message = "转入方类型必须为 01、02 或 03")
    private String transferType;

    // 环境字段，01 或 02，必输
    @NotBlank(message = "环境字段不能为空")
    @Pattern(regexp = "01|02", message = "环境字段必须为01-定版 或 02-预演")
    @Column(name = "environment", length = 2, nullable = false)
    private String environment;

    // 备注字段，非必输
    @Column(name = "remarks", length = 60)
    @Size(max = 60, message = "备注字段不能超过 60 个字符")
    private String remarks;

    // 创建时间，数据库会自动填充
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 在插入数据之前自动设置创建时间
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
}
