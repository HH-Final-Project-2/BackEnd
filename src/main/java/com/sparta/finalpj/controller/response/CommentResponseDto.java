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
  private boolean commentHeartYn;
  private String content;
  private String author; //작성자
  private Long authorId; //댓글 수정, 삭제 시 필요한 권한을 부여하기 위한 식별자
  private Long CommentHeartCnt; //댓글 좋아요 갯수
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
