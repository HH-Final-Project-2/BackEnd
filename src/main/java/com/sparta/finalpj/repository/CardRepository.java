package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.Card;
import com.sparta.finalpj.domain.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByCompanyType(CompanyType companyType);
}
