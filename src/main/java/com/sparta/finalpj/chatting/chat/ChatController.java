package com.sparta.finalpj.chatting.chat;

import com.amazonaws.services.kms.model.NotFoundException;

import com.sparta.finalpj.chatting.chat.requestDto.ChatMessageDto;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.JwtDecoder;
import com.sparta.finalpj.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;
    private final JwtDecoder jwtDecoder;
    private final MemberRepository memberRepository;

    /**
     * websocket "/pub/chat/enter"로 들어오는 메시징을 처리한다.
     * 채팅방에 입장했을 경우
     */
    @MessageMapping("/chat/enter")
    public void enter(ChatMessageDto chatMessageDto, @Header("Authorization") String token) {
        String email = jwtDecoder.decodeEmail(token);
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        chatService.enter(member.getId(), chatMessageDto.getRoomId());
    }

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto chatMessageDto, @Header("Authorization") String token) {
        String email = jwtDecoder.decodeEmail(token);
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        chatService.sendMessage(chatMessageDto, member);
        chatService.updateUnReadMessageCount(chatMessageDto);
    }

}