package com.sparta.finalpj.controller.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDto {
  private Long id; //cardId
  private String cardName;
  private String email;
  private String phoneNum;
  private String company;
  private String department;
  private String position;
  private String companyAddress;
  private String tel;
  private String fax;
  private String companyType;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
