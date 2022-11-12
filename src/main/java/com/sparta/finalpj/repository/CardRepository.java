package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
