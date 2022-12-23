package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.*;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.repository.CommentHeartRepository;
import com.sparta.finalpj.repository.PostHeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final PostHeartRepository postHeartRepository;
    private final CommentHeartRepository commentHeartRepository;
    private final PostService postService;
    private final CommentService commentService;

    //===============게시글 좋아요================
    public ResponseDto<?> postHeartUp(Long id, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Post post = postService.isPresentPost(id);

        //Heart DB에서 member와 postId가 존재하는지 확인
        // 회원번호, 게시글번호 조회
        Optional<PostHeart> postHeart = postHeartRepository.findByMemberAndPost(member, post);
        if (postHeart.isPresent()) {
            postHeartRepository.deleteById(postHeart.get().getId());
            return ResponseDto.success(false);
        } else {
            PostHeart heart = PostHeart.builder()
                    .post(post)
                    .member(member)
                    .build();
            postHeartRepository.save(heart);
            return ResponseDto.success(true);
        }
    }

    //===============댓글 좋아요===============
    public ResponseDto<?> commentHeartUp(Long id, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Comment comment = commentService.isPresentComment(id);

        //Heart DB에서 member와 commentId가 존재하는지 확인
        // 회원번호, 댓글 조회
        Optional<CommentHeart> commentHeart = commentHeartRepository.findByMemberAndComment(member, comment);
        if (commentHeart.isPresent()) {
            commentHeartRepository.deleteById(commentHeart.get().getId());
            return ResponseDto.success(false);
        } else {
            CommentHeart heart = CommentHeart.builder()
                    .comment(comment)
                    .member(member)
                    .build();
            commentHeartRepository.save(heart);
            return ResponseDto.success(true);
        }
    }
}

