package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Card;
import com.sparta.finalpj.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByMemberOrderByCreatedAtDesc(Member member);

    @Query(value = "select * from card " +
            "where member_id = :member_id " +
            "and (card_name like %:keyword% " +
            "or company like %:keyword% " +
            "or department like %:keyword% " +
            "or phone_num like %:keyword% " +
            "or position like %:keyword%) " +
            "order by created_at"
            , nativeQuery = true)
    List<Card> searchCard(@Param("member_id") Member member, @Param("keyword") String keyword);
}
