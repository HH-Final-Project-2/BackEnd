package com.sparta.finalpj.chatting.chatRoom;

import com.sparta.finalpj.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser,Long> {
   // List<ChatRoomUser> findAllByUser(User user);
    //Page<ChatRoomUser> findAllByMember(Member member, Pageable pageable);
   List<ChatRoomUser> findAllByMember(Member member);
    List<ChatRoomUser> findAllByMemberNotAndChatRoom(Member member, ChatRoom chatRoom);
    void deleteByChatRoomAndMember(ChatRoom chatRoom, Member member);

    // ChatRoomUser findByUser(User user);
}
