package com.sparta.finalpj.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiResponseDto {
    private Long id; // 고유값
    private Long crno; //고유값
   // private String pageNo; // 페이지 번호
    //private String numOfRows; // 한 페이지 결과 수
    private String totalCount; // 전체 결과 수
    private String companyName; // 법인명칭(corpNm) - main
    private String companyAddress; // 회사주소(enpBsadr)
}
