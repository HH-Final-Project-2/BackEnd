package com.sparta.finalpj.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PostHeart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="memberId")
    private Member member;

    @ManyToOne
    @JoinColumn(name="postId")
    private Post post;

//    @ManyToOne
//    @JoinColumn(name="commentId")
//    private Comment comment;
}
