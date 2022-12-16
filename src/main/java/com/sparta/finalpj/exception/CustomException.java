package com.sparta.finalpj.exception;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class CustomException extends RuntimeException implements Supplier<ErrorCode> {
    private final ErrorCode errorCode;


    @Override
    public ErrorCode get() {
        return errorCode;
    }

    public CustomException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }

}
