package com.example.batchtransfer.model;

import java.util.List;

public class ResponseMessage {

    private String statusCode;  // 状态码
    private String message;     // 消息内容
    private Object data;        // 数据，可以是任何类型

    // Constructor with data field (for query results or detailed error messages)
    public ResponseMessage(String statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    // Constructor without data field (for simple messages or status codes)
    public ResponseMessage(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = null; // 默认没有数据
    }

    // Constructor for validation errors (data will contain a List of errors)
    public ResponseMessage(String statusCode, String message, List<FieldError> errorMessages) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = errorMessages; // 错误信息列表
    }

    // Getters and Setters
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // 简化后的字段错误类
    public static class FieldError {
        private String field; // 错误字段
        private String message; // 错误消息
        private String rejectedValue; // 被拒绝的值

        public FieldError(String field, String message, String rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        // Getters and Setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(String rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}
