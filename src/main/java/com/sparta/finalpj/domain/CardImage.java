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

  @Column(nullable = false)
  private String cardImg;

  @Column(nullable = false)
  private String cardName;

  @Column(nullable = false)
  private String cardUrl;

  @JoinColumn(name = "memberId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @JoinColumn(name = "cardId", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private Card card;

}
