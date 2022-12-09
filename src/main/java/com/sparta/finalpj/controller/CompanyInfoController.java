package com.sparta.finalpj.controller;

import com.sparta.finalpj.configuration.SwaggerAnnotation;
import com.sparta.finalpj.controller.request.card.CardInfoRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.company.CompanyInfoResponseDto;
import com.sparta.finalpj.service.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    @SwaggerAnnotation
    @PostMapping(value = "/cardInfo")
    public CardInfoRequestDto cardInfo(@RequestBody CardInfoRequestDto requestDto){

        return companyInfoService.cardInfo(requestDto);
    }

    /**
     * 명함등록 시 기업 검색
     */
    @SwaggerAnnotation
    @PostMapping(value = "/companySearch")
    public ResponseDto<?> companySearch(@RequestParam("keyword")String keyword, HttpServletRequest request) {
        
        return companyInfoService.companySearch(keyword, request);
    }

    /**
     * 카드 및 기업정보 가져오기
     */
    @PostMapping("/companyInfo")
    public ResponseDto<?> getCompanyInfo(@RequestBody CompanyInfoResponseDto requestDto) {

        return companyInfoService.getCompanyInfo(requestDto);
    }
}
