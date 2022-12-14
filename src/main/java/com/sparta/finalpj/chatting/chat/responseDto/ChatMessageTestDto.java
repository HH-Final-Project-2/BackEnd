package com.sparta.finalpj.chatting.chat.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.finalpj.chatting.chat.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ChatMessageTestDto {

    private Long userId;
    private String nickname;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd a hh:mm")
    private LocalDateTime createdAt;

    public ChatMessageTestDto(ChatMessage chatMessage) {

        if(chatMessage.getMember() == null){
            this.userId = null;
        }else{
            this.userId = chatMessage.getMember().getId();
        }
        if(chatMessage.getMember() == null){
            this.nickname = "탈퇴한 계정입니다.";
        }else{
            this.nickname = chatMessage.getMember().getNickname();
        }

        this.message = chatMessage.getMessage();
        this.createdAt = chatMessage.getCreatedAt();
    }
}
