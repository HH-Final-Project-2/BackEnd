package com.sparta.finalpj.chatting.chat;

import com.amazonaws.services.kms.model.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.finalpj.chatting.chat.requestDto.ChatMessageDto;
import com.sparta.finalpj.chatting.chat.responseDto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // ChatMessageDto 객채로 맵핑
            ChatMessageDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);

            // Websocket 구독자에게 채팅 메시지 Send
            MessageResponseDto messageResponseDto = new MessageResponseDto(roomMessage);
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), messageResponseDto);

        } catch (Exception e) {
            throw new NotFoundException("메세지를 확인할 수 없습니다.");
        }
    }
}