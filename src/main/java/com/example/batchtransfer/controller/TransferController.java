package com.example.batchtransfer.controller;

import com.example.batchtransfer.model.BatchTransferSummary;
import com.example.batchtransfer.model.QueryRequest;
import com.example.batchtransfer.service.TransferService;
import com.example.batchtransfer.model.SummaryInfo;
import org.springframework.beans.factory.annotation.Autowired;
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
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "1");
            errorResponse.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // POST 查询接口，支持条件查询，返回 Map 类型
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryBatchTransferSummary(@RequestBody Map<String, String> queryRequest) {
        try {
            String environment = queryRequest.get("environment");
            String batchNo = queryRequest.get("batchNo");
            String payerWallet = queryRequest.get("payerWallet");
            String fileType = queryRequest.get("fileType");

            List<BatchTransferSummary> batchTransferSummaries = transferService.queryBatchTransferSummary(
                    environment, batchNo, payerWallet, fileType
            );

            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", "0");
            response.put("message", "查询成功");
            response.put("data", batchTransferSummaries); // 返回查询结果

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", "1");
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}
