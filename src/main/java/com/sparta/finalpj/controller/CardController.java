package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.ocr.OcrResponseDto;
import com.sparta.finalpj.domain.CompanyType;
import com.sparta.finalpj.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CardController {

    private final CardService cardService;

    // 자사&타사 명함 등록
    @PostMapping(value = "/businessCards")
    public ResponseDto<?> createCard(@RequestParam("companyType") String companyType, @RequestBody CardRequestDto requestDto,
                                     HttpServletRequest request) {
        return cardService.createCard(companyType, requestDto, request);
    }

    // 자사&타사 명함 수정
    @PutMapping(value = "/businessCards/{cardId}")
    public ResponseDto<?> updateCard(@PathVariable Long cardId, @RequestBody CardRequestDto requestDto, HttpServletRequest request) {
        return cardService.updateCard(cardId, requestDto, request);
    }

    // 자사&타사 명함 삭제
    @DeleteMapping(value = "/businessCards/{cardId}")
    public ResponseDto<?> deleteCard(@PathVariable Long cardId, HttpServletRequest request) {
        return cardService.deleteCard(cardId, request);
    }
    // 자사&타사 명함 상세조회
    @PostMapping(value = "/businessCards/{cardId}")
    public ResponseDto<?> getDetailCard(@PathVariable Long cardId, HttpServletRequest request) {
        return cardService.getDetailCard(cardId, request);
    }

    // 자사&타사 명함 전체조회
    @GetMapping(value = "/businessCards")
    public ResponseDto<?> getAllCardsList(@RequestParam("companyType") CompanyType companyType, HttpServletRequest request) {
        return cardService.getAllCardsList(companyType, request);
    }
}
