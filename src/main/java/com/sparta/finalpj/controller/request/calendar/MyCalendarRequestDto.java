package com.sparta.finalpj.controller.request.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCalendarRequestDto {
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String filteredDate;
    private String title;
    private String todo;
}
