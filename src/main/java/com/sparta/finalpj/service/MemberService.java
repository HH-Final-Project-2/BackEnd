package com.sparta.finalpj.service;



import com.sparta.finalpj.controller.request.member.*;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.member.SignupResponseDto;
import com.sparta.finalpj.domain.Mail;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.*;
import com.sparta.finalpj.repository.MailRepository;
import com.sparta.finalpj.repository.MemberRepository;
import com.sparta.finalpj.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final MailRepository mailRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Validation validation;

    //============ 회원가입
    @Transactional
    public ResponseDto<?> signupMember(SignupRequestDto requestDto) {
        validation.validateSignUpInput(requestDto);  //유효성 검사
        //이메일 중복확인
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
        //멤버 객체 생성
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .build();
        //저장
        memberRepository.save(member);
        //인증코드 제거
        Mail mail = mailService.isPresentMail(requestDto.getEmail());
        if(mail != null){
            mailRepository.delete(mail);
        }
        return ResponseDto.success(
                SignupResponseDto.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .build()
        );
    }

    //============ 이메일 중복 확인
    @Transactional
    public ResponseDto<?> emailCheck(EmailCheckRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
        return ResponseDto.success("사용가능한 이메일입니다.");
    }


    //============ 로그인 기능
    @Transactional
    public ResponseDto<?> loginMember(LoginRequestDto requestDto, HttpServletResponse response) {
        //해당 이메일이 있는지 조회
        validation.emailCheck(requestDto.getEmail());
        //해당 member email로 조회
        Member member = validation.getPresentEmail(requestDto.getEmail());
        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        //토큰 발급
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenDto.tokenToHeaders(response);

        return ResponseDto.success(
                SignupResponseDto.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .build()
        );
    }
    //============ 회원탈퇴 기능
    @Transactional
    public ResponseDto<?> withdrawMember(HttpServletRequest request) {

        Member member = validation.validateMemberToAccess(request);
        if (null == member) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        //해당 member의 RefreshToken 제거
        tokenProvider.deleteRefreshToken(member);
        //Member 제거
        memberRepository.delete(member);

        return ResponseDto.success("success");
    }

    //============ 로그아웃 기능
    public ResponseDto<?> logoutMember(HttpServletRequest request) {

        Member member = validation.validateMemberToAccess(request);
        if (null == member) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        //RefreshToken 제거
        tokenProvider.deleteRefreshToken(member);
        return ResponseDto.success("success");
    }


//    //============ 재발급
//    public ResponseDto<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
//        Member requestingMember = validation.validateMemberToRefresh(request); //리프레시 토큰 검사 및 멤버 객체 불러오기
//        long accessTokenExpire = Long.parseLong(request.getHeader("Access-Token-Expire-Time"));
//        long now = (new Date().getTime());
//
//
//        if (now > accessTokenExpire) {     //액세스 토큰 만료여부 검사
////            tokenProvider.deleteRefreshToken(requestingMember);//만료되었으면 리프레시삭제
////            throw new CustomException(ErrorCode.INVALID_TOKEN_DELETE);}
//            RefreshToken refreshTokenConfirm = refreshTokenRepository.findByMember(requestingMember).orElse(null);
//            if (refreshTokenConfirm == null) {
//                throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
//            }
//            else if (Objects.equals(refreshTokenConfirm.getValue(), request.getHeader("Refresh-Token"))) {
//                TokenDto tokenDto = tokenProvider.generateAccessTokenDto(requestingMember);
//                validation.accessTokenToHeaders(tokenDto, response);
//                return new ResponseEntity<>(Message.success("ACCESS_TOKEN_REISSUE"), HttpStatus.OK);
//            } else {
//                tokenProvider.deleteRefreshToken(requestingMember);
//                throw new CustomException(ErrorCode.INVALID_TOKEN);
//            }
//        }
//    }

    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);
        return optionalRefreshToken.orElse(null);
    }

    // 내 프로필 조회
    public ResponseDto<?> myProfile(HttpServletRequest request) {
        Member member = validation.validateMemberToAccess(request);
        if (null == member) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        return ResponseDto.success(
                SignupResponseDto.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .build()
        );
    }

    // 닉네임 수정
    @Transactional
    public ResponseDto<?> updateMember(MemberUpdateRequestDto memberRequestDto, HttpServletRequest request){
        Member member = validation.validateMemberToAccess(request);
        if (null == member) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        member.updateProfile(memberRequestDto);
//        // 저장
//        memberRepository.save(member);

        return ResponseDto.success(
                SignupResponseDto.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .build()
        );
    }
    @Transactional
    public ResponseDto<?> updatePassword(PasswordFindDto passwordFindDto) {
        if (passwordFindDto.getPassword() == null || passwordFindDto.getPasswordCheck() == null) {
            throw new CustomException(ErrorCode.PASSWORD_NULL_INPUT_ERROR);
        }
        //비밀번호 유효성 검사
        validation.validatePasswordInput(passwordFindDto.getPassword(),passwordFindDto.getPasswordCheck());
        //해당 이메일이 있는지 조회
        validation.emailCheck(passwordFindDto.getEmail());
        //해당 member email로 조회
        Member member = validation.getPresentEmail(passwordFindDto.getEmail());

        Mail mail = mailService.isPresentMail(passwordFindDto.getEmail()); //DB조회
        if (null == mail) {
            throw new CustomException(ErrorCode.AUTH_CODE_NOT_ISSUE);
        }
        if (!mail.getCode().equals(passwordFindDto.getCode())) {
            throw new CustomException(ErrorCode.AUTH_CODE_NOT_CORRECT);
        }
        //인증코드 제거
        mailRepository.delete(mail);

        member.updatePassword(passwordEncoder.encode(passwordFindDto.getPassword()));

        return ResponseDto.success("비밀번호 수정완료. 새로운 비밀번호로 로그인 해주세요.");
    }


}
