package com.sparta.finalpj.chatting.chatRoom.responseDto;

import com.sparta.finalpj.domain.Member;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatRoomOtherMemberInfoResponseDto {

    private Long otherUserId;
    private String otherNickname;
    private Long myId;
    private String myNickname;

    public ChatRoomOtherMemberInfoResponseDto(Member User, Member otherUser) {
        this.otherUserId = otherUser.getId();
        this.otherNickname = otherUser.getNickname();
        this.myId = User.getId();
        this.myNickname = User.getNickname();
    }
}
