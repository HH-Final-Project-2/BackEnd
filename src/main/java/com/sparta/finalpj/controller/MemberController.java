package com.sparta.finalpj.controller;


import com.sparta.finalpj.controller.request.member.EmailCheckRequestDto;
import com.sparta.finalpj.controller.request.member.LoginRequestDto;
import com.sparta.finalpj.controller.request.member.SignupRequestDto;
import com.sparta.finalpj.jwt.ResponseDto;
import com.sparta.finalpj.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("/member/signup")
    public ResponseDto<?> signupMember(@RequestBody SignupRequestDto requestDto) {
        return memberService.signupMember(requestDto);
    }

    //이메일체크
    @PostMapping("/member/check")
    public ResponseDto<?> emailDubCheck(@RequestBody EmailCheckRequestDto requestDto) {
        return memberService.emailCheck(requestDto);
    }

    //로그인
    @PostMapping("/member/login")
    public ResponseDto<?> loginMember(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.loginMember(requestDto,response);
    }

    //로그아웃
    @PostMapping("/auth/member/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logoutMember(request);
    }

    //회원탈퇴
    @DeleteMapping("/auth/member/withdraw")
    public ResponseDto<?> withdraw(HttpServletRequest request) {
        return memberService.withdrawMember(request);
    }

//    @PostMapping("/member/refresh")
//    public ResponseDto<?> refreshTokenCheck(HttpServletRequest request, HttpServletResponse response){
//        return memberService.refreshToken(request, response);
//    }

}
