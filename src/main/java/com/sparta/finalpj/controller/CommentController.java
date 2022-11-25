package com.sparta.finalpj.controller;

import com.sparta.finalpj.configuration.SwaggerAnnotation;
import com.sparta.finalpj.controller.request.CommentRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@RestController
public class CommentController {

  private final CommentService commentService;

  //댓글 작성
  @SwaggerAnnotation
  @PostMapping(value = "/comment/{postingId}")
  public ResponseDto<?> createComment(@PathVariable Long postingId, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.createComment(postingId, requestDto, request);
  }

  //특정 게시글의 댓글 전체조회
  @GetMapping(value = "/comment/{postingId}")
  public ResponseDto<?> getAllComment(@PathVariable Long postingId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.getAllCommentByPost(postingId, userDetails);
  }

  //댓글 수정
  @SwaggerAnnotation
  @PutMapping(value = "/comment/{postingId}/{commentId}")
  public ResponseDto<?> updateComment(@PathVariable Long postingId,@PathVariable Long commentId, @RequestBody CommentRequestDto requestDto,
      HttpServletRequest request) {
    return commentService.updateComment(postingId, commentId, requestDto, request);
  }

  //댓글 삭제
  @SwaggerAnnotation
  @DeleteMapping(value = "/comment/{postingId}/{commentId}")
  public ResponseDto<?> deleteComment(@PathVariable Long postingId,@PathVariable Long commentId,
      HttpServletRequest request) {
    return commentService.deleteComment(postingId, commentId, request);
  }
}
