package com.sparta.finalpj.controller.request.member;

import lombok.Getter;

import java.util.List;


@Getter
public class SignupRequestDto {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String passwordCheck;
}
