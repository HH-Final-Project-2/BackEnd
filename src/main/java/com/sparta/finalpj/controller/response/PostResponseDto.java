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
  private Long authorId; //게시글 수정, 삭제 시 필요한 권한을 부여하기 위한 식별자
  private String title; //제목
  private String author; //작성자
  private String jobGroup; //관심 직군
  private String content;
  private String image;
  private Long commentCnt; //댓글 갯수
  private Long postHeartCnt; //게시글 좋아요
  private Integer hit; //조회수
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
