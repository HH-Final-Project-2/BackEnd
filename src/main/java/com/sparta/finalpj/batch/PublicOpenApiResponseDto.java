package com.sparta.finalpj.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicOpenApiResponseDto {
    private String crno; //고유값
    private String companyName; // 법인명칭(corpNm) - main
    private String companyAddress; // 회사주소(enpBsadr)
}
