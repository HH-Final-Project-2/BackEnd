package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
}
