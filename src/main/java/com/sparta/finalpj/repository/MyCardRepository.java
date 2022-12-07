package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.MyCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyCardRepository extends JpaRepository<MyCard, Long> {
    MyCard findAllByMember(Member member);
    void deleteByMemberId(Long memberId);
}
