package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Card;
import com.sparta.finalpj.domain.CardImage;
import com.sparta.finalpj.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CardImageRepository extends JpaRepository<CardImage, Long> {
    Optional<CardImage> findByMemberAndCard(Member member, String empty);

}
