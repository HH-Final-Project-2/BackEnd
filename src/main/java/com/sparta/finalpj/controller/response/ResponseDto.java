package com.sparta.finalpj.controller.response;

import com.sparta.finalpj.exception.CustomResponseBody;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private boolean success;
    private T data;
//    private Error error;
//    private ErrorCode errorCode;
    private CustomResponseBody error;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data, null);
    }
    public static <T> ResponseDto<T> fail(CustomResponseBody error) {
        return new ResponseDto<>(false, null, error);
    }
//    public static <T> ResponseDto<T> fail(Integer httpStatus, String code, String message) {
//        return new ResponseDto<>(false, null, new ErrorCode(httpStatus,code,message));
//    }
//
//    @Getter
//    @AllArgsConstructor
//    static class ErrorCode {
//        private int httpStatus;
//        private String code;
//        private String message;
//    }
}
