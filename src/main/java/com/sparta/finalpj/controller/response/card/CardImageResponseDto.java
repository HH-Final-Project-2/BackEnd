package com.sparta.finalpj.controller.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardImageResponseDto {
    private Long id;
    private String cardImg;
}
