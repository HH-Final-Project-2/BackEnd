package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();

  @Modifying(clearAutomatically = true)
  //@Query 어노테이션에서 작성된 조회를 제외한 데이터의 변경이 있는
//삽입(Insert), 수정(Update), 삭제(Delete) 쿼리 사용시 필요한 어노테이션
//@Query("update Post p set p.hit = p.hit + 1 where p.id = :postId") int updateHit(Long postId);
  @Query("update Post q set q.hit = q.hit + 1 where q.id = :id")
  int updateHit(@Param("id") Long id);

  @Query(value = "SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR p.jobGroup LIKE %:keyword% ORDER BY p.createdAt desc", nativeQuery = true)
  List <Post> search(@Param("keyword") String keyword);
}
