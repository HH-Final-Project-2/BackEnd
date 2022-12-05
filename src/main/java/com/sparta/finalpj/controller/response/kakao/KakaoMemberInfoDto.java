package com.sparta.finalpj.controller.response.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberInfoDto {
    private Long id;
    private String nickname; //카카오 계정 닉네임
    private String email; // 카카오 계정 email
}
