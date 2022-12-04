package com.sparta.finalpj.controller.response.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyCalendarResponseDto {
    private Long id;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String title;
    private String todo;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
