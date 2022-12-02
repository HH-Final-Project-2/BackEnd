package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.PostRequestDto;
import com.sparta.finalpj.controller.response.PostResponseDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.*;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.TokenProvider;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.repository.CommentRepository;
import com.sparta.finalpj.repository.PostHeartRepository;
import com.sparta.finalpj.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PostHeartRepository postHeartRepository;
  private final TokenProvider tokenProvider;
  private final GoogleCloudUploadService googleCloudUploadService;


  //===============게시글 작성================
  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, MultipartFile image,
                                   HttpServletRequest request) {

    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    String imageUrl = "";

    if (image == null) {
      imageUrl = "";
    }else {
      UUID uuid = UUID.randomUUID();
      String fileName = uuid.toString() + "_" + image.getOriginalFilename();
        imageUrl = googleCloudUploadService.upload("community", image, fileName);
      }
      Post post = Post.builder()
              .title(requestDto.getTitle())
              .content(requestDto.getContent())
              .jobGroup(requestDto.getJobGroup())
              .member(member)
              .image(imageUrl)
              .hit(0)
              .build();

      postRepository.save(post);
      return ResponseDto.success(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .author(post.getMember().getNickname())
                      .jobGroup(post.getJobGroup()) // 관심 직군
                      .content(post.getContent())
                      .image(post.getImage())
                      .postHeartCnt(0L) // 게시글 좋아요
                      .commentCnt(0L) // 댓글 갯수
                      .hit(post.getHit()) // 조회수
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );

  }

  //=============게시글 상세 조회=============
  @Transactional(readOnly = false)
  public ResponseDto<?> getPost(Long postingId, UserDetailsImpl userDetails) {
    Post post = isPresentPost(postingId);
    if (null == post) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }

    // 댓글 갯수 조회
    Long commentCnt = commentRepository.countByPost(post);

    List<PostHeart> postHeartCnt=postHeartRepository.findByPost(post);
    PostResponseDto postDetailList = PostResponseDto.builder()
            .id(post.getId())
            .postHeartYn(postHeartCheck(post, userDetails))
            .title(post.getTitle())
            .image(post.getImage())
            .author(post.getMember().getNickname())
            .jobGroup(post.getJobGroup()) // 관심 직군
            .content(post.getContent())
            .postHeartCnt((long) postHeartCnt.size())
            .hit(updateHit(postingId))
            .hit(post.getHit()+1) // 조회수
            .commentCnt(commentCnt) // 댓글 갯수
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build();

    return ResponseDto.success(postDetailList);
  }

  //=====조회수 증가=====
  @Transactional
  public int updateHit(Long postId) {
    return postRepository.updateHit(postId);
  }

  //=====사용자별 게시글 좋아요 체크=====
  @Transactional
  public boolean postHeartCheck(Post post, UserDetailsImpl userDetails) {
    if(userDetails == null){
      return false;
    }
    return postHeartRepository.existsByMemberAndPost(userDetails.getMember(), post);
  }

  //======================게시글 전체 조회=====================
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost(UserDetailsImpl userDetails) {

    List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
    List<PostResponseDto> postListResponseDtoList = new ArrayList<>();

    for (Post post : postList) {
      long comment = commentRepository.countAllByPost(post);
      long postHeartCnt = postHeartRepository.findAllByPost(post).size();
      postListResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .postHeartYn(postHeartCheck(post, userDetails))
                      .title(post.getTitle())
                      .image(post.getImage())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .jobGroup(post.getJobGroup()) // 관심 직군
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
    public ResponseDto<?> searchPost(String keyword, UserDetailsImpl userDetails) {
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
                      .postHeartYn(postHeartCheck(post, userDetails))
                      .title(post.getTitle())
                      .image(post.getImage())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .jobGroup(post.getJobGroup()) // 관심 직군
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

    String imageUrl = "";

    if (image == null) {
      imageUrl = post.getImage();
    }else {
      UUID uuid = UUID.randomUUID();
      String fileName = uuid.toString() + "_" + image.getOriginalFilename();
      imageUrl = googleCloudUploadService.upload("community", image, fileName);
    }

    List<PostHeart> postHeartCnt = postHeartRepository.findByPost(post);
    Long commentCnt = commentRepository.countByPost(post);

    post.update(requestDto, imageUrl);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .image(post.getImage())
                    .author(post.getMember().getNickname())
                    .jobGroup(post.getJobGroup()) // 관심 직군
                    .content(post.getContent())
                    .postHeartCnt((long) postHeartCnt.size()) // 게시글 좋아요
                    .commentCnt(commentCnt) // 댓글 갯수
                    .hit(post.getHit()) // 조회수
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  //===================게시글 삭제======================
  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {

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
  @Transactional
  public ResponseDto<?> getPostByTop(UserDetailsImpl userDetails) {

      List<Post> postList = postRepository.findTop5ByOrderByHitDesc();
      List<PostResponseDto> postListResponseDtoList = new ArrayList<>();

      for (Post post : postList) {

      long comment = commentRepository.countAllByPost(post);
      long postHeartCnt = postHeartRepository.findAllByPost(post).size();

      postListResponseDtoList.add(PostResponseDto.builder()
                      .id(post.getId())
                      .postHeartYn(postHeartCheck(post, userDetails))
                      .title(post.getTitle())
                      .image(post.getImage())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .jobGroup(post.getJobGroup()) // 관심 직군
                      .postHeartCnt(postHeartCnt) //게시글 좋아요
                      .commentCnt(comment) // 댓글 갯수
                      .hit(post.getHit()) //조회수
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
        postListResponseDtoList.sort((o1, o2) -> (o2.getHit() - o1.getHit()));
    }
    return ResponseDto.success(postListResponseDtoList);
  }

  //==================좋아요순 게시글 전체 조회====================
  @Transactional
  public ResponseDto<?> getPostByHeart(UserDetailsImpl userDetails) {

    List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
    List<PostResponseDto> postListResponseDtoList = new ArrayList<>();

    for (Post post : postList) {

      long comment = commentRepository.countAllByPost(post);
      long postHeartCnt = postHeartRepository.findAllByPost(post).size();

      postListResponseDtoList.add(PostResponseDto.builder()
              .id(post.getId())
              .postHeartYn(postHeartCheck(post, userDetails))
              .title(post.getTitle())
              .image(post.getImage())
              .content(post.getContent())
              .author(post.getMember().getNickname())
              .jobGroup(post.getJobGroup()) // 관심 직군
              .postHeartCnt(postHeartCnt) //게시글 좋아요
              .commentCnt(comment) // 댓글 갯수
              .hit(post.getHit()) //조회수
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
      postListResponseDtoList.sort((o1, o2) -> (int) (o2.getPostHeartCnt() - o1.getPostHeartCnt()));
    }
    return ResponseDto.success(postListResponseDtoList);
  }

  //==================조회순 게시글 전체 조회===================
//  @Transactional
//  public ResponseDto<?> getPostByHits(Pageable pageable) {
//    Page<Post> postList = postRepository.findAll(pageable);
//    List<PostResponseDto> postListResponseDtoList = new ArrayList<>();
//
//    for (Post post : postList) {
//
//      long comment = commentRepository.countAllByPost(post);
//      long postHeartCnt = postHeartRepository.findAllByPost(post).size();
//
//      postListResponseDtoList.add(PostResponseDto.builder()
//              .id(post.getId())
//              .title(post.getTitle())
//              .image(post.getImage())
//              .content(post.getContent())
//              .author(post.getMember().getNickname())
//              .jobGroup(post.getJobGroup()) // 관심 직군
//              .postHeartCnt(postHeartCnt) //게시글 좋아요
//              .commentCnt(comment) // 댓글 갯수
//              .hit(post.getHit()) //조회수
//              .createdAt(post.getCreatedAt())
//              .modifiedAt(post.getModifiedAt())
//              .build()
//      );
//      postListResponseDtoList.sort((o1, o2) -> (o2.getHit() - o1.getHit()));
//    }
//    return ResponseDto.success(postListResponseDtoList);
//  }

  @Transactional
  public ResponseDto<?> getPostByHits(UserDetailsImpl userDetails) {

    List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
    List<PostResponseDto> postListResponseDtoList = new ArrayList<>();

    for (Post post : postList) {

      long comment = commentRepository.countAllByPost(post);
      long postHeartCnt = postHeartRepository.findAllByPost(post).size();

      postListResponseDtoList.add(PostResponseDto.builder()
              .id(post.getId())
              .postHeartYn(postHeartCheck(post, userDetails))
              .title(post.getTitle())
              .image(post.getImage())
              .content(post.getContent())
              .author(post.getMember().getNickname())
              .jobGroup(post.getJobGroup()) // 관심 직군
              .postHeartCnt(postHeartCnt) //게시글 좋아요
              .commentCnt(comment) // 댓글 갯수
              .hit(post.getHit()) //조회수
              .createdAt(post.getCreatedAt())
              .modifiedAt(post.getModifiedAt())
              .build()
      );
      postListResponseDtoList.sort((o1, o2) -> (o2.getHit() - o1.getHit()));
    }
    return ResponseDto.success(postListResponseDtoList);
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
