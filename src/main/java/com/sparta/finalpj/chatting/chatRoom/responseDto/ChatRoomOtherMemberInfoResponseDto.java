package com.sparta.finalpj.chatting.chatRoom.responseDto;

import com.sparta.finalpj.domain.Member;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatRoomOtherMemberInfoResponseDto {

    private Long otherUserId;
    private String otherNickname;

    public ChatRoomOtherMemberInfoResponseDto(Member otherUser) {
        this.otherUserId = otherUser.getId();
        this.otherNickname = otherUser.getNickname();
    }
}
