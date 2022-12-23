package com.sparta.finalpj.domain;

import com.sparta.finalpj.controller.request.card.MyCardRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MyCard extends Timestamped {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String cardName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phoneNum;

  @Column(nullable = false)
  private String company;

  @Column(nullable = false)
  private String department;

  @Column(nullable = false)
  private String position;

  @Column
  private String companyAddress;

  @Column
  private String tel;

  @Column
  private String fax;

  @JoinColumn(name = "memberId", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private Member member;

  public void update(MyCardRequestDto myCardRequestDto) {
    this.cardName = myCardRequestDto.getCardName();
    this.email = myCardRequestDto.getEmail();
    this.company = myCardRequestDto.getCompany();
    this.phoneNum = myCardRequestDto.getPhoneNum();
    this.department = myCardRequestDto.getDepartment();
    this.position = myCardRequestDto.getPosition();
    this.companyAddress = myCardRequestDto.getCompanyAddress();
    this.tel = myCardRequestDto.getTel();
    this.fax = myCardRequestDto.getFax();
  }
}
