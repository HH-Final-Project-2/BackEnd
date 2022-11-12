package com.sparta.finalpj.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
  private Long id;
  private String title;
  private String content;
  private String thumbnail;
  private String author;
//  private String job;
  private Long commentCnt; // 댓글 갯수
  private Long postHeartCnt;
  private Integer hit;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
