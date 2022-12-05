package com.sparta.finalpj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.service.KakaoMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoMemberController {
    private final KakaoMemberService kakaoMemberService;

    @Transactional
    @GetMapping("/oauth/kakao")
    public Member kakaoLogin(@RequestParam String code, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        log.info("code + {}" , code);

        return kakaoMemberService.kakaoLogin(code, httpServletResponse);
    }
}
