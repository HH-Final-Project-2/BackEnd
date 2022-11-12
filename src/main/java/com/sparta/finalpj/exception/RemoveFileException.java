package com.sparta.finalpj.exception;

public class RemoveFileException extends RuntimeException {
  public RemoveFileException() {
    super("fail to remove target file");
  }
}
