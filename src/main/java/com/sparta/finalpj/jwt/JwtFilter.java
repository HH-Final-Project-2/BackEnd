package com.sparta.finalpj.jwt;

import com.sparta.finalpj.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.sparta.finalpj.exception.ErrorCode.INVALID_TOKEN;
import static com.sparta.finalpj.exception.ErrorCode.UNAUTHORIZED;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static String AUTHORIZATION_HEADER = "Authorization";
    public static String BEARER_PREFIX = "Bearer ";

    public static String AUTHORITIES_KEY = "auth";

    private final String SECRET_KEY;

    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;


    //실제 필터링 로직
    //JWT토큰의 인증정보를 현재 실행중인 시큐리티 컨텍스트에 저장하기 위한 역할
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // 1) HttpServletRequest request 에서 Header(jwtToken)을 획득한다.
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) { // jwtToken 에 값이 있고, 토큰 유효성 검증을 통과했을때..
            Claims claims;
            try {
                claims = Jwts.parserBuilder().setSigningKey(key).build().
                        parseClaimsJws(jwt).getBody();
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();
            }

            if (claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()) {
                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter().println(
//                        new ObjectMapper().writeValueAsString(
////                                ResponseDto.fail("BAD_REQUEST", "Token이 유효하지 않습니다.")
//                                ResponseDto.fail(INVALID_TOKEN)
//                                )
//                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            String subject = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = userDetailsService.loadUserByUsername(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authorities); // jwtToken 으로 부터 Authentication 객체 얻어오기
            SecurityContextHolder.getContext().setAuthentication(authentication); // 받아온 Authentication 객체 SecurityContextHolder 에 저장

        }

        filterChain.doFilter(request, response);
    }

    // header 에서 토큰을 뽑는 메서드
    private String resolveToken(HttpServletRequest request) {
        // authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); // Client 에서 accessToken 을 받아 올때 Authorization
        // 접두사 분리
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {// 값이 있고 && 앞 철자가 Bearer 일때 true
            return bearerToken.substring(7);// "Bearer " + 토큰 정보에서 "Bearer " 를 땜
        }
        return null;
    }

}
