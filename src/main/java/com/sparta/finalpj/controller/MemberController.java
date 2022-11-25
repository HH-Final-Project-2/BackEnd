package com.sparta.finalpj.controller;


import com.sparta.finalpj.configuration.SwaggerAnnotation;
import com.sparta.finalpj.controller.request.member.EmailCheckRequestDto;
import com.sparta.finalpj.controller.request.member.LoginRequestDto;
import com.sparta.finalpj.controller.request.member.MemberUpdateRequestDto;
import com.sparta.finalpj.controller.request.member.SignupRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
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
    @PostMapping("/members/signup")
    public ResponseDto<?> signupMember(@RequestBody SignupRequestDto requestDto) {
        return memberService.signupMember(requestDto);
    }

    //이메일체크
    @PostMapping("/members/check")
    public ResponseDto<?> emailDubCheck(@RequestBody EmailCheckRequestDto requestDto) {
        return memberService.emailCheck(requestDto);
    }

    //로그인
    @PostMapping("/members/login")
    public ResponseDto<?> loginMember(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.loginMember(requestDto,response);
    }

    //로그아웃
    @SwaggerAnnotation
    @PostMapping("/members/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logoutMember(request);
    }

    //회원탈퇴
    @SwaggerAnnotation
    @DeleteMapping("/members/withdraw")
    public ResponseDto<?> withdraw(HttpServletRequest request) {
        return memberService.withdrawMember(request);
    }

//    @PostMapping("/member/refresh")
//    public ResponseDto<?> refreshTokenCheck(HttpServletRequest request, HttpServletResponse response){
//        return memberService.refreshToken(request, response);
//    }

    //내 프로필 조회
    @SwaggerAnnotation
    @GetMapping("/members/profiles")
    public ResponseDto<?> myProfile(HttpServletRequest request){

        return  memberService.myProfile(request);
    }
    @SwaggerAnnotation
    @PatchMapping("/members/profiles")
    public ResponseDto<?> memberUpdate(@RequestBody MemberUpdateRequestDto memberRequestDto,
                                       HttpServletRequest request){
        return memberService.updateMember(memberRequestDto, request);
    }


}
