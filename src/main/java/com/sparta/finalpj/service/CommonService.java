package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Comment;
import com.sparta.finalpj.domain.CommentHeart;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Post;
import com.sparta.finalpj.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final TokenProvider tokenProvider;
    public ResponseDto<?> loginCheck(HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        return null;
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
