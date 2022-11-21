package com.sparta.finalpj.chatting.chat;

import com.sparta.finalpj.chatting.chatRoom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findAllByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
    List<ChatMessage> findAllByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

}
