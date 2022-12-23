package com.sparta.finalpj.controller.response.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CompanyInfoResponseDto {
    private String companyName;
    private String companyAddress;
}
