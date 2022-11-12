package com.sparta.finalpj.exception;

public class EmptyMultipartFileException extends RuntimeException {
  public EmptyMultipartFileException() {
    super("multipart file is empty");
  }
}
