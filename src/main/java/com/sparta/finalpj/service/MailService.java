package com.sparta.finalpj.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.sparta.finalpj.controller.request.EmailAuthRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Mail;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.MailRepository;
import com.sparta.finalpj.util.RedisUtil;
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

    private final RedisUtil redisUtil;
    private String ePw; // 인증번호

    private final MailRepository mailRepository;

    public enum EmailType {
        SIGNUP, FINDPW
    }


//    MIME 이란?
//
//    전자우편을 위한 인터넷 표준 포맷. 전자우편은 7비트 아스키 문자를 사용하여 전송되기 때문에, 8비트 이상의 코드를 사용하는 문자나 이진 파일들은 MIME 포맷으로 변환되어 SMTP로 전송된다.

//    *MimeMessage 대신 SimpleMailMessage를 사용할 수도 있습니다.
//    둘의 차이점은 MimeMessage의 경우 멀티파트 데이터를 처리 할 수 있고 SimpleMailMessage는 단순한 텍스트 데이터만 전송이 가능합니다.


    // 회원가입 인증 메일 내용 작성
    public MimeMessage createMessage(String to, EmailType emailType) throws MessagingException, UnsupportedEncodingException {
//		System.out.println("보내는 대상 : " + to);
//		System.out.println("인증 번호 : " + ePw);

        String Authname = "";
        if (emailType == EmailType.SIGNUP) {
            Authname = "회원가입";
        } else if (emailType == EmailType.FINDPW) {
            Authname = "비밀번호 확인";
        }
        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(RecipientType.TO, to);// 보내는 대상
        message.setSubject("Businus " + Authname + " 이메일 인증");// 제목

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> Businus 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 " + Authname + " 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>감사합니다!<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>" + Authname + " 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += ePw + "</strong><div><br/> "; // 메일에 인증번호 넣기
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("bum4321@naver.com", "Businus 관리자"));// 보내는 사람

        return message;
    }


//    String 클래스의 인스턴스는 한 번 생성되면 그 값을 읽기만 할 수 있고, 변경할 수는 없습니다.
//    하지만 StringBuffer 클래스의 인스턴스는 그 값을 변경할 수도 있고, 추가할 수도 있습니다.
//
//    이를 위해 StringBuffer 클래스는 내부적으로 버퍼(buffer)라고 하는 독립적인 공간을 가집니다.
//    버퍼 크기의 기본값은 16개의 문자를 저장할 수 있는 크기이며, 생성자를 통해 그 크기를 별도로 설정할 수도 있습니다.


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
    public ResponseDto<?> sendSimpleMessage(String to, EmailType emailType) throws Exception {

        ePw = createKey(); // 랜덤 인증번호 생성

        // TODO Auto-generated method stub
        MimeMessage message = createMessage(to, emailType); // 메일 발송
        try {// 예외처리
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new CustomException(ErrorCode.INVALID_EMAIL_ERROR);
        }
        String authkey = redisUtil.getData(to);
        if (null == authkey) { //인증코드 최초 발송
            // 유효 시간(10분)동안 {email, authKey} 저장
            redisUtil.setDataExpire(to, ePw, 60 * 10L);

            return ResponseDto.success("인증코드 발송 완료"); // 메일로 보냈던 인증 코드를 서버로 반환
        }
        //이전에 코드를 보냈던 메일이면, 인증번호 갱신
        redisUtil.deleteData(to);
        redisUtil.setDataExpire(to, ePw, 60 * 10L);

        return ResponseDto.success("인증코드 재발송 완료"); // 메일로 보냈던 인증 코드를 서버로 반환
        }

    public ResponseDto<?> mailConfirm(EmailAuthRequestDto requestDto) {
//        Mail mail = isPresentMail(requestDto.getEmail()); //DB조회
        String authkey = redisUtil.getData(requestDto.getEmail());
        if (null == authkey) {
            throw new CustomException(ErrorCode.AUTH_CODE_NOT_ISSUE);
        }

        if (!authkey.equals(requestDto.getCode())) {
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
