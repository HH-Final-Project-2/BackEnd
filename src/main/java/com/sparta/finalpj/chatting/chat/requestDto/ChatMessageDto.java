package com.sparta.finalpj.chatting.chat.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto implements Serializable {

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        TALK, UNREAD_MESSAGE_COUNT_ALARM
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 공통으로 만들어진 방 번호
    private Long otherMemberId; // 상대방

    @NotBlank
    @Size(max=2000)
    private String message; // 메시지

    private String createdAt;

    private Long userId;
    private int count;

    public ChatMessageDto(ChatMessageDto chatMessageDto, int count) {
        this.type = MessageType.UNREAD_MESSAGE_COUNT_ALARM; // 메시지 타입
        this.roomId = chatMessageDto.roomId; // 방 이름
        this.otherMemberId = chatMessageDto.otherMemberId; // 상대방 prvateKey
        this.count = count; //안읽은 메세지 개수
    }
}
