package com.example.batchtransfer.controller;

import com.example.batchtransfer.model.ReceiverInfo;
import com.example.batchtransfer.model.ResponseMessage;
import com.example.batchtransfer.service.ReceiverInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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
    // 多条件模糊查询接口
    @PostMapping("/search")
    public ResponseEntity<ResponseMessage> searchReceiverInfo(@RequestBody Map<String, String> searchCriteria) {
        try {
            // 从请求体中提取查询条件
            String transferType = searchCriteria.get("transferType");
            String environment = searchCriteria.get("environment");
            String receiverId = searchCriteria.get("receiverId");

            // 执行查询并返回结果
            List<ReceiverInfo> result = service.searchReceiverInfo(transferType, environment, receiverId);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage("404", "未找到符合条件的收款信息")
                );
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", "查询成功", result)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("500", "内部服务器错误: " + e.getMessage()));
        }
    }
}
