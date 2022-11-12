package com.sparta.finalpj.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomResponseBody {
    private int httpStatus;
    private String code;
    private String message;

    public CustomResponseBody(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
//    public CustomResponseBody(ErrorCode statusCode, Object data){
//        this.statusCode = statusCode.getStatusCode();
//        this.statusMsg = statusCode.getStatusMsg();
//        this.data = data;
//    }
}