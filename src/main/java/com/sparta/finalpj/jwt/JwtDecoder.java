package com.sparta.finalpj.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;

@Component
public class JwtDecoder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public JwtDecoder(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    private static Key key;

    public String decodeEmail(String token) {

        String jwt = resolveToken(token);

        String email = "";
        try {
            email = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody().getSubject();

            //Date expiration = claims.get("exp", Date.class);
            //String data = claims.get("data", String.class);
        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
            System.out.println(e);
        } catch (Exception e) { // 그외 에러났을 경우
            System.out.println(e);
        }
        return email;
    }

    private String resolveToken(String token) {
        // 접두사 분리
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {// 값이 있고 && 앞 철자가 Bearer 일때 true
            return token.substring(7);// <"Bearer " + 토큰 정보> 에서 "Bearer " 를 떼어냄
        }
        return null;
    }

}