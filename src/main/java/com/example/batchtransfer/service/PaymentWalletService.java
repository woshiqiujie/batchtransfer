package com.example.batchtransfer.service;

import com.example.batchtransfer.model.PaymentWallet;
import com.example.batchtransfer.model.ResponseMessage;
import com.example.batchtransfer.repository.PaymentWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentWalletService {

    @Autowired
    private PaymentWalletRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(PaymentWalletService.class);

    // 新增钱包/账户信息
    public ResponseEntity<ResponseMessage> addPaymentWallet(PaymentWallet paymentWallet) {
        try {
            // 校验钱包ID是否为空或长度超过限制
            if (paymentWallet == null || paymentWallet.getWalletIdAccount().isEmpty() || paymentWallet.getWalletIdAccount().length() > 60) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "钱包ID不能为空且长度不能超过60个字符")
                );
            }

            // 校验 contractNumber 长度
            if (paymentWallet.getContractNumber() != null && paymentWallet.getContractNumber().length() > 34) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "签约协议号长度不能超过34个字符")
                );
            }

            // 校验 bankCode 长度
            if (paymentWallet.getBankCode() != null && paymentWallet.getBankCode().length() > 14) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "合作银行编号长度不能超过14个字符")
                );
            }

            // 校验 transferType 是否有效
            if (paymentWallet.getTransferType() == null || !paymentWallet.getTransferType().matches("00|01")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "无效的转出方类型")
                );
            }

            // 校验 environment 字段
            if (paymentWallet.getEnvironment() == null || !paymentWallet.getEnvironment().matches("01|02")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "环境字段必须为 '01' 或 '02'")
                );
            }

            // 校验 remarks 字段长度
            if (paymentWallet.getRemarks() != null && paymentWallet.getRemarks().length() > 60) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "备注长度不能超过60个字符")
                );
            }

            // 设置创建时间为当前时间
            if (paymentWallet.getCreatedAt() == null) {
                paymentWallet.setCreatedAt(LocalDateTime.now());
            }

            // 保存到数据库
            repository.save(paymentWallet);

            // 返回成功的响应
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseMessage("0", "交易成功")
            );

        } catch (Exception e) {
            // 捕获异常，返回500错误
            logger.error("内部服务器错误: {}", e.getMessage(), e); // 添加日志记录
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", "内部服务器错误: " + e.getMessage())
            );
        }
    }

    // 删除钱包信息
    public ResponseMessage deleteMultiplePaymentWallets(List<String> walletIdAccounts) {
        for (String walletIdAccount : walletIdAccounts) {
            Optional<PaymentWallet> wallet = repository.findById(walletIdAccount);
            if (wallet.isPresent()) {
                repository.deleteById(walletIdAccount);
            } else {
                return new ResponseMessage("400", "未找到钱包ID：" + walletIdAccount);
            }
        }
        return new ResponseMessage("0", "删除成功");
    }

    // 根据转出方类型查询钱包信息，并支持分页
    public Map<String, Object> searchPaymentWallet(String transferType, String environment, String walletIdAccount, Pageable pageable) {
        logger.info("Pageable in Service: {}", pageable); // 日志输出 Pageable 参数

        Page<PaymentWallet> resultsPage = repository.findByCriteria(transferType, environment, walletIdAccount, pageable);

        List<PaymentWallet> results = resultsPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", resultsPage.getTotalElements());
        response.put("totalPages", resultsPage.getTotalPages());
        response.put("currentPage", resultsPage.getNumber());
        response.put("results", results);

        return response;
    }
}
