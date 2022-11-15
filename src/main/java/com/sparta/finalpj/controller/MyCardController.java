package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.card.CardRequestDto;
import com.sparta.finalpj.controller.request.card.MyCardRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.CompanyType;
import com.sparta.finalpj.service.MyCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class MyCardController {

    private final MyCardService mycardService;

    // 내명함 등록
    @PostMapping(value = "/mypages")
    public ResponseDto<?> createMyCard(@RequestBody MyCardRequestDto requestDto, HttpServletRequest request) {
        return mycardService.createMyCard(requestDto, request);
    }

    // 내명함 수정
    @PutMapping(value = "/mypages/{myCardId}")
    public ResponseDto<?> updateMyCard(@PathVariable Long myCardId, @RequestBody MyCardRequestDto requestDto, HttpServletRequest request) {
        return mycardService.updateMyCard(myCardId, requestDto, request);
    }

    // 내명함 삭제
    @DeleteMapping(value = "/mypages/{myCardId}")
    public ResponseDto<?> deleteMyCard(@PathVariable Long myCardId, HttpServletRequest request) {
        return mycardService.deleteMyCard(myCardId, request);
    }
    // 내명함 상세조회
    @PostMapping(value = "/mypages/{myCardId}")
    public ResponseDto<?> getDetailMyCard(@PathVariable Long myCardId, HttpServletRequest request) {
        return mycardService.getDetailMyCard(myCardId, request);
    }

    // 내명함 전체조회
    @GetMapping(value = "/mypages")
    public ResponseDto<?> getAllMyCardsList(HttpServletRequest request) {
        return mycardService.getAllMyCardsList(request);
    }
}
