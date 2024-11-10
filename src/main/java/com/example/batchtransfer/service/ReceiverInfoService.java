package com.example.batchtransfer.service;

import com.example.batchtransfer.model.ReceiverInfo;
import com.example.batchtransfer.model.ResponseMessage;
import com.example.batchtransfer.repository.ReceiverInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiverInfoService {

    @Autowired
    private ReceiverInfoRepository repository;

    // 新增收款信息
    public ResponseEntity<ResponseMessage> addReceiverInfo(@Valid ReceiverInfo receiverInfo) {
        try {
            // 校验收款ID不能为空
            if (receiverInfo.getReceiverId() == null || receiverInfo.getReceiverId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "收款ID不能为空")
                );
            }

            // 校验转入方类型是否有效
            if (!receiverInfo.getTransferType().matches("01|02|03")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "无效的转入方类型")
                );
            }

            // 校验用户名不能为空
            if (receiverInfo.getUserName() == null || receiverInfo.getUserName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "用户名不能为空")
                );
            }
            // 校验环境类型是否有效
            if (!receiverInfo.getEnvironment().matches("01|02|03")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseMessage("400", "无效的环境类型")
                );
            }




            // 保存收款信息
            repository.save(receiverInfo);

            // 返回成功响应
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseMessage("0", "交易成功")
            );
        } catch (Exception e) {
            // 捕获异常，返回500错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", "内部服务器错误: " + e.getMessage())
            );
        }
    }

    // 批量删除收款信息
    public ResponseMessage deleteReceiverInfos(List<String> receiverIds) {
        try {
            // 遍历 receiverIds 删除每个对应的收款信息
            for (String receiverId : receiverIds) {
                Optional<ReceiverInfo> receiverInfo = repository.findById(receiverId);
                if (receiverInfo.isPresent()) {
                    repository.deleteById(receiverId);
                } else {
                    // 如果某个 receiverId 找不到，返回错误
                    return new ResponseMessage("400", "未找到收款信息: " + receiverId);
                }
            }

            // 返回成功消息
            return new ResponseMessage("0", "删除成功");
        } catch (Exception e) {
            // 捕获异常并返回 500 错误
            return new ResponseMessage("500", "内部服务器错误: " + e.getMessage());
        }
    }

    // 根据转入方类型查询
    // 多条件模糊查询
    public List<ReceiverInfo> searchReceiverInfo(String transferType, String environment, String receiverId) {
        // 根据条件执行模糊查询
        return repository.findByCriteria(
                transferType.isEmpty() ? null : transferType,
                environment.isEmpty() ? null : environment,
                receiverId.isEmpty() ? null : receiverId
        );
    }
}
