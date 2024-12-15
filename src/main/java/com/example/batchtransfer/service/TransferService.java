package com.example.batchtransfer.service;

import com.example.batchtransfer.model.BatchTransferSummary;
import com.example.batchtransfer.model.SummaryInfo;
import com.example.batchtransfer.model.TransactionInfo;
import com.example.batchtransfer.repository.BatchTransferSummaryRepository;
import com.example.batchtransfer.sdk.BatchTransferSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // 确保 summaryInfo 不为 null
        if (summaryInfo == null) {
            errors.put("summaryInfo", "SummaryInfo 对象不能为空");
            return errors;
        }

        // 检查批次号是否为空或长度不符合要求
        if (summaryInfo.getBatchNo() == null || summaryInfo.getBatchNo().length() > 36) {
            errors.put("batchNo", "批次号不能为空且必须小于等于36个字符");
        }

        // 检查付款钱包是否为空
        if (summaryInfo.getPayerWallet() == null || summaryInfo.getPayerWallet().isEmpty()) {
            errors.put("payerWallet", "付款钱包不能为空");
        }

        // 检查付款方钱包名称是否为空
        if (summaryInfo.getPayerWalletName() == null || summaryInfo.getPayerWalletName().isEmpty()) {
            errors.put("payerWalletName", "付款方钱包名称不能为空");
        }

        // 检查环境字段是否符合要求
        if (summaryInfo.getEnvironment() == null || (!summaryInfo.getEnvironment().equals("01") && !summaryInfo.getEnvironment().equals("02"))) {
            errors.put("environment", "环境字段只能为 '01' 或 '02'");
        }
        if (summaryInfo.getCenterFlag() == null || summaryInfo.getCenterFlag().isEmpty()) {
            errors.put("centerFlag", "Center Flag 不能为空");
        }
        if (summaryInfo.getPartnerBankCode() == null || summaryInfo.getPartnerBankCode().isEmpty()) {
            errors.put("partnerBankCode", "合作银行代码不能为空");
        }
        if (summaryInfo.getInstitutionCode() == null || summaryInfo.getInstitutionCode().isEmpty()) {
            errors.put("institutionCode", "机构代码不能为空");
        }
        if (summaryInfo.getFileType() == null || summaryInfo.getFileType().isEmpty()) {
            errors.put("fileType", "文件类型不能为空");
        }
        if (summaryInfo.getTransactions() == null) {
            errors.put("transactions", "交易列表不能为空");
        }

        // 校验交易列表是否为空
        if (summaryInfo.getTransactions() == null || summaryInfo.getTransactions().isEmpty()) {
            errors.put("transactions", "交易列表不能为空");
        } else {
            // 校验每一笔交易
            for (TransactionInfo transaction : summaryInfo.getTransactions()) {
                if (transaction == null) {
                    errors.put("transaction", "交易信息不能为空");
                    continue;  // 跳过该笔交易
                }

                // 检查序号是否为空或超长
                if (transaction.getSerialNo() == null || transaction.getSerialNo().length() > 16) {
                    errors.put("serialNo", "交易序号不能为空且长度不能超过16个字符");
                }

                // 检查收款方钱包ID
                if (transaction.getRecipientWalletID() == null || transaction.getRecipientWalletID().length() > 16) {
                    errors.put("recipientWalletID", "收款方钱包ID不能为空且长度不能超过16个字符");
                }

                // 检查收款方用户名称是否为空
                if (transaction.getRecipientUserName() == null || transaction.getRecipientUserName().isEmpty()) {
                    errors.put("recipientUserName", "收款方用户名称不能为空");
                }

                // 检查转款金额是否合理
                if (transaction.getTransferAmount() == 0 || transaction.getTransferAmount() <= 0) {
                    errors.put("transferAmount", "转款金额必须大于0");
                }

                // 检查付款用途
                if (transaction.getPaymentPurpose() == null || transaction.getPaymentPurpose().isEmpty()) {
                    errors.put("paymentPurpose", "付款用途不能为空");
                }

                // 检查用途描述
                if (transaction.getPurposeDescription() != null && transaction.getPurposeDescription().length() > 100) {
                    errors.put("purposeDescription", "用途描述的长度不能超过100个字符");
                }

                // 检查交易附言
                if (transaction.getTransactionNote() != null && transaction.getTransactionNote().length() > 240) {
                    errors.put("transactionNote", "交易附言的长度不能超过240个字符");
                }

                // 检查摘要是否超长
                if (transaction.getSummary() != null && transaction.getSummary().length() > 300) {
                    errors.put("summary", "摘要的长度不能超过300个字符");
                }
            }
        }

        return errors.isEmpty() ? null : errors;
    }



    // 生成批量转账文件并执行转账
    public Map<String, String> generateBatchTransferFile(SummaryInfo summaryInfo) throws IOException {
        // 确保传入的 summaryInfo 对象不为 null，并且正确处理
        if (summaryInfo == null) {
            throw new IllegalArgumentException("SummaryInfo cannot be null");
        }

        // 校验批次号、付款方钱包ID、付款方钱包名称等信息
        Map<String, Object> errors = validateSummaryInfo(summaryInfo);
        if (errors != null && !errors.isEmpty()) {
            throw new IllegalArgumentException("校验错误: " + errors);
        }

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

        System.out.println("Center Flag: " + summaryInfo.getCenterFlag());  // 输出 center_flag 的值


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
        // 设置 creation_time 为当前时间
        summary.setCreationTime(LocalDateTime.now());

        batchTransferSummaryRepository.save(summary);

        Map<String, String> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("batchNo", summaryInfo.getBatchNo());
        result.put("transferStatus", transferSuccess ? "成功" : "失败");

        return result;
    }


     // 根据转出方类型查询钱包信息，并支持分页
    public Map<String, Object> queryBatchTransferSummary(String environment, String payerWallet, String batchNo, String fileType, Pageable pageable) {

        Page<BatchTransferSummary> resultsPage = batchTransferSummaryRepository.findByCriteria(environment, payerWallet, batchNo, fileType, pageable);

        List<BatchTransferSummary> results = resultsPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", resultsPage.getTotalElements());
        response.put("totalPages", resultsPage.getTotalPages());
        response.put("currentPage", resultsPage.getNumber());
        response.put("results", results);

        return response;
    }
}
