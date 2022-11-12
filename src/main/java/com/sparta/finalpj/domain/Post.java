package com.sparta.finalpj.domain;

import com.sparta.finalpj.controller.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String title;
  @Column(nullable = false)
  private String content;

//  @Column(nullable = true)
//  private String thumbnail;// 썸네일
  @Column(nullable = true)
  private String image;

//  @Column(nullable = false)
//  private job job;

//  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<Comment> comments;
//
//  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<PostHeart> heart;

  //조회 수
  @Column(columnDefinition = "integer default 0",nullable = false) //조회수의 기본 값을 0으로 지정, null 불가 처리
  private Integer hit;

  @JoinColumn(name = "member_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  public void update(PostRequestDto postRequestDto, String image) {
    this.title = postRequestDto.getTitle();
//    this.job = postRequestDto.getjob();
    this.content = postRequestDto.getContent();
    this.image=image;
//    this.thumbnail=thumbnail;
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}
