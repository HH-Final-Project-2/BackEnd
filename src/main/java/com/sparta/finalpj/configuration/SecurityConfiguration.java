//package com.sparta.finalpj.configuration;
//
//import com.sparta.finalpj.exception.AccessDeniedHandlerException;
//import com.sparta.finalpj.exception.AuthenticationEntryPointException;
//import com.sparta.finalpj.jwt.TokenProvider;
//import com.sparta.finalpj.service.UserDetailsServiceImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@ConditionalOnDefaultWebSecurity
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
//public class SecurityConfiguration {
//
//    @Value("${jwt.secret}")
//    String SECRET_KEY;
//    private final TokenProvider tokenProvider;
//    private final UserDetailsServiceImpl userDetailsService;
//    private final AuthenticationEntryPointException authenticationEntryPointException;
//    private final AccessDeniedHandlerException accessDeniedHandlerException;
//
//    // 암호화 알고리즘 빈 등록
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    @Order(SecurityProperties.BASIC_AUTH_ORDER)
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors().disable();
//
//        http.csrf().disable()
//                //Exception을 핸들링할때 사용할 클래스들을 추가
//                .exceptionHandling()
//                .authenticationEntryPoint(authenticationEntryPointException)
//                .accessDeniedHandler(accessDeniedHandlerException)
//
//                .and()
//                // 세션 필요하다면 사용
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                .and()
//                .headers()
//                .frameOptions().sameOrigin()
//
//                // 인증 없이 사용 가능한 api
//                .and()
//                .authorizeRequests()
////                .antMatchers("/member/**").permitAll()
//                .antMatchers("/**").permitAll()
//
//                // 이외에는 모두 인증 필요
//                .anyRequest().authenticated()
//
//                .and()
//                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));
//
//        return http.build();
//    }
//
//}