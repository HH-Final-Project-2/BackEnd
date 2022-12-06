package com.sparta.finalpj.chatting.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@RequiredArgsConstructor
@Service
public class RedisRepository {

    public static final String MEMBER_INFO = "MEMBER_INFO"; // session id와 member id를 mapping
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    // 유저가 입장한 채팅방 정보 저장
    // "ENTER_INFO", "member id", "room id"
    @Resource(name = "redisTemplate")
    private HashOperations<String, Long, String> chatRoomUserEnteredRoomInfo;

    // 채팅방 마다 유저가 안 읽은 메세지 갯수에 대한 정보 저장
    @Resource(name = "redisTemplate")
    // roomUuid, memberId, 안 읽은 메세지 갯수
    private HashOperations<String, Long, Integer> chatRoomUnReadMessageInfo;

    // 나의 대화상대 정보를 session id로 확인
    @Resource(name = "redisTemplate")
    // MEMBER_INFO, sessionId, memberId
    private HashOperations<String, String, Long> memberInfo;


    // step1
    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void memberEnterRoomInfo(Long memberId, String roomUuid) {
        chatRoomUserEnteredRoomInfo.put(ENTER_INFO, memberId, roomUuid);
    }

    // 사용자가 채팅방에 입장해 있는지 확인
    public boolean existChatRoomUserInfo(Long memberId) {
        return chatRoomUserEnteredRoomInfo.hasKey(ENTER_INFO, memberId);
    }

    // 사용자가 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(Long memberId) {
        return chatRoomUserEnteredRoomInfo.get(ENTER_INFO, memberId);
    }

    // 사용자가 입장해 있는 채팅방 ID 조회
    public void exitUserEnterRoomId(Long memberId) {
        chatRoomUserEnteredRoomInfo.delete(ENTER_INFO, memberId);
    }

    // step2
    // 채팅방에서 사용자가 읽지 않은 메세지의 갯수 초기화
    public void initChatRoomMessageInfo(String roomUuid, Long memberId) {
        System.out.println("roomId>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+roomUuid);
        System.out.println("memberId>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+memberId);
        chatRoomUnReadMessageInfo.put(roomUuid, memberId, 0);
        System.out.println("안읽은 메시지 업데이트했음 "+getChatRoomMessageCount(roomUuid, memberId));
    }

    // 채팅방에서 사용자가 읽지 않은 메세지의 갯수 추가
    public void addChatRoomMessageCount(String roomUuid, Long memberId) {
        chatRoomUnReadMessageInfo.put(roomUuid, memberId, chatRoomUnReadMessageInfo.get(roomUuid, memberId) + 1);
    }

    //
    public int getChatRoomMessageCount(String roomUuid, Long memberId) {
        if(chatRoomUnReadMessageInfo.get(roomUuid, memberId)==null){
            return 0;
        }
        else{
            return chatRoomUnReadMessageInfo.get(roomUuid, memberId);
        }
    }

    // step3
    // 나의 대화상대 정보 저장
    public void saveMyInfo(String sessionId, Long memberId) {
        memberInfo.put(MEMBER_INFO, sessionId, memberId);
    }

    public boolean existMyInfo(String sessionId) {
        return memberInfo.hasKey(MEMBER_INFO, sessionId);
    }

    public Long getMyInfo(String sessionId) {
        return memberInfo.get(MEMBER_INFO, sessionId);
    }

    // 나의 대화상대 정보 삭제
    public void deleteMyInfo(String sessionId) {
        memberInfo.delete(MEMBER_INFO, sessionId);
    }
}