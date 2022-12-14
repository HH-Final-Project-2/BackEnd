package com.sparta.finalpj.chatting.chat;

import com.sparta.finalpj.chatting.chat.requestDto.ChatMessageDto;
import com.sparta.finalpj.chatting.chatRoom.*;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisRepository redisRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    public void enter(Long memberId, String roomId) {
        // 채팅방 입장 정보 저장
        redisRepository.memberEnterRoomInfo(memberId, roomId);
        // 채팅방의 안 읽은 메세지의 수 초기화
        redisRepository.initChatRoomMessageInfo(roomId, memberId);
    }

    /**
     * 채팅 메세지보내기
     */
    @Transactional(readOnly = true)
    public ChatRoomUser isPresentChatRoomUser(Long memberId, Long otherId) {
        Optional<ChatRoomUser> chatRoomUser = chatRoomUserRepository.findByMemberIdAndChatRoomId(memberId, otherId);
        return chatRoomUser.orElse(null);
    }

    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto, Member member) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUuid(chatMessageDto.getRoomId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM)
        );
        //내 ChatRoomUser
        ChatRoomUser mychatRoomUser = isPresentChatRoomUser(member.getId(), chatRoom.getId());
        //상대방 ChatRoomUser
        ChatRoomUser chatRoomUser = isPresentChatRoomUser(mychatRoomUser.getOtherMember().getId(), chatRoom.getId());
        //상대방이 채팅방 삭제를 했다면, 생성해서 상대방 채팅방 리스트에 추가해줌
        if (chatRoomUser == null) {
            chatRoomService.existRoom(chatRoom.getRoomHashCode(), member, mychatRoomUser.getOtherMember());
        }

        String topic = channelTopic.getTopic();
        String createdAt = getCurrentTime();
        chatMessageDto.setCreatedAt(createdAt);
        chatMessageDto.setOtherMemberId(mychatRoomUser.getOtherMember().getId());
        chatMessageDto.setType(ChatMessageDto.MessageType.TALK);
        // front에서 요청해서 진행한 작업 나의 userId 넣어주기
        chatMessageDto.setUserId(member.getId());

        log.info(chatMessageDto.getMessage()); //=> test : 메세지 로그 찍어보기
        log.info(chatMessageDto.getCreatedAt()); //=> test : 메세지 로그 찍어보기

        ChatMessage chatMessage = new ChatMessage(member, chatMessageDto, chatRoom);
        chatMessageRepository.save(chatMessage);

        redisTemplate.convertAndSend(topic, chatMessageDto);
    }

    //현재시간 추출 메소드
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd a hh:mm");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);
    }

    //안읽은 메세지 업데이트
    public void updateUnReadMessageCount(ChatMessageDto requestChatMessageDto, Member member) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUuid(requestChatMessageDto.getRoomId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM)
        );
        List<ChatRoomUser> chatRoomUser = chatRoomUserRepository.findAllByMemberNotAndChatRoom(member, chatRoom);
        Long otherUserId = chatRoomUser.get(0).getMember().getId();
        String roomId = requestChatMessageDto.getRoomId();
        // 상대방이 채팅방에 들어가 있지 않거나 들어가 있어도 나와 같은 대화방이 아닌 경우 안 읽은 메세지 처리를 할 것이다.
        if (!redisRepository.existChatRoomUserInfo(otherUserId) || !redisRepository.getUserEnterRoomId(otherUserId).equals(roomId)) {
            // || : 하나라도 true인 경우 true 반환
            redisRepository.addChatRoomMessageCount(roomId, otherUserId);
//            int unReadMessageCount = redisRepository
//                .getChatRoomMessageCount(roomId, otherUserId);
//            String topic = channelTopic.getTopic();
////
//            ChatMessageDto responseChatMessageDto = new ChatMessageDto(requestChatMessageDto, unReadMessageCount);
//
//            redisTemplate.convertAndSend(topic, responseChatMessageDto);
        }
    }
}
