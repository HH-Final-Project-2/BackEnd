package com.sparta.finalpj.jwt;

import com.sparta.finalpj.controller.request.EmailAuthRequestDto;
import com.sparta.finalpj.controller.request.member.SignupRequestDto;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.MemberRepository;
import com.sparta.finalpj.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class Validation {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public void validateSignUpInput(SignupRequestDto signupRequestDto) {
        //필수 입력사항에 대한 입력 여부 확인
        if(hasNullDtoField(signupRequestDto)){
            throw new CustomException(ErrorCode.NULL_INPUT_ERROR);
        }
        //2~5자 이내 , 한글
        if (!isValidUsername(signupRequestDto.getUsername())) {
            throw new CustomException(ErrorCode.SIGNUP_USERNAME_FORM_ERROR);
        }
        //2~10자 이내 , 한글,영어,숫자
        if (!isValidNickname(signupRequestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_FORM_ERROR);
        }
        //이메일형식 유효성 검사
        if (!isValidEmail(signupRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.SIGNUP_EMAIL_FORM_ERROR);
        }
        validatePasswordInput(signupRequestDto.getPassword(),signupRequestDto.getPasswordCheck());

    }
    public void validatePasswordInput(String password, String passwordCheck) {
        //password 는 passwordCheck 와 동일한지 검사
        if (!isValidPasswordCheck(password,passwordCheck)) {
            throw new CustomException(ErrorCode.SIGNUP_PASSWORD_CHECK_ERROR);
        }
        //비밀번호는 6자 ~ 15자 , 영문 , 숫자
        if (!isValidPassword(password)) {
            throw new CustomException(ErrorCode.SIGNUP_PASSWORD_FORM_ERROR);
        }
        //비밀번호확인도 6자 ~ 15자 , 영문 , 숫자
        if (!isValidPasswordCheck(passwordCheck)) {
            throw new CustomException(ErrorCode.SIGNUP_PASSWORD_FORM_ERROR);
        }
    }


    public void validateEmailInput(EmailAuthRequestDto emailAuthRequestDto) {
        if(emailAuthRequestDto.getEmail()==null){
            throw new CustomException(ErrorCode.EMAIL_NULL_INPUT_ERROR);
        }
        if (!isValidEmail(emailAuthRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_INPUT_ERROR);
        }
    }

    public void emailDupCheck(EmailAuthRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
    }
    public void emailCheck(String email) {
        if (!memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    private boolean hasNullDtoField(SignupRequestDto signupRequestDto) {
        //<논리연산자 ||>하나라도 true인 경우 true 반환
        return  signupRequestDto.getUsername() == null ||
                signupRequestDto.getEmail() == null ||
                signupRequestDto.getNickname() == null ||
                signupRequestDto.getPassword() == null ||
                signupRequestDto.getPasswordCheck() == null;
    }
    private boolean isValidPassword(String password) {
        // 6자 ~ 15자 , 영문 , 숫자
        String pattern = "^[A-Za-z0-9]{6,15}$";
        return Pattern.matches(pattern, password);
    }
    private boolean isValidPasswordCheck(String passwordcheck) {
        // 6자 ~ 15자 , 영문 , 숫자
        String pattern = "^[A-Za-z0-9]{6,15}$";
        return Pattern.matches(pattern, passwordcheck);
    }
    private boolean isValidPasswordCheck(String password, String passwordCheck) {
        //password 는 passwordCheck 와 동일해야 한다.
        return password.equals(passwordCheck);
    }

    public boolean isValidEmail(String email) {
        //이메일 형식
        String pattern = "\\w+@\\w+\\.\\w+(\\.\\w+)?";
        return Pattern.matches(pattern, email);
    }

    public boolean isValidUsername(String username) {
        //2~5자 이내 , 한글
        String pattern = "^[가-힣]{2,5}$";
        return Pattern.matches(pattern, username);
    }
    public boolean isValidNickname(String nickname) {
        //2~10자 이내 , 한글,영어,숫자
        String pattern = "^[가-힣][A-Za-z0-9]{2,10}$";
        return Pattern.matches(pattern, nickname);
    }

    public void checkAccessToken (HttpServletRequest request, Member member){
        if (!tokenProvider.validateToken(request.getHeader("Authorization").substring(7)))
            throw new CustomException(ErrorCode.TOKEN_IS_EXPIRED);
        if (null == member) throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public Member getPresentEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    @Transactional
    public Member validateMemberToRefresh(HttpServletRequest request) {
        //리프레시 토큰 검사
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
//            return null;
        }
        //Authentication에서 멤버 객체 불러오기
        return tokenProvider.getMemberFromAuthentication();
    }

    @Transactional
    public Member validateMemberToAccess(HttpServletRequest request) {
        //액세스 토큰 검사
        if (!tokenProvider.validateToken(request.getHeader("Authorization").substring(7))) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
//            return null;
        }
        //Authentication에서 멤버 객체 불러오기
        return tokenProvider.getMemberFromAuthentication();
    }


    public void accessTokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }


    public RefreshToken getDeleteToken(Member member) {
        Optional<RefreshToken> optionalMember = refreshTokenRepository.findByMember(member);
        return optionalMember.orElseThrow(()->new IllegalArgumentException("INVALID_TOKEN"));
    }

}


