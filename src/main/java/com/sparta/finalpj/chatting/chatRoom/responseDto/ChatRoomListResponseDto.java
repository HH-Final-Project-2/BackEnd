package com.sparta.finalpj.chatting.chatRoom.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ChatRoomListResponseDto {
    private List<ChatRoomResponseDto> chatRoomResponseDto;
    private int totalCnt;




    public ChatRoomListResponseDto(List<ChatRoomResponseDto> chatRoomResponseDto, int totalCnt) {
        this.chatRoomResponseDto = chatRoomResponseDto;
        this.totalCnt=totalCnt;
    }
}

