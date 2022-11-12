package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Comment;
import com.sparta.finalpj.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
  Long countByPost(Post post);
  Long countAllByPost(Post post);
}
