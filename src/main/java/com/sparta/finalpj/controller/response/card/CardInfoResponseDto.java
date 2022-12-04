package com.sparta.finalpj.controller.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoResponseDto {
    private String cardName;
    private String email;
    private String phoneNum;
    private String department;
    private String position;
    private String tel;
    private String fax;
    private String companyType;
}
