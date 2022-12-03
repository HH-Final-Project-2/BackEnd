package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.card.CardInfoRequestDto;
import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.request.company.CompanyRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    /**
     * 명함등록 시 기업 검색
     */
    @PostMapping(value = "/companySearch")
    public ResponseDto<?> companySearch(@RequestParam("keyword")String keyword, HttpServletRequest request) {
        
        return companyInfoService.companySearch(keyword, request);
    }

    /**
     * 기업정보 가져오기
     */
    @PostMapping("/companyInfo")
    public ResponseDto<?> getCompanyInfo(@RequestBody CompanyRequestDto requestDto) {

        return companyInfoService.getCompanyInfo(requestDto);
    }

    /**
     * 카드정보 저장
     */
    @PostMapping("/cardInfo")
    public ResponseDto<?> getCardInfo(@RequestBody CardInfoRequestDto requestDto) {

        return companyInfoService.saveCardInfo(requestDto);
    }
}
