package com.example.batchtransfer.controller;

import com.example.batchtransfer.model.BatchTransferSummary;
import com.example.batchtransfer.model.QueryRequest;
import com.example.batchtransfer.model.ResponseMessage;
import com.example.batchtransfer.service.TransferService;
import com.example.batchtransfer.model.SummaryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    // POST 批量转账处理接口
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> processBatchTransfer(@RequestBody SummaryInfo summaryInfo) {
        try {
            Map<String, String> fileData = transferService.generateBatchTransferFile(summaryInfo);

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", "0");
            response.put("message", "交易成功");
            response.put("data", fileData);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "1");
            errorResponse.put("message", "输入数据错误: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            // 打印堆栈信息，帮助调试
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "1");
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


       // 查询批量交易记录，并支持翻页
    @PostMapping("/query")
    public ResponseEntity<ResponseMessage> searchPaymentWallet(@RequestBody Map<String, String> params) {
        try {

            String environment = params.getOrDefault("environment", "");
           String payerWallet = params.getOrDefault("payerWallet", "");
            String batchNo = params.getOrDefault("batchNo", "");
            String fileType = params.getOrDefault("fileType", "");

            int page = Integer.parseInt(params.getOrDefault("page", "0")); // 默认页码为0
            int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10")); // 默认每页大小为10

            String sortField = params.getOrDefault("sortField", "creationTime"); // 默认排序字段为 creationTime
            String sortDirection = params.getOrDefault("sortDirection", "DESC"); // 默认排序方向为 DESC

            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
            Pageable pageable = PageRequest.of(page, pageSize, sort);

            Map<String, Object> response = transferService.queryBatchTransferSummary(environment, payerWallet, batchNo, fileType,pageable);

            if (response == null) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ResponseMessage("500", "内部服务器错误: 响应为空")
                );
            }

            List<BatchTransferSummary> results = (List<BatchTransferSummary>) response.get("results");

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
