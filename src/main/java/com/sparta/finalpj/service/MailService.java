package com.sparta.finalpj.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.sparta.finalpj.controller.request.EmailAuthRequestDto;
import com.sparta.finalpj.controller.request.EmailConfirmRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Mail;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Post;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MailService {

    @Autowired
    JavaMailSender emailsender; // Bean 등록해둔 MailConfig 를 emailsender 라는 이름으로 autowired

    private String ePw; // 인증번호

    private final MailRepository mailRepository;

    // 메일 내용 작성
    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
//		System.out.println("보내는 대상 : " + to);
//		System.out.println("인증 번호 : " + ePw);

        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);// 보내는 대상
        message.setSubject("Businus 회원가입 이메일 인증");// 제목

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> Businus 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>감사합니다!<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> "; // 메일에 인증번호 넣기
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("bum4321@naver.com", "Businus 관리자"));// 보내는 사람

        return message;
    }

    // 랜덤 인증 코드 전송
    public String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤, rnd 값에 따라서 아래 switch 문이 실행됨

            switch (index) {
                case 0:
                    key.append((char) (rnd.nextInt(26) + 97));
                    // a~z (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) (rnd.nextInt(26) + 65));
                    // A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }

        return key.toString();
    }

    // 메일 발송
    // sendSimpleMessage 의 매개변수로 들어온 to 는 곧 이메일 주소가 되고,
    // MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다.
    // 그리고 bean 으로 등록해둔 javaMail 객체를 사용해서 이메일 send!!
    @Transactional
    public ResponseDto<?> sendSimpleMessage(String to) throws Exception {

        ePw = createKey(); // 랜덤 인증번호 생성

        // TODO Auto-generated method stub
        MimeMessage message = createMessage(to); // 메일 발송
        try {// 예외처리
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_EMAIL_ERROR);
        }

        Mail mail = isPresentMail(to); //이전에 인증했던 메일인지 DB조회
        if (null == mail) {
            //(메일-인증번호) 객체 생성
            mail = Mail.of(to,ePw);
            //저장
            mailRepository.save(mail);
            return ResponseDto.success("인증코드 발송 완료"); // 메일로 보냈던 인증 코드를 서버로 반환
        }
            //이전에 코드를 보냈던 메일이면, 인증번호 갱신
            mail.update(ePw);
        return ResponseDto.success("인증코드 재발송 완료"); // 메일로 보냈던 인증 코드를 서버로 반환
    }

    public ResponseDto<?> mailConfirm(EmailAuthRequestDto requestDto){
        Mail mail = isPresentMail(requestDto.getEmail()); //DB조회
        if (null == mail) {
            throw new CustomException(ErrorCode.AUTH_CODE_NOT_ISSUE);
        }

        if (!mail.getCode().equals(requestDto.getCode())) {
            throw new CustomException(ErrorCode.AUTH_CODE_NOT_CORRECT);
        }
        return ResponseDto.success("인증 완료");
    }

    @Transactional(readOnly = true)
    public Mail isPresentMail(String email) {
        Optional<Mail> optionalMail = mailRepository.findByEmail(email);
        return optionalMail.orElse(null);
    }
}
