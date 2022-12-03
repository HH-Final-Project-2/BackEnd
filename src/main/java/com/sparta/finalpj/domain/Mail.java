package com.sparta.finalpj.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@RequiredArgsConstructor
public class Mail extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;//요청받은 이메일
    @Column
    private String code;//발급한 코드

    public Mail(String email, String code) {
        this.email = email;
        this.code = code;
    }
    public void update(String code){
        this.code = code;
    }

    public static Mail of(String email, String code) {
        return new Mail(email, code);
    }
}
