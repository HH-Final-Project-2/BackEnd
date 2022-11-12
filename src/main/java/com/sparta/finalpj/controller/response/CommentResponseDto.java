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
public class CommentResponseDto {
  private Long id;
  private String content;
  private String author;
  private String job;
  private Long CommentHeartCnt; // 좋아요 갯수
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
