package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@CustomBaseControllerAnnotation
public class HeartController {
    private final HeartService heartService;

    // 게시글 좋아요
    @PostMapping("/api/auth/post/heart/{postingId}")
    public ResponseDto<?> postHeart(@PathVariable Long postingId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return heartService.postHeartUp(postingId, userDetails);
    }
    // 댓글 좋아요
    @PostMapping("/api/auth/comment/heart/{commentId}")
    public ResponseDto<?> commentHeart(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return heartService.commentHeartUp(commentId, userDetails);
    }

    // 좋아요 한 게시글 조회 (마이페이지)
//    @GetMapping("/auth/post/like")
//    public ResponseDto<?> LikesPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return likesService.LikesPost(userDetails);
//    }


}
