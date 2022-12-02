package com.sparta.finalpj.chatting.chatRoom;

import com.sparta.finalpj.chatting.chat.RedisRepository;
import com.sparta.finalpj.chatting.chat.responseDto.ChatMessageTestDto;
import com.sparta.finalpj.chatting.chatRoom.requestDto.ChatRoomUserRequestDto;
import com.sparta.finalpj.chatting.chatRoom.responseDto.ChatRoomResponseDto;
import com.sparta.finalpj.chatting.chatRoom.responseDto.ChatRoomOtherMemberInfoResponseDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.domain.Post;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.jwt.UserDetailsImpl;
import com.sparta.finalpj.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final RedisRepository redisRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PostService postService;

    //방생성
    @PostMapping ("/chat/rooms")
    public ResponseDto<?> createChatRoom(
        @RequestBody ChatRoomUserRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Post post = postService.isPresentPost(requestDto.getPostId());
        if (null == post) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        Long chatPartnerUserId = post.getMember().getId();

        String chatRoomUuid = chatRoomService.createChatRoom(chatPartnerUserId, userDetails);
//        Long chatPartnerUserId = requestDto.getUserId();
        Long myUserId = userDetails.getMember().getId();

        System.out.println(userDetails.getMember().getNickname()+ "채팅방 생성 요청");
        // redis repository에 채팅방에 존재하는 사람 마다 안 읽은 메세지의 갯수 초기화
        redisRepository.initChatRoomMessageInfo(chatRoomUuid, myUserId);
        redisRepository.initChatRoomMessageInfo(chatRoomUuid, chatPartnerUserId);

        return ResponseDto.success(chatRoomUuid);
    }

    //내가 가진 채팅방 조회
    @GetMapping ("/chat/rooms")
    public List<ChatRoomResponseDto> getChatRoom (
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println(userDetails.getMember().getNickname()+ "채팅방 조회 요청");
        return chatRoomService.getChatRoom(userDetails);
    }

    //채팅방 삭제
    @DeleteMapping("chat/rooms/{roomId}")
    public void deleteChatRoom(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        //roonId=uuid
        //방번호랑 나간 사람
        System.out.println(roomId+"삭제 요청");
        System.out.println(userDetails.getMember().getNickname()+"삭제 요청");
        ChatRoom chatroom = chatRoomRepository.findByChatRoomUuid(roomId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_CHATROOM)
        );

        chatRoomService.deleteChatRoom(chatroom, userDetails.getMember());
    }

    //이전 채팅 메시지 불러오기
    @GetMapping("/chat/rooms/{roomId}/messages")
    public List<ChatMessageTestDto> getPreviousChatMessage(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        System.out.println(userDetails.getMember().getNickname()+ "채팅방 이전채팅불러오기 요청");
        return chatRoomService.getPreviousChatMessage(roomId, userDetails);
    }

    //채팅방 입장시 상대정보 조회
    @GetMapping("/chat/rooms/userInfo/{roomId}")
    public ChatRoomOtherMemberInfoResponseDto getOtherUserInfo(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        System.out.println(userDetails.getMember().getNickname()+ "채팅방 상대방정보 요청");
        return chatRoomService.getOtherUserInfo(roomId, userDetails);
    }
}
