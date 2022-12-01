package com.sparta.finalpj.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String crno;

    @Column
    private String companyName; // 법인명칭(corpNm)

    @Column
    private String companyAddress; // 회사주소(enpBsadr)

}
