package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.PostRequestDto;
import com.sparta.finalpj.controller.response.CommentResponseDto;
import com.sparta.finalpj.controller.response.PostResponseDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.*;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.TokenProvider;
import com.sparta.finalpj.repository.CommentHeartRepository;
import com.sparta.finalpj.repository.CommentRepository;
import com.sparta.finalpj.repository.PostHeartRepository;
import com.sparta.finalpj.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PostHeartRepository postHeartRepository;
  private final CommentHeartRepository commentHeartRepository;
  private final FileS3Service fileS3Service;
  private final TokenProvider tokenProvider;


  //===============게시글 작성================
  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, MultipartFile image,
                                   HttpServletRequest request) {

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    //===이미지 파일 처리===
    String imageUrl = "";

    try {
      imageUrl = fileS3Service.uploadFile(image);
    } catch (IOException e) {
      throw new CustomException(ErrorCode.AWS_S3_UPLOAD_FAIL);
    }

    Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .jobGroup(requestDto.getJobGroup())
            .member(member)
            .image(imageUrl)
            .hit(0)
//            .thumbnail(thumbnailUrl)
            .build();
    postRepository.save(post);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .author(post.getMember().getNickname())
                    .jobGroup(post.getJobGroup())
                    .content(post.getContent())
                    .image(post.getImage())
                    .postHeartCnt(0L)
                    .commentCnt(0L)
                    .hit(post.getHit())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  //=============게시글 상세 조회=============
  @Transactional(readOnly = false)
  public ResponseDto<?> getPost(Long postingId) {
    Post post = isPresentPost(postingId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }
    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    // 댓글 갯수 조회
    Long commentCnt = commentRepository.countByPost(post);

    // 해당 게시글에 대한 댓글 List
    for (Comment comment : commentList) {
      long commentHeartCnt = commentHeartRepository.findAllByComment(comment).size();
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
//                      .jobGroup(comment.getJobGroup())
                      .CommentHeartCnt(commentHeartCnt)
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    List<PostHeart> postHeartCnt=postHeartRepository.findByPost(post);
    PostResponseDto postDetailList = PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .author(post.getMember().getNickname())
            .jobGroup(post.getJobGroup())
            .content(post.getContent())
            .image(post.getImage())
            .commentResponseDtoList(commentResponseDtoList)
            .postHeartCnt((long) postHeartCnt.size())
            .hit(updateHit(postingId))
            .hit(post.getHit()+1) // 조회수
            .commentCnt(commentCnt) // 댓글 갯수
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();

    return ResponseDto.success(postDetailList);
  }

  //=====조회수 증가 =====
  @Transactional
  public int updateHit(Long postId) {
    return postRepository.updateHit(postId);
  }

  //==============게시글 전체 조회================
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
    List<PostResponseDto> postListResponseDtoList = new ArrayList<>();
    for (Post post : postList) {
      long comment = commentRepository.countAllByPost(post);
      long postHeartCnt = postHeartRepository.findAllByPost(post).size();
      postListResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .image(post.getImage())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .jobGroup(post.getJobGroup())
                      .postHeartCnt(postHeartCnt) //게시글 좋아요
                      .commentCnt(comment) // 댓글 갯수
                      .hit(post.getHit()) //조회수
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(postListResponseDtoList);
  }

  //=================게시글 검색=================
  @Transactional
    public ResponseDto<?> searchPost(String keyword) {
    List<Post> postList = postRepository.search(keyword);
    // 검색된 항목 담아줄 리스트 생성
    List<PostResponseDto> postListResponseDtoList = new ArrayList<>();
    //for문을 통해서 List에 담아주기
    for (Post post : postList) {
      long comment = commentRepository.countAllByPost(post);
      long postHeartCnt = postHeartRepository.findAllByPost(post).size();
      postListResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .image(post.getImage())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .jobGroup(post.getJobGroup())
                      .postHeartCnt(postHeartCnt) //게시글 좋아요
                      .commentCnt(comment) // 댓글 갯수
                      .hit(post.getHit()) //조회수
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }
    //결과값
    return ResponseDto.success(postListResponseDtoList);
  }

  //===============게시글 수정=================
  @Transactional
  public ResponseDto<?> updatePost(Long id, PostRequestDto requestDto, MultipartFile image, HttpServletRequest request) {

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
    Post post = isPresentPost(id);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }
    if (post.validateMember(member)) {
      throw new CustomException(ErrorCode.NOT_AUTHOR);
    }

    // 이미지 파일 처리
    String imageUrl = "";

    try {
      imageUrl = fileS3Service.uploadFile(image);
    } catch (IOException e) {
      throw new CustomException(ErrorCode.AWS_S3_UPLOAD_FAIL);
    }

    List<PostHeart> postHeartCnt = postHeartRepository.findByPost(post);
    Long commentCnt = commentRepository.countByPost(post);

    post.update(requestDto, imageUrl);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .author(post.getMember().getNickname())
                    .jobGroup(post.getJobGroup())
                    .content(post.getContent())
                    .image(post.getImage())
                    .postHeartCnt((long) postHeartCnt.size())
                    .commentCnt(commentCnt)
                    .hit(post.getHit())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  //===================게시글 삭제======================
  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
//    if (null == request.getHeader("Refresh-Token")) {
//      return ResponseDto.fail("MEMBER_NOT_FOUND",
//          "로그인이 필요합니다.");
//    }
//
//    if (null == request.getHeader("Authorization")) {
//      return ResponseDto.fail("MEMBER_NOT_FOUND",
//          "로그인이 필요합니다.");
//    }

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    Post post = isPresentPost(id);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    if (post.validateMember(member)) {
      throw new CustomException(ErrorCode.NOT_AUTHOR);
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }


  //==================조회수TOP5 게시글 조회===================
//  @Transactional
//  public ResponseDto<?> getPostsByCount(Pageable pageable) {
//
//      Page<Post> postList = postRepository.findAll(pageable);
//      List<PostResponseDto> postListResponseDtoList = new ArrayList<>();
//
//      for (Post post : postList) {
//
//      long comment = commentRepository.countAllByPost(post);
//      long postHeartCnt = postHeartRepository.findAllByPost(post).size();
//
//      postListResponseDtoList.add(PostResponseDto.builder()
//                      .id(post.getId())
//                      .title(post.getTitle())
//                      .content(post.getContent())
//                      .author(post.getMember().getNickname())
//                      .jobGroup(post.getJobGroup())
//                      .postHeartCnt(postHeartCnt) //게시글 좋아요
//                      .commentCnt(comment) // 댓글 갯수
//                      .hit(post.getHit()) //조회수
//                      .createdAt(post.getCreatedAt())
//                      .modifiedAt(post.getModifiedAt())
////              .memberId(post.getMember().getMemberId())
//                      .build()
//      );
//    }
//    return ResponseDto.success(postListResponseDtoList);
//  }


  @Transactional(readOnly = true)
  public int commentHeartCnt(Comment comment) {
    List<CommentHeart> commentLikeList = commentHeartRepository.findAllByComment(comment);
    return commentLikeList.size();
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
