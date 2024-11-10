package com.example.batchtransfer.service;

import com.example.batchtransfer.model.BatchTransferSummary;
import com.example.batchtransfer.model.SummaryInfo;
import com.example.batchtransfer.model.TransactionInfo;
import com.example.batchtransfer.repository.BatchTransferSummaryRepository;
import com.example.batchtransfer.sdk.BatchTransferSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransferService {

    @Value("${batchtransfer.output-directory}")
    private String baseDirectory;

    private static final String DELIMITER = "|";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    @Autowired
    private BatchTransferSummaryRepository batchTransferSummaryRepository;

    // 校验 SummaryInfo 对象中的数据
    public Map<String, Object> validateSummaryInfo(SummaryInfo summaryInfo) {
        Map<String, Object> errors = new HashMap<>();

        // 校验环境字段
        if (!summaryInfo.getEnvironment().matches("01|02")) {
            errors.put("environment", "环境字段必须为 01 或 02");
        }
        // 校验中心标志
        if (!summaryInfo.getCenterFlag().matches("01|02")) {
            errors.put("centerFlag", "中心标志必须为 01 或 02");
        }
        // 校验合作银行编号
        if (summaryInfo.getPartnerBankCode().length() > 14) {
            errors.put("partnerBankCode", "合作银行编号长度不能超过 14 个字符");
        }
        // 校验机构代码
        if (summaryInfo.getInstitutionCode() == null || !summaryInfo.getInstitutionCode().matches("\\d{3}")) {
            throw new IllegalArgumentException("机构代码必须为 3 个数字");
        }
        // 校验文件类型
        if (!summaryInfo.getFileType().matches("03|07")) {
            errors.put("fileType", "文件类型必须为 03 或 07");
        }

        // 校验每一笔交易
        for (TransactionInfo transaction : summaryInfo.getTransactions()) {
            if (transaction.getSerialNo() == null || transaction.getSerialNo().length() > 16) {
                errors.put("serialNo", "序号长度不能超过 16 个字符");
            }
            if (transaction.getRecipientWalletID() == null || transaction.getRecipientWalletID().length() > 16) {
                errors.put("recipientWalletID", "收款方钱包ID 长度不能超过 16 个字符");
            }
            if (transaction.getRecipientUserName() == null || transaction.getRecipientUserName().length() > 60) {
                errors.put("recipientUserName", "收款方用户名称 长度不能超过 60 个字符");
            }
            if (transaction.getTransferAmount() == 0 || transaction.getTransferAmount() < 0) {
                errors.put("transferAmount", "转款金额为必填项，且必须大于零");
            }
            if (transaction.getPaymentPurpose() == null || transaction.getPaymentPurpose().length() > 4) {
                errors.put("paymentPurpose", "付款用途 长度不能超过 4 个字符");
            }
        }

        return errors.isEmpty() ? null : errors;
    }

    // 生成批量转账文件并执行转账
    public Map<String, String> generateBatchTransferFile(SummaryInfo summaryInfo) throws IOException {
        // 校验批次号、付款方钱包ID、付款方钱包名称等信息
        validateSummaryInfo(summaryInfo);

        // 生成文件名
        String fileName = String.format("INTERBANK_TRANSFER_%s_0000_%s_I_%04d_%04d.txt",
                summaryInfo.getPartnerBankCode(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                (int) (Math.random() * 10000),
                (int) (Math.random() * 10000)
        );

        // 生成文件路径
        File file = new File(baseDirectory + fileName);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("无法创建文件目录");
            }
        }

        // 写入文件内容
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // 写入文件头部
            String headerLine = String.join(DELIMITER,
                    summaryInfo.getBatchNo(),
                    summaryInfo.getPayerWallet(),
                    summaryInfo.getPayerWalletName(),
                    summaryInfo.getTransferTimeFlag(),
                    summaryInfo.getScheduledProcessingTime() != null ?
                            summaryInfo.getScheduledProcessingTime().format(DATE_TIME_FORMATTER) : "",
                    String.valueOf(summaryInfo.getTotalTransactions()),
                    DECIMAL_FORMAT.format(summaryInfo.getTotalAmount()),
                    summaryInfo.getPayerContractNo(),
                    summaryInfo.getPartnerBankCode(),
                    summaryInfo.getBusinessType(),
                    summaryInfo.getBusinessCategory()
            );
            writer.write(headerLine);
            writer.newLine();

            // 写入交易记录
            List<TransactionInfo> transactions = summaryInfo.getTransactions();
            for (TransactionInfo transaction : transactions) {
                String transactionLine = String.join(DELIMITER,
                        transaction.getSerialNo(),
                        "01", // 默认代发方式
                        transaction.getRecipientWalletID(),
                        transaction.getRecipientWalletName() != null ? transaction.getRecipientWalletName() : "",
                        transaction.getRecipientUserName(),
                        DECIMAL_FORMAT.format(transaction.getTransferAmount()),
                        transaction.getPaymentPurpose(),
                        transaction.getPurposeDescription() != null ? transaction.getPurposeDescription() : "",
                        transaction.getTransactionNote() != null ? transaction.getTransactionNote() : "",
                        transaction.getSummary() != null ? transaction.getSummary() : ""
                );
                writer.write(transactionLine);
                writer.newLine();
            }
        }

        // 调用批量转账 SDK 执行转账
        BatchTransferSDK sdk = new BatchTransferSDK();
        boolean transferSuccess = sdk.executeBatchTransfer(
                summaryInfo.getEnvironment(),
                summaryInfo.getCenterFlag(),
                summaryInfo.getPartnerBankCode(),
                summaryInfo.getInstitutionCode(),
                summaryInfo.getFileType(),
                file.getPath()
        );

        // 保存汇总信息到数据库
        BatchTransferSummary summary = new BatchTransferSummary();
        summary.setBatchNo(summaryInfo.getBatchNo());
        summary.setPayerWallet(summaryInfo.getPayerWallet());
        summary.setPayerWalletName(summaryInfo.getPayerWalletName());
        summary.setTotalTransactions(summaryInfo.getTotalTransactions());
        summary.setTotalAmount(BigDecimal.valueOf(summaryInfo.getTotalAmount()));
        summary.setPayerContractNo(summaryInfo.getPayerContractNo());
        summary.setPartnerBankCode(summaryInfo.getPartnerBankCode());
        summary.setBusinessType(summaryInfo.getBusinessType());
        summary.setBusinessCategory(summaryInfo.getBusinessCategory());
        summary.setEnvironment(summaryInfo.getEnvironment());
        summary.setCenterFlag(summaryInfo.getCenterFlag());
        summary.setFileType(summaryInfo.getFileType());
        summary.setFileName(file.getName());

        batchTransferSummaryRepository.save(summary);

        Map<String, String> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("batchNo", summaryInfo.getBatchNo());
        result.put("transferStatus", transferSuccess ? "成功" : "失败");

        return result;
    }

    // 查询批量转账汇总信息
    public List<BatchTransferSummary> queryBatchTransferSummary(String environment, String batchNo, String payerWallet, String fileType) {
        // 默认查询所有，如果某个条件为空则忽略
        return batchTransferSummaryRepository.queryBatchTransferSummary(
                environment == null ? "" : environment,
                batchNo == null ? "" : batchNo,
                payerWallet == null ? "" : payerWallet,
                fileType == null ? "" : fileType
        );
    }

}
