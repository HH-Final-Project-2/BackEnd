package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.EmailAuthRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.jwt.Validation;
import com.sparta.finalpj.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final Validation validation;


    @PostMapping("/api/mail/auth")
    public ResponseDto<?> mailAuth(@RequestBody EmailAuthRequestDto requestDto) throws Exception {
        validation.validateEmailInput(requestDto);
        validation.emailDupCheck(requestDto);
        return mailService.sendSimpleMessage(requestDto.getEmail(), MailService.EmailType.SIGNUP);
    }

    @PostMapping("/api/mail/confirm")
    public ResponseDto<?> mailConfirm(@RequestBody EmailAuthRequestDto requestDto){
        return mailService.mailConfirm(requestDto);
    }

    @PostMapping("/api/mail/pw")
    public ResponseDto<?> pwFindAuth(@RequestBody EmailAuthRequestDto requestDto) throws Exception {
        validation.validateEmailInput(requestDto);
        validation.emailCheck(requestDto.getEmail());
        return mailService.sendSimpleMessage(requestDto.getEmail(),MailService.EmailType.FINDPW);
    }


}
