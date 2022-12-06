package com.sparta.finalpj.chatting.chat;


import com.sparta.finalpj.chatting.chat.requestDto.ChatMessageDto;
import com.sparta.finalpj.chatting.chatRoom.ChatRoom;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Entity
@NoArgsConstructor
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 메세지 작성자
    @ManyToOne
    private Member member;

    // 채팅 메세지 내용
    @Size(max = 1000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    public ChatMessage(Member member, ChatMessageDto chatMessageDto, ChatRoom chatRoom) {
        this.member = member;
        this.message = chatMessageDto.getMessage();
        this.chatRoom = chatRoom;
    }
}
