package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.PostHeart;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    Optional<PostHeart> findByMemberAndPost(Member member, Post post);
    List<PostHeart> findByPost(Post post);
    Long countByPost(Post post);
    List<PostHeart> findAllByPost(Post post);

}
