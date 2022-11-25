package com.sparta.finalpj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing //타임스탬프 상속해서 쓰려면 넣어줘야함
public class FinalpjApplication {
    @PostConstruct //@PostConstruct는 Bean이 완전히 초기화 된 후, 단 한번만 호출되는 메서드 이다.
    //애플리케이션이 처음 구동될때 한번 실행된다
    public void started() {
        // timezone 셋팅
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
    public static void main(String[] args) {
        SpringApplication.run(FinalpjApplication.class, args);
    }

}
