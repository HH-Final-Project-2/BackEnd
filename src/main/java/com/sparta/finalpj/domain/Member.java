package com.sparta.finalpj.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String nickname;
    @Column
    private String phoneNum;//개인연락처
    @Column
    private String company;//회사
    @Column
    private String position;//직책
    @Column
    private String department;//소속부서
    @Column
    private String companyAddress;//회사주소
    @Column
    private String tel;//회사 유선전화
    @Column
    private String fax;//팩스
    @Column(nullable = false)
    @JsonIgnore
    private String password;


    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

}
