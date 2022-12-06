package com.sparta.finalpj.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.finalpj.controller.request.member.MemberUpdateRequestDto;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
    @Column(unique = true)
    private Long kakaoId;//카카오 ID
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MyCalendar> myCalendar;
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> post;
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Card> card;
//    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private MyCard myCard;

    public Member(String email, String username, String nickname, String encodedPassword, Long kakaoId) {
        this.email = email;
        this.nickname = nickname;
        this.username = username;
        this.password = encodedPassword;
        this.kakaoId = kakaoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Member member = (Member) o;
        return id != null && Objects.equals(id, member.id);
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public void updateProfile(MemberUpdateRequestDto memberRequestDto) {
        if (memberRequestDto.getNickname() == null) {
            throw new CustomException(ErrorCode.NICKNAME_FORM_ERROR);
        }
        this.nickname = memberRequestDto.getNickname();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
