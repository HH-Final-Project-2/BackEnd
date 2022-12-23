package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentHeartRepository extends JpaRepository<CommentHeart, Long> {
    Optional<CommentHeart> findByMemberAndComment(Member member, Comment comment);

    List<CommentHeart> findAllByComment(Comment comment);

    boolean existsByMemberAndComment(Member member, Comment comment);
}
