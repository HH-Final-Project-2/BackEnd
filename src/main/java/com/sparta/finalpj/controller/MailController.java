package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.EmailAuthRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.jwt.Validation;
import com.sparta.finalpj.service.RegisterMail;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final RegisterMail registerMail;
    private final Validation validation;


    @PostMapping("/api/login/mailConfirm")
    public ResponseDto<?> mailConfirm(@RequestBody EmailAuthRequestDto requestDto) throws Exception {

//        String code = registerMail.sendSimpleMessage(requestDto.getEmail());
//        System.out.println("인증코드 : " + code);
        validation.validateEmailInput(requestDto);

        return registerMail.sendSimpleMessage(requestDto.getEmail());
    }
}
