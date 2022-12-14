package com.sparta.finalpj.domain;

import com.sparta.finalpj.controller.request.card.CardRequestDto;
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
public class Card extends Timestamped {
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

  @Column(nullable = false)
  private String companyType;

  @JoinColumn(name = "memberId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  public void update(CardRequestDto cardRequestDto) {
    this.cardName = cardRequestDto.getCardName();
    this.email = cardRequestDto.getEmail();
    this.company = cardRequestDto.getCompany();
    this.phoneNum = cardRequestDto.getPhoneNum();
    this.department = cardRequestDto.getDepartment();
    this.position = cardRequestDto.getPosition();
    this.companyAddress = cardRequestDto.getCompanyAddress();
    this.tel = cardRequestDto.getTel();
    this.fax = cardRequestDto.getFax();
  }
}
