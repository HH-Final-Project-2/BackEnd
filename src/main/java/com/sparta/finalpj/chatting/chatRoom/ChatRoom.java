package com.sparta.finalpj.chatting.chatRoom;

import com.sparta.finalpj.chatting.chat.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String chatRoomUuid = UUID.randomUUID().toString();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    private int roomHashCode;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    public ChatRoom(int roomUsers) {
        this.roomHashCode = roomUsers;
    }

}
