package com.sparta.finalpj.controller;

import com.sparta.finalpj.configuration.SwaggerAnnotation;
import com.sparta.finalpj.controller.request.PostRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RequestMapping(value = "/api")
@RestController
public class PostController {

  private final PostService postService;

  //게시글 작성 (파일 업로드 포함)
  @SwaggerAnnotation
  @PostMapping(value = "/posting", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
  public ResponseDto<?> createPost(@RequestPart(value = "postDto") PostRequestDto requestDto,
                                   @RequestParam(value = "image", required = false) MultipartFile image,
                                   HttpServletRequest request) {
    return postService.createPost(requestDto, image, request);
  }

  //특정 게시글 상세 조회
  @GetMapping(value = "/posting/{postingId}")
  public ResponseDto<?> getPost(@PathVariable Long postingId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.getPost(postingId, userDetails);
  }

  //게시글 전체 조회
  @GetMapping(value = "/posting")
  public ResponseDto<?> getAllPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.getAllPost(userDetails);
  }

  //게시글 수정
  @SwaggerAnnotation
  @PutMapping(value = "/posting/{postingId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
  public ResponseDto<?> updatePost(@PathVariable Long postingId,
                                   @RequestPart(value = "postDto") PostRequestDto requestDto,
                                   @RequestParam(value = "image", required = false) MultipartFile image,
                                   HttpServletRequest request) {
    return postService.updatePost(postingId, requestDto, image, request);
  }

  //게시글 삭제
  @SwaggerAnnotation
  @DeleteMapping(value = "/posting/{postingId}")
  public ResponseDto<?> deletePost(@PathVariable Long postingId,
                                   HttpServletRequest request) {
    return postService.deletePost(postingId, request);
  }

  //조회수TOP5 게시글 조회
  @GetMapping(value = "/posting/five")
  public ResponseDto<?> getPostByTop(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.getPostByTop(userDetails);
  }

  //게시글 좋아요순 전체 조회
  @GetMapping(value = "/posting/hearts")
  public ResponseDto<?> getPostByHeart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.getPostByHeart(userDetails);
  }

  //게시글 조회순 전체 조회
  @GetMapping(value = "/posting/hits")
  public ResponseDto<?> getPostByHits(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.getPostByHits(userDetails);
  }

  //게시글 검색
  @GetMapping("/posting/search")
  public ResponseDto<?> search(@RequestParam(value = "keyword") String keyword, @AuthenticationPrincipal UserDetailsImpl userDetails){
    return postService.searchPost(keyword, userDetails);
  }
}

