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

    //==========λκΈ μμ±==========
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
                        .authorId(comment.getMember().getId()) //λκΈ μμ , μ­μ  μ νμν κΆνμ λΆμ¬νκΈ° μν μλ³μ
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }

    //==========μ¬μ©μλ³ λκΈ μ’μμ μ²΄ν¬==========
    @Transactional
    public boolean commentHeartCheck(Comment comment, UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return false;
        }
        return commentHeartRepository.existsByMemberAndComment(userDetails.getMember(), comment);
    }

    //================νΉμ  κ²μκΈμ λκΈ μ μ²΄μ‘°ν=================
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
                            .author(comment.getMember().getNickname()) //μμ±μ
                            .authorId(comment.getMember().getId()) //λκΈ μμ , μ­μ  μ νμν κΆνμ λΆμ¬νκΈ° μν μλ³μ
                            .content(comment.getContent())
                            .CommentHeartCnt(commentHeartCnt) //λκΈ μ’μμ
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }
        return ResponseDto.success(commentResponseDtoList);
    }

    //==========λκΈ μμ ==========
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
                        .author(comment.getMember().getNickname()) //μμ±μ
                        .authorId(comment.getMember().getId()) //λκΈ μμ , μ­μ  μ νμν κΆνμ λΆμ¬νκΈ° μν μλ³μ
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }

    //==========λκΈ μ­μ ==========
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
//      => HttpServletRequestμμ "Refresh-Token"μ΄λΌλ μ΄λ¦μ κ°μ Headerμμ getν΄μ
//      tokenProviderμ μλ validateToken methodμ λ§€κ°λ³μλ‘ μ΄μ€λ€.
            return null; //=> μ ν¨μ± κ²μ¬ ν΅κ³Όx
        }  //=> ν΅κ³Όλλ©΄ tokenProvider.getMemberFromAuthenticationμΌλ‘ go!
        return tokenProvider.getMemberFromAuthentication();
    }
}
