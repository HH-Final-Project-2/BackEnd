package com.sparta.finalpj.controller.request.member;

import lombok.Getter;

@Getter
public class PasswordFindDto {
    private String email;
    private String code;
    private String password;
    private String passwordCheck;
}
