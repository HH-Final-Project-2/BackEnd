package com.sparta.finalpj.controller.request.member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginRequestDto {
    private String email;
    private String password;
}
