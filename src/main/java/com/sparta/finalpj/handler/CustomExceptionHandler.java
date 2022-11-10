package com.sparta.finalpj.handler;

import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.CustomResponseBody;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.ResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
  private ErrorCode errorCode;
  private CustomException customException;


//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  public ResponseDto<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
//    String errorMessage = exception.getBindingResult()
//        .getAllErrors()
//        .get(0)
//        .getDefaultMessage();
//
//    return ResponseDto.fail("BAD_REQUEST", errorMessage);
//  }

//  @ExceptionHandler({CustomException.class})
//  public ResponseDto<?> customExceptionHandler() {
//    return ResponseDto.fail(
//            customException.get()
//            );
//  }
      @ExceptionHandler({CustomException.class})
      public ResponseDto<?> customExceptionHandler(CustomException e) {
        int errHttpStatus = e.get().getHttpStatus();
        String errCode = e.get().getCode();
        String errMessage = e.get().getMessage();
        CustomResponseBody customResponseBody = new CustomResponseBody();
        customResponseBody.setHttpStatus(errHttpStatus);
        customResponseBody.setCode(errCode);
        customResponseBody.setMessage(errMessage);
           return ResponseDto.fail(
                   customResponseBody
      );
}
//  private int httpStatus;
//  private String code;
//  private String message;

}
