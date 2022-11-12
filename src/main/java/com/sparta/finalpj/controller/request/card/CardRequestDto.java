package com.sparta.finalpj.controller.request.card;

import com.sparta.finalpj.domain.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {

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
  private CompanyType companyType;
}
