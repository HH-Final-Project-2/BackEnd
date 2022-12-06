package com.sparta.finalpj.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
  private Long id;
  private String title; //제목
  private String content; //내용
  private String author; //작성자
  private String jobGroup; //관심 직군
  private Long commentCnt; //댓글 갯수
  private Long postHeartCnt; //게시글 좋아요
  private Integer hit; //조회수
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
