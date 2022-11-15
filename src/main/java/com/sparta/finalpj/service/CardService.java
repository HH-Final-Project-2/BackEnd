package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.card.CardResponseDto;
import com.sparta.finalpj.domain.*;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.CardImageRepository;
import com.sparta.finalpj.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CommonService commonService;
    private final CardRepository cardRepository;
    private final CardImageRepository cardImageRepository;
    private CompanyType TYPE_OWN = CompanyType.own;
    private CompanyType TYPE_OTHER = CompanyType.other;

    // 자사&타사 명함 등록
    @Transactional
    public ResponseDto<?> createCard(String companyType, CardRequestDto requestDto, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 3-1. 명함정보 저장 [ companyType="own"(자사) / companyType="other"(타사) ]
        // 3-2. 명함이미지 정보 업데이트 => cardId
        if (companyType.equals("own")) {
            try {
                Card card = Card.builder()
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
                        .companyType(TYPE_OWN)
                        .build();
                cardRepository.save(card);

                // 명함 이미지 정보가 있을 경우 cardId를 업데이트
                CardImage cardImage = isPresentCardImg(member);
                if (cardImage != null && cardImage.getMember() != null && cardImage.getCard() == null) {
                    cardImage.update(card);
                }

            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.CARDINFO_UPDATE_FAIL);
            }
        } else {
            try {
                Card card = Card.builder()
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
                        .companyType(TYPE_OTHER)
                        .build();
                cardRepository.save(card);

                // 명함 이미지 정보가 있을 경우 cardId를 업데이트
                CardImage cardImage = isPresentCardImg(member);
                if (cardImage != null && cardImage.getMember() != null && cardImage.getCard() == null) {
                    cardImage.update(card);
                }

            } catch (NullPointerException e) {
                throw new CustomException(ErrorCode.CARDINFO_UPDATE_FAIL);
            }
        }

        return ResponseDto.success("등록 성공");
    }

    // 자사&타사 명함 수정
    @Transactional
    public ResponseDto<?> updateCard(Long cardId, CardRequestDto requestDto, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Card card = isPresentCard(cardId);
        if (card == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }
        card.update(requestDto);
        return ResponseDto.success("수정 완료");
    }

    // 자사&타사 명함 삭제
    @Transactional
    public ResponseDto<?> deleteCard(Long cardId, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Card card = isPresentCard(cardId);
        if (card == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }

        cardRepository.delete(card);
        return ResponseDto.success("삭제 완료");
    }
    
    // 자사&타사 명함 상세조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getDetailCard(Long cardId, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Card card = isPresentCard(cardId);
        if (card == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }

        CardResponseDto cardDetailList = CardResponseDto.builder()
                .id(card.getId())
                .cardName(card.getCardName())
                .engName(card.getEngName())
                .email(card.getEmail())
                .phoneNum(card.getPhoneNum())
                .company(card.getCompany())
                .department(card.getDepartment())
                .position(card.getPosition())
                .companyAddress(card.getCompanyAddress())
                .tel(card.getTel())
                .fax(card.getFax())
                .companyType(card.getCompanyType())
                .createdAt(card.getCreatedAt())
                .modifiedAt(card.getModifiedAt())
                .build();
        return ResponseDto.success(cardDetailList);
    }
    
    // 자사&타사 명함 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCardsList(CompanyType companyType, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        
        // Card => CardResponseDto 타입으로 변환
        List<Card> cardList = cardRepository.findAllByCompanyType(companyType);
        List<CardResponseDto> cardResponseDtoList = new ArrayList<>();
       
        // 자사&타사 명함정보가 없을 경우
        if(cardList.isEmpty()) {
            return ResponseDto.success("명함을 등록해주세요");
        }
        for (Card card : cardList) {
            cardResponseDtoList.add(
                    CardResponseDto.builder()
                            .id(card.getId())
                            .cardName(card.getCardName())
                            .engName(card.getEngName())
                            .email(card.getEmail())
                            .phoneNum(card.getPhoneNum())
                            .company(card.getCompany())
                            .department(card.getCompany())
                            .position(card.getPosition())
                            .companyAddress(card.getCompanyAddress())
                            .tel(card.getTel())
                            .fax(card.getFax())
                            .companyType(card.getCompanyType())
                            .createdAt(card.getCreatedAt())
                            .modifiedAt(card.getModifiedAt())
                            .build()
            );
        }
        return ResponseDto.success(cardResponseDtoList);
    }

    @Transactional(readOnly = true)
    public Card isPresentCard(Long cardId) {
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        return optionalCard.orElse(null);
    }

    @Transactional(readOnly = true)
    public CardImage isPresentCardImg(Member member) {
        Optional<CardImage> optionalCardImage = cardImageRepository.findByMemberAndCard(member, null);
        return optionalCardImage.orElse(null);
    }
}
