package com.sparta.finalpj.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.password}")
    String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.naver.com");
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        javaMailSender.setPort(465);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.trust", "smtp.naver.com"); //SSL : 인터넷에서 데이터를 안전하게 전송하기 위한 인터넷 통신 규약 프로토콜
        properties.setProperty("mail.smtp.ssl.enable", "true");

        return properties;
    }
}

//    SMTP 서버의 암호화 방식에 따라 TLS나 SSL을 사용하는데, TLS를 사용하는 경우에는 smtplib.SMTP(서버주소, 포트 번호)를, SSL을 사용하는 경우 smtplib.SMTP_SSL(서버주소, 포트 번호) 함수를 이용한다. 본문에서는 SSL 방식을 사용하므로 _SSL을 추가해준다.
//
//        또한 TLS는 포트 번호 587을 사용하고, SSL은 465를 사용한다.
////
//        근데 SSL 과 TLS 가 뭐지?
//        간단히 말해, 두 컴퓨터 사이의 연결을 암호화하는 표준 기술이다. 통신을 제3자가 엿보는 것을 방지.
//
//    네이버에서 제공하는 SMTP 서버 접속은
//
//        smtp = smtplib.SMTP_SSL('smtp.naver.com', 465) 이렇게 표현한다.
//        [출처] [day3] SMTP, MIME 사용하여 이메일 보내기|작성자 마포구 솜뭉치

