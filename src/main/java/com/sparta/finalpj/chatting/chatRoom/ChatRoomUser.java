package com.sparta.finalpj.chatting.chatRoom;

import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.domain.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ChatRoomUser extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 채팅방 주인
    @ManyToOne
    private Member member;

    // 채팅방 이름
    private String name;

    @ManyToOne
    private Member otherMember;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;


    public ChatRoomUser(Member member, Member anotherUser, ChatRoom room) {

        this.member = member;
        this.name = anotherUser.getNickname();
        this.chatRoom = room;
        this.otherMember = anotherUser;
    }


}
