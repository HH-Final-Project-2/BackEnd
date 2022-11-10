package com.sparta.finalpj.configuration;


import com.sparta.finalpj.jwt.JwtFilter;
import com.sparta.finalpj.jwt.TokenProvider;
import com.sparta.finalpj.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfiguration
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;


    //JWT 필터를 Security 로직에 필터를 등록할때 사용하는 메서드
    @Override
    public void configure(HttpSecurity httpSecurity){
        JwtFilter customJwtFilter = new JwtFilter(SECRET_KEY,tokenProvider,userDetailsService);
        httpSecurity.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

}