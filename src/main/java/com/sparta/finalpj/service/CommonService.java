package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Comment;
import com.sparta.finalpj.domain.CommentHeart;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Post;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
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
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if (null == request.getHeader("Authorization")) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
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
