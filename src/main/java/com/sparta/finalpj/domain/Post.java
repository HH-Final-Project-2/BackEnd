package com.sparta.finalpj.domain;

import com.sparta.finalpj.controller.request.PostRequestDto;
import lombok.*;

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
  @Column(nullable = false)
  private String author;
  @Column(nullable = true)
  private String image;
  @Column(nullable = false)
  private String jobGroup;

  //조회 수
  @Column(columnDefinition = "integer default 0",nullable = false) //조회수의 기본 값을 0으로 지정, null 불가 처리
  private Integer hit;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> comment;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<PostHeart> postHeart;

  @JoinColumn(name = "memberId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  public void update(PostRequestDto postRequestDto, String image) {
    this.title = postRequestDto.getTitle();
    this.jobGroup = postRequestDto.getJobGroup();
    this.content = postRequestDto.getContent();
    this.image=image;
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}
