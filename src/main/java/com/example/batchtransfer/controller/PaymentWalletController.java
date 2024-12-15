package com.example.batchtransfer.controller;

import com.example.batchtransfer.model.PaymentWallet;
import com.example.batchtransfer.model.ResponseMessage;
import com.example.batchtransfer.service.PaymentWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-wallets")
public class PaymentWalletController {

    @Autowired
    private PaymentWalletService service;

    // 新增钱包/账户信息
    @PostMapping
    public ResponseEntity<ResponseMessage> addPaymentWallet(@RequestBody PaymentWallet paymentWallet) {
        try {
            // 校验环境字段是否为空并有效
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

            // 调用服务层的新增方法，直接返回服务层的响应
            return service.addPaymentWallet(paymentWallet);
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常并返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("500", "内部服务器错误: " + e.getMessage()));
        }
    }

    // 删除指定的钱包信息
    @PostMapping("/delete-multiple")
    public ResponseEntity<ResponseMessage> deleteMultiplePaymentWallets(@RequestBody List<String> walletIdAccounts) {
        try {
            // 调用服务层的批量删除方法
            ResponseMessage result = service.deleteMultiplePaymentWallets(walletIdAccounts);

            if ("0".equals(result.getStatusCode())) {
                // 删除成功
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                // 删除失败，返回失败原因
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常并返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("500", "内部服务器错误: " + e.getMessage()));
        }
    }

   // 查询钱包/账户信息，根据转出方类型 (transferType)，并支持翻页
    @PostMapping("/search")
    public ResponseEntity<ResponseMessage> searchPaymentWallet(@RequestBody Map<String, String> params) {
        try {
            String transferType = params.getOrDefault("transferType", "");
            String environment = params.getOrDefault("environment", "");
            String walletIdAccount = params.getOrDefault("walletIdAccount", "");
            int page = Integer.parseInt(params.getOrDefault("page", "0")); // 默认页码为0
            int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10")); // 默认每页大小为10

            String sortField = params.getOrDefault("sortField", "createdAt"); // 默认排序字段为 createdAt
            String sortDirection = params.getOrDefault("sortDirection", "DESC"); // 默认排序方向为 DESC

            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
            Pageable pageable = PageRequest.of(page, pageSize, sort);

            Map<String, Object> response = service.searchPaymentWallet(transferType, environment, walletIdAccount, pageable);

            if (response == null) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ResponseMessage("500", "内部服务器错误: 响应为空")
                );
            }

            List<PaymentWallet> results = (List<PaymentWallet>) response.get("results");

            if (results == null || results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("404", "未找到符合条件的钱包信息"));
            }

            return ResponseEntity.ok(new ResponseMessage("0", "查询成功", response));

        } catch (NumberFormatException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseMessage("400", "页码或每页大小参数格式不正确")
            );
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", "内部服务器错误: " + e.getMessage())
            );
        }
    }

}
