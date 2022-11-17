package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.card.MyCardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.card.MyCardResponseDto;
import com.sparta.finalpj.domain.*;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.CardImageRepository;
import com.sparta.finalpj.repository.MyCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyCardService {
    private final CommonService commonService;
    private final MyCardRepository myCardRepository;
    private final CardImageRepository cardImageRepository;

    // 내명함 등록
    @Transactional
    public ResponseDto<?> createMyCard(MyCardRequestDto requestDto, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        // 3-1. 내명함정보 저장
        // 3-2. 내명함이미지 정보 업데이트 => myCardId
        try {
            MyCard myCard = MyCard.builder()
                    .member(member)
                    .cardName(requestDto.getCardName())
                    .engName(requestDto.getEngName())
                    .email(requestDto.getEmail())
                    .phoneNum(requestDto.getPhoneNum())
                    .company(requestDto.getCompany())
                    .department(requestDto.getDepartment())
                    .position(requestDto.getPosition())
                    .companyAddress(requestDto.getCompanyAddress())
                    .tel(requestDto.getTel())
                    .fax(requestDto.getFax())
                    .build();
            myCardRepository.save(myCard);

            // 명함 이미지 정보가 있을 경우 cardId를 업데이트
            CardImage cardImage = isPresentCardImg(member);
            if (cardImage != null && cardImage.getMember() != null && cardImage.getMyCard() == null && cardImage.getCard() == null) {
                cardImage.updateMyCard(myCard);
            }

        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.CARDINFO_UPDATE_FAIL);
        }

        return ResponseDto.success("등록 성공");
    }

    // 내명함 수정
    @Transactional
    public ResponseDto<?> updateMyCard(Long myCardId, MyCardRequestDto requestDto, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        MyCard myCard = isPresentMyCard(myCardId);
        if (myCard == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }
        myCard.update(requestDto);

        MyCardResponseDto myCardUpdateList = MyCardResponseDto.builder()
                .id(myCard.getId())
                .cardName(myCard.getCardName())
                .engName(myCard.getEngName())
                .email(myCard.getEmail())
                .phoneNum(myCard.getPhoneNum())
                .company(myCard.getCompany())
                .department(myCard.getDepartment())
                .position(myCard.getPosition())
                .companyAddress(myCard.getCompanyAddress())
                .tel(myCard.getTel())
                .fax(myCard.getFax())
                .createdAt(myCard.getCreatedAt())
                .modifiedAt(myCard.getModifiedAt())
                .build();

        return ResponseDto.success(myCardUpdateList);
    }

    // 내명함 삭제
    @Transactional
    public ResponseDto<?> deleteMyCard(Long myCardId, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        MyCard myCard = isPresentMyCard(myCardId);
        if (myCard == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }

        myCardRepository.delete(myCard);
        return ResponseDto.success("삭제 완료");
    }

    // 내명함 상세조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getDetailMyCard(Long myCardId, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        MyCard myCard = isPresentMyCard(myCardId);
        if (myCard == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }

        MyCardResponseDto myCardList = MyCardResponseDto.builder()
                .id(myCard.getId())
                .cardName(myCard.getCardName())
                .engName(myCard.getEngName())
                .email(myCard.getEmail())
                .phoneNum(myCard.getPhoneNum())
                .company(myCard.getCompany())
                .department(myCard.getDepartment())
                .position(myCard.getPosition())
                .companyAddress(myCard.getCompanyAddress())
                .tel(myCard.getTel())
                .fax(myCard.getFax())
                .createdAt(myCard.getCreatedAt())
                .modifiedAt(myCard.getModifiedAt())
                .build();
        return ResponseDto.success(myCardList);
    }

    // 내명함 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllMyCardsList(HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // Card => CardResponseDto 타입으로 변환
        MyCard myCard = myCardRepository.findAllByMember(member);

        // 내명함정보가 없을 경우
        if (myCard == null) {
            return ResponseDto.success("명함을 등록해주세요");
        }
        MyCardResponseDto mycardInfo = MyCardResponseDto.builder()
                .id(myCard.getId())
                .cardName(myCard.getCardName())
                .engName(myCard.getEngName())
                .email(myCard.getEmail())
                .phoneNum(myCard.getPhoneNum())
                .company(myCard.getCompany())
                .department(myCard.getDepartment())
                .position(myCard.getPosition())
                .companyAddress(myCard.getCompanyAddress())
                .tel(myCard.getTel())
                .fax(myCard.getFax())
                .createdAt(myCard.getCreatedAt())
                .modifiedAt(myCard.getModifiedAt())
                .build();

        return ResponseDto.success(mycardInfo);
    }

    @Transactional(readOnly = true)
    public MyCard isPresentMyCard(Long myCardId) {
        Optional<MyCard> optionalMyCard = myCardRepository.findById(myCardId);
        return optionalMyCard.orElse(null);
    }

    @Transactional(readOnly = true)
    public CardImage isPresentCardImg(Member member) {
        Optional<CardImage> optionalCardImage = cardImageRepository.findByMemberAndCard(member, null);
        return optionalCardImage.orElse(null);
    }
}
