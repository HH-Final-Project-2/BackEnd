package com.sparta.finalpj.controller.request.card;

import com.sparta.finalpj.domain.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCardRequestDto {

  private String cardName;
  private String engName;
  private String email;
  private String phoneNum;
  private String company;
  private String department;
  private String position;
  private String companyAddress;
  private String tel;
  private String fax;
}
