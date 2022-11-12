package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.*;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.repository.CommentHeartRepository;
//import com.sparta.finalpj.repository.CommentRepository;
import com.sparta.finalpj.repository.PostHeartRepository;
import com.sparta.finalpj.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final PostHeartRepository postHeartRepository;
    private final CommentHeartRepository commentHeartRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final PostRepository postRepository;

    public ResponseDto<?> postHeartUp (Long id , UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Post post = postService.isPresentPost(id);

   //Heart DB에서 맴버아이디와 포스트아이디가 존재하는지 확인
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

    public ResponseDto<?> commentHeartUp (Long id , UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        Comment comment = commentService.isPresentComment(id);

        //Heart DB에서 맴버아이디와 commentId가 존재하는지 확인
        // 회원번호, 게시글번호 조회
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

    // 내가 좋아요한 게시글들 조회
//    public ResponseDto<?> LikesPost(UserDetailsImpl userDetails){
//
//        // 현재 로그인된 유저정보로 like DB에서 연관되어 있는 like 정보 리스트업
//        // 리스트업한 이유 : 좋아요한 글이 두개 이상일 수도 있기 때문
//        List<Likes> likelist = likesRepository.findAllByMember(userDetails.getMember());
//
//        // 조회 가능한 이력이 없다면 실패 처리
//        if(likelist.isEmpty()){
//            ResponseDto.fail("NOT_FOUND", "좋아요한 게시글이 없습니다.");
//        }
//
//        // 조회된 post들을 최종적으로 담아 보여줄 list 생성
//        List<PostResponseDto> postResponseDtos = new ArrayList<>();
//
//        // 조회된 좋아요한 post들의 각 postid로 DB에서 조회하여 post 불러옴
//        for(Likes like : likelist){
//            Post post = postRepository.findById(like.getPost().getId()).orElseThrow(
//                    () -> new NullPointerException("좋아요한 게시글이 아닙니다.")
//            );
//
//            // 불러온 post들을 postResponseDtos 에 저장
//            postResponseDtos.add(
//                    PostResponseDto.builder()
//                            .id(post.getId())
//                            .title(post.getTitle())
//                            .content(post.getContent())
//                            .author(post.getMember().getNickname())
//                            .createdAt(post.getCreatedAt())
//                            .modifiedAt(post.getModifiedAt())
//                            .build()
//            );
//        }
//        // postResponseDtos 에 저장되어 최종적으로 좋아요가 되어있는 게시글들 전부 조회
//        return ResponseDto.success(postResponseDtos);

    }

