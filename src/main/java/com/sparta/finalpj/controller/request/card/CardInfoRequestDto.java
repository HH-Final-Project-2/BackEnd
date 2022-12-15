package com.sparta.finalpj.controller.request.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoRequestDto {
    private String cardName;
    private String email;
    private String phoneNum;
    private String department;
    private String position;
    private String tel;
    private String fax;
    private String company;
    private String companyType;
}
