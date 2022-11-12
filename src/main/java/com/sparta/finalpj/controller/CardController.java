package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class CardController {

    private final CardService cardService;

    // 자사 명함 등록
    @PostMapping(value = "/cards/own")
    public ResponseDto<?> createOwnCard(@RequestBody CardRequestDto requestDto,
                                     HttpServletRequest request) {
        return cardService.createOwnCard(requestDto, request);
    }

    // 타사 명함 등록
    @PostMapping(value = "/cards/other")
    public ResponseDto<?> createOtherCard(@RequestBody CardRequestDto requestDto,
                                        HttpServletRequest request) {
        return cardService.createOtherCard(requestDto, request);
    }
    
    // 자사&타사 명함 수정
    @PutMapping(value = "/cards/{cardId}")
    public ResponseDto<?> updateCard(@PathVariable Long cardId, @RequestBody CardRequestDto requestDto, HttpServletRequest request) {
        return cardService.updateCard(cardId, requestDto, request);
    }

}
