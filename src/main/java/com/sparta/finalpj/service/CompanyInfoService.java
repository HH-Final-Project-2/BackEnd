package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.card.CardInfoRequestDto;
import com.sparta.finalpj.controller.request.company.CompanyRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.card.CardInfoResponseDto;
import com.sparta.finalpj.controller.response.company.CompanyInfoResponseDto;
import com.sparta.finalpj.domain.CompanyInfo;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyInfoService {

    private final CommonService commonService;
    private final CompanyInfoRepository companyInfoRepository;

    // 명함등록 시 기업 검색
    public ResponseDto<?> companySearch(String keyword, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        List<CompanyInfo> companyInfoList = companyInfoRepository.searchCompany(keyword);
        List<CompanyInfoResponseDto> responseDtoList = new ArrayList<>();

        for (CompanyInfo companyInfo : companyInfoList) {
            responseDtoList.add(
                    CompanyInfoResponseDto.builder()
                            .companyName(companyInfo.getCompanyName())
                            .companyAddress(companyInfo.getCompanyAddress())
                            .build()
            );
        }
        return ResponseDto.success(responseDtoList);
    }

    // 기업정보 받아오기
    public ResponseDto<?> getCompanyInfo(CompanyRequestDto requestDto) {
        CompanyInfoResponseDto responseDto = CompanyInfoResponseDto.builder()
                .companyName(requestDto.getCompanyName())
                .companyAddress(requestDto.getCompanyAddress())
                .build();

        return ResponseDto.success(responseDto);
    }

    // 카드정보 임시저장
    public ResponseDto<?> getCardInfo(CardInfoRequestDto requestDto) {
        CardInfoResponseDto responseDto = CardInfoResponseDto.builder()
                .cardName(requestDto.getCardName())
                .engName(requestDto.getEngName())
                .email(requestDto.getEmail())
                .phoneNum(requestDto.getPhoneNum())
                .department(requestDto.getDepartment())
                .position(requestDto.getPosition())
                .tel(requestDto.getTel())
                .fax(requestDto.getFax())
                .companyType(requestDto.getCompanyType())
                .build();

        return ResponseDto.success(responseDto);
    }
}
