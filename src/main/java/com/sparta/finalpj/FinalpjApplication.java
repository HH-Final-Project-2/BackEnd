package com.sparta.finalpj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //타임스탬프 상속해서 쓰려면 넣어줘야함
public class FinalpjApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalpjApplication.class, args);
    }

}
