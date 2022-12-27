package com.sparta.finalpj.controller;

import com.google.gson.Gson;
import com.sparta.finalpj.controller.request.member.LoginRequestDto;
import com.sparta.finalpj.controller.request.member.SignupRequestDto;
import com.sparta.finalpj.repository.MemberRepository;
import com.sparta.finalpj.service.KakaoMemberService;
import com.sparta.finalpj.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Nested
@DisplayName("Member Controller 테스트")
class MemberControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberService memberService;

    @Autowired
    KakaoMemberService kakaoMemberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("회원가입 테스트(이메일, 닉네임, 패스워드)")
    class SignupTest {
        @Test
        @DisplayName("회원가입 성공")
        void signup() throws Exception {

            // 회원 가입을 위한 dto
            SignupRequestDto dto = SignupRequestDto.builder()
                    .email("signuptest01@test.com")
                    .username("테스트유저")
                    .nickname("테스트닉네임")
                    .password("test1234")
                    .passwordCheck("test1234")
                    .build();

            String json = new Gson().toJson(dto); // dto 를 json 형식의 String 으로 만들기

            // api 전송
            mvc.perform(post("/api/members/signup")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력

//            Optional<Member> member = memberRepository.findByNickName("signup");
//            Optional<Member> member = memberRepository.findByEmail("signuptest@test.com");
//            Member member = memberRepository.findByEmail("signuptest@test.com").orElseThrow();
//
//            memberRepository.delete(member);
        }
    }

    @Nested
    @DisplayName("로그인 테스트(이메일, 패스워드)")
    class LoginTest {
        @Test
        @Transactional
        @DisplayName("로그인 성공")
        void login() throws Exception {

            // 로그인을 위한 dto
            LoginRequestDto loginDto = LoginRequestDto.builder()
                    .email("yusung4612@naver.com")
                    .password("dbtjd1234")
                    .build();

            // dto 를 json 형식의 String 으로 만들기
            String json = new Gson().toJson(loginDto);

            // api 전송
            mvc.perform(post("/api/members/login")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }

        @Nested
        @DisplayName("로그인 실패")
        class LoginFailed {

            @Test
            @DisplayName("이메일 유효성 없음")
            void notEmail() throws Exception {

                // 로그인을 위한 dto
                LoginRequestDto dto = LoginRequestDto.builder()
                        .email("failtest2@test.com")
                        .password("pass1234^^")
                        .build();

                // dto 를 json 형식의 String 으로 만들기
                String json = new Gson().toJson(dto);

                // api 전송
                mvc.perform(post("/tb/login")// 요청 전송
                                .with(csrf()) // 403 에러를 방지하기 위한 csrf
                                .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                                .content(json))
                        .andExpect(status().isNonAuthoritativeInformation()) // 에러 코드 반환
                        .andDo(print()); // 요청과 응답 정보 전체 출력
            }

            @Test
            @DisplayName("비밀번호 유효성 없음")
            void notPw() throws Exception {

                // 이미 존재하는 이메일
                String existEmail = "yusung4612@naver.com";

                // 로그인을 위한 dto
                LoginRequestDto dto = LoginRequestDto.builder()
                        .email(existEmail)
                        .password("failpw1234!")
                        .build();

                // dto 를 json 형식의 String 으로 만들기
                String json = new Gson().toJson(dto);

                // api 전송
                mvc.perform(post("/tb/login")// 요청 전송
                                .with(csrf()) // 403 에러를 방지하기 위한 csrf
                                .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                                .content(json))
                        .andExpect(status().isNonAuthoritativeInformation()) // 에러 코드 반환
                        .andDo(print()); // 요청과 응답 정보 전체 출력
            }
        }
    }

    @Test
    @WithUserDetails(value = "yusung4612@naver.com")
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {

        // api 전송
        mvc.perform(post("/tb/logout")// 요청 전송
                        .with(csrf())) // 403 에러를 방지하기 위한 csrf
                .andExpect(status().isOk()) // 성공 코드 반환
                .andDo(print()); // 요청과 응답 정보 전체 출력
    }

    @Nested
    @DisplayName("중복 체크")
    class existCheck {
        @Test
        @DisplayName("이메일 중복 체크")
        void idcheck() throws Exception {

            // 이미 존재하는 이메일
            String existEmail = "yusung4612@naver.com";

            // 로그인을 위한 dto
//                    IdCkeckRequestDto dto = IdCkeckRequestDto.builder()
            LoginRequestDto dto = LoginRequestDto.builder()
                    .email(existEmail)
                    .build();

            // dto 를 json 형식의 String 으로 만들기
            String json = new Gson().toJson(dto);

            // api 전송
            mvc.perform(post("/tb/signup/idcheck")// 요청 전송
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))// 403 에러를 방지하기 위한 csrf
                    .andExpect(status().isImUsed()) // 에러 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }

//                @Test
//                @DisplayName("닉네임 중복 체크")
//                void nicknamecheck() throws Exception {
//
//                    // 이미 존재하는 닉네임
//                    String existNickName = "user1";
//
//                    // 로그인을 위한 dto
//                    NickNameCheckRequestDto dto = NickNameCheckRequestDto.builder()
//                            .nickName(existNickName)
//                            .build();
//
//                    // dto 를 json 형식의 String 으로 만들기
//                    String json = new Gson().toJson(dto);
//
//                    // api 전송
//                    mvc.perform(post("/tb/signup/nicknamecheck")// 요청 전송
//                                    .with(csrf())
//                                    .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
//                                    .content(json))// 403 에러를 방지하기 위한 csrf
//                            .andExpect(status().isImUsed()) // 에러 코드 반환
//                            .andDo(print()); // 요청과 응답 정보 전체 출력
//                }
    }

}
