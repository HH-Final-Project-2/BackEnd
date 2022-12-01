package com.sparta.finalpj.controller.request.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class CompanyRequestDto {
    private String companyName;
    private String companyAddress;
}
