package com.sparta.finalpj.chatting.chatRoom.responseDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ChatRoomResponseDto implements Comparable<ChatRoomResponseDto> {
    private String roomName;
    private String chatRoomUuid;
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    private String dayBefore;
    private int unreadCount;

    private int totalCnt;

    public ChatRoomResponseDto(String roomName, String roomId, String lastMessage,
        LocalDateTime lastTime, int unReadMessageCount, String dayBefore,int totalCnt) {
        this.roomName = roomName;
        this.chatRoomUuid=roomId;
        this.lastMessage=lastMessage;
        this.createAt=lastTime;
        this.dayBefore = dayBefore;
        this.unreadCount = unReadMessageCount;
        this.totalCnt = totalCnt;
    }

    @Override
    public int compareTo(ChatRoomResponseDto responseDto) {
        if (responseDto.createAt.isBefore(createAt)) {
            return 1;
        } else if (responseDto.createAt.isAfter(createAt)) {
            return -1;
        }
        return 0;
    }
}
