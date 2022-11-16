package com.sparta.finalpj.domain;

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
public class CardImage extends Timestamped {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String cardImgName;

  @Column(nullable = false)
  private String cardImgUrl;

  @JoinColumn(name = "memberId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "cardId")
  @OneToOne(fetch = FetchType.LAZY)
  private Card card;

  @JoinColumn(name = "myCardId")
  @OneToOne(fetch = FetchType.LAZY)
  private MyCard myCard;

  public void updateCard(Card cardId) {
    this.card = cardId;
  }

  public void updateMyCard(MyCard myCardId) {
    this.myCard = myCardId;
  }

}
