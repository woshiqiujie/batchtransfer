package com.example.batchtransfer.controller;

import com.example.batchtransfer.model.ReceiverInfo;
import com.example.batchtransfer.model.ResponseMessage;
import com.example.batchtransfer.service.ReceiverInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receiver-info")
public class ReceiverInfoController {

    @Autowired
    private ReceiverInfoService service;

    // 新增收款信息
    @PostMapping
    public ResponseEntity<ResponseMessage> addReceiverInfo(@Valid @RequestBody ReceiverInfo receiverInfo, BindingResult result) {
        if (result.hasErrors()) {
            // 如果有字段校验错误，返回 400 错误，并提供详细错误信息
            List<ResponseMessage.FieldError> errorMessages = new ArrayList<>();
            result.getFieldErrors().forEach(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                String rejectedValue = String.valueOf(error.getRejectedValue());
                errorMessages.add(new ResponseMessage.FieldError(field, message, rejectedValue));
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("400", "字段校验失败", errorMessages));
        }

        return service.addReceiverInfo(receiverInfo);
    }


    // 批量删除收款信息
    @PostMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteReceiverInfo(@RequestBody List<String> receiverIds) {
        try {
            // 调用服务层的批量删除方法
            ResponseMessage result = service.deleteReceiverInfos(receiverIds);

            if ("0".equals(result.getStatusCode())) {
                // 删除成功，返回 200
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                // 删除失败，返回 400
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            // 捕获异常并返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("500", "内部服务器错误: " + e.getMessage()));
        }
    }

    // 多条件模糊查询接口，支持分页和排序
    @PostMapping("/search")
    public ResponseEntity<ResponseMessage> searchPaymentWallet(@RequestBody Map<String, String> params) {
        try {
            String transferType = params.getOrDefault("transferType", "");
            String environment = params.getOrDefault("environment", "");
            String receiverId = params.getOrDefault("receiverId", "");
            int page = Integer.parseInt(params.getOrDefault("page", "0")); // 默认页码为0
            int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10")); // 默认每页大小为10

            String sortField = params.getOrDefault("sortField", "createdAt"); // 默认排序字段为 createdAt
            String sortDirection = params.getOrDefault("sortDirection", "DESC"); // 默认排序方向为 DESC

            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
            Pageable pageable = PageRequest.of(page, pageSize, sort);

            Map<String, Object> response = service.searchReceiverInfo(transferType, environment, receiverId, pageable);

            if (response == null) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        new ResponseMessage("500", "内部服务器错误: 响应为空")
                );
            }

            List<ReceiverInfo> results = (List<ReceiverInfo>) response.get("results");

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
