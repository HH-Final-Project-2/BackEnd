package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Card;
import com.sparta.finalpj.domain.CompanyType;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.TokenProvider;
import com.sparta.finalpj.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final TokenProvider tokenProvider;
    private final CardRepository cardRepository;
    private CompanyType TYPE_OWN = CompanyType.own;
    private CompanyType TYPE_OTHER = CompanyType.other;

    // 자사 명함 등록
    @Transactional
    public ResponseDto<?> createOwnCard(CardRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        if (null == request.getHeader("Authorization")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Member member = validateMember(request);
        if (null == member) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Card cardInfo = Card.builder()
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
        cardRepository.save(cardInfo);
        return ResponseDto.success("등록 성공");
    }

    // 타사 명함 등록
    @Transactional
    public ResponseDto<?> createOtherCard(CardRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        if (null == request.getHeader("Authorization")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Member member = validateMember(request);
        if (null == member) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Card cardInfo = Card.builder()
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
        cardRepository.save(cardInfo);
        return ResponseDto.success("등록 성공");
    }

    // 명함 수정
    public ResponseDto<?> updateCard(Long cardId, CardRequestDto requestDto, HttpServletRequest request) {
        if (request.getHeader("Refresh-Token") == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getHeader("Authorization") == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Member member = validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Card card = isPresentComment(cardId);
        if(card == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CARD);
        }
        card.update(requestDto);
        return ResponseDto.success("수정 완료");
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    @Transactional(readOnly = true)
    public Card isPresentComment(Long commentId) {
        Optional<Card> optionalComment = cardRepository.findById(commentId);
        return optionalComment.orElse(null);
    }
}
