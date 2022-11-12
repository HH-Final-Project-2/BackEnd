package com.sparta.finalpj.controller.response.ocr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponseDto {
  private String email;
  private String phoneNum;
//  private String company;
//  private String companyAddress;
  private String tel;
  private String fax;
}
