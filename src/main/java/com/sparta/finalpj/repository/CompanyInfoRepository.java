package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
    @Query(value = "select * from company_info where company_name like %:keyword%", nativeQuery = true)
//    @Query(value = "SELECT * FROM company_info WHERE MATCH (company_name) AGAINST ('':keyword'*' in boolean mode)", nativeQuery = true)
    List<CompanyInfo> searchCompany(@Param("keyword") String keyword);
}
