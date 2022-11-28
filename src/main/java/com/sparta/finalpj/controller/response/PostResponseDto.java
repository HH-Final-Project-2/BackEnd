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
public class PostResponseDto {
  private Long id;
  private boolean postHeartYn;
  private String title;
  private String author;
  private String jobGroup; // 관심 직군
  private String content;
  private String image;
  private Long commentCnt; // 댓글 갯수
  private Long postHeartCnt; // 게시글 좋아요
  private Integer hit; // 조회수
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
