package com.sparta.finalpj.controller;

import com.sparta.finalpj.configuration.SwaggerAnnotation;
import com.sparta.finalpj.controller.request.PostRequestDto;
import com.sparta.finalpj.controller.response.PostResponseDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RequestMapping(value = "/api")
@RestController
public class PostController {

  private final PostService postService;
//  @ApiImplicitParams({
//          @ApiImplicitParam(
//                  name = "Refresh-Token",
//                  required = true,
//                  dataType = "string",
//                  paramType = "header"
//          )
//  })

//  @GetMapping("/posting/read/{postingId}")
//  public String read(@PathVariable Long id, Model model) {
//    PostResponseDto dto = postService.findById(id);
//    postService.updateHitCnt(id); // views ++
//    model.addAttribute("posts", dto);
//
//    return "posts-read";
//  }

  //게시글 작성 (파일 업로드 포함)
  @SwaggerAnnotation
  @PostMapping(value = "/posting",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
  public ResponseDto<?> createPost(@RequestPart(value = "postDto") PostRequestDto requestDto,
                                   @RequestParam(value = "image", required = false) MultipartFile image,
                                   HttpServletRequest request) {
    return postService.createPost(requestDto,image, request);
  }

  //특정 게시글 조회
  @GetMapping(value = "/posting/{postingId}")
  public ResponseDto<?> getPost(@PathVariable Long postingId) {
    return postService.getPost(postingId);
  }

  //게시글 전체 조회
  @GetMapping(value = "/posting")
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

//  @GetMapping(value = "/api/posting/job/{job}")
//  public ResponseDto<?> getPost(@PathVariable String job) {
//    return postService.getPostbyjob(job);
//  }

  //게시글 수정
  @SwaggerAnnotation
  @PutMapping(value = "/posting/{postingId}",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
  public ResponseDto<?> updatePost(@PathVariable Long postingId,
                                   @RequestPart(value = "postDto") PostRequestDto requestDto,
//                                   @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
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

  //========게시글 검색==========
//  @ResponseBody
//  @GetMapping("/posting/search")
//  public ResponseEntity<PrivateResponseBody> search(@RequestParam(value = "keyword") String keyword){
//    return postService.searchPosts(keyword);
//  }
}