package com.sparta.finalpj.configuration;

import com.sparta.finalpj.jwt.JwtFilter;
import com.sparta.finalpj.jwt.TokenProvider;
import com.sparta.finalpj.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${jwt.secret}")
    String SECRET_KEY;

    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    // 암호화 알고리즘 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // corsConfigurationSource 포트 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // origin
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.addAllowedOrigin("http://localhost:3000");
        // method
        configuration.setAllowedMethods(Arrays.asList("*"));
        // header
        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        configuration.setExposedHeaders(Arrays.asList("Authorization", "Refresh-Token" , "Access_Control-Allow-Origin"));

        // Todo :: Security Config -> 허용할 포트만 열어두기!!
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // Todo :: filterChain (권한 제한 걸어주기)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource());
        http
                .httpBasic().disable()
                .csrf().disable()
                .addFilterBefore(new JwtFilter(SECRET_KEY,tokenProvider,userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        http
                .headers().frameOptions().disable() // h2-console 화면을 사용하기 위해 해당 옵션들을 disable 합니다.

                .and()
                .authorizeRequests() // URL 별 권한 관리를 설정하는 옵션의 시작점이다.
                // authorizeRequests 가 선언되어야만 antMatchers 옵션을 사용할 수 있다.

                .antMatchers("/css/**", "/images/**", "/js/**", "/h2-console/**","/login/**","/favicon.ico").permitAll()
                .antMatchers("/v2/**","/v2/api-docs", "/swagger-resources/**",
                        "/swagger-ui.html/**","/swagger-ui/**","/swagger*/**","/v3/api-docs").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/sub/**").permitAll()
                .antMatchers("/pub/**").permitAll()
                .antMatchers("/chat/**").permitAll()
                .antMatchers("/ws-stomp/**").permitAll()
                .antMatchers("/wss-stomp/**").permitAll()
                .antMatchers("/stomp/**").permitAll()
                .antMatchers("/websocket/**").permitAll()
                //.antMatchers("/api/v1/**").hasRole(Role.USER.name())
                // .antMatchers -> 권한 관리 대상을 지정하는 옵션이다.
                // URL, HTTP 메소드별로 관리가 가능하다.
                // "/" 등 지정된 URL 들은 permitAll() 옵션을 통해 전체 열람 권한을 주었습니다.
                // POST 메소드이면서 "/api/v1/**" 주소를 가진 API 는 USER 권한을 가진 사람만 가능하도록 했다.

                .antMatchers("/api/**").permitAll()


                .anyRequest().authenticated(); // 설정된 값들 이외 나머지 URL 들을 나타낸다. (인증된 사용자들만)

        // 리소스 서버 (즉, 소셜 서비스들) 에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있습니다.

        return http.build();

    }
}
