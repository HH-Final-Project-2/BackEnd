package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.CommentRequestDto;
import com.sparta.finalpj.controller.response.CommentResponseDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Comment;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Post;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.TokenProvider;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.repository.CommentHeartRepository;
import com.sparta.finalpj.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentHeartRepository commentHeartRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  //==========댓글 작성==========
  @Transactional
  public ResponseDto<?> createComment(Long postingId, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    if (null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
    }

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    Post post = postService.isPresentPost(postingId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    Comment comment = Comment.builder()
          .member(member)
          .post(post)
          .content(requestDto.getContent())
          .build();
    commentRepository.save(comment);
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  //==========사용자별 댓글 좋아요 체크==========
  @Transactional
  public boolean commentHeartCheck(Comment comment, UserDetailsImpl userDetails) {
    if(userDetails == null){
      return false;
    }
    return commentHeartRepository.existsByMemberAndComment(userDetails.getMember(), comment);
  }

  //================특정 게시글의 댓글 전체조회=================
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentByPost(Long postingId, UserDetailsImpl userDetails) {
    Post post = postService.isPresentPost(postingId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }
    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      long commentHeartCnt = commentHeartRepository.findAllByComment(comment).size();
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .commentHeartYn(commentHeartCheck(comment, userDetails))
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
              .CommentHeartCnt(commentHeartCnt)
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  //==========댓글 수정==========
  @Transactional
  public ResponseDto<?> updateComment(Long postingId, Long commentId, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    if (null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
    }

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    Post post = postService.isPresentPost(postingId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    Comment comment = isPresentComment(commentId);
    if (null == comment) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }

    if (comment.validateMember(member)) {
      throw new CustomException(ErrorCode.NOT_AUTHOR);
    }

    comment.update(requestDto);
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  //==========댓글 삭제==========
  @Transactional
  public ResponseDto<?> deleteComment(Long postingId, Long commentId, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    if (null == request.getHeader("Authorization")) {
      throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
    }

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    Post post = postService.isPresentPost(postingId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    Comment comment = isPresentComment(commentId);
    if (null == comment) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }

    if (comment.validateMember(member)) {
      throw new CustomException(ErrorCode.NOT_AUTHOR);
    }

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//      => HttpServletRequest에서 "Refresh-Token"이라는 이름의 값을 Header에서 get해서
//      tokenProvider에 있는 validateToken method의 매개변수로 쏴준다.
      return null; //=> 유효성 검사 통과x
    }  //=> 통과되면 tokenProvider.getMemberFromAuthentication으로 go!
    return tokenProvider.getMemberFromAuthentication();
  }
}
