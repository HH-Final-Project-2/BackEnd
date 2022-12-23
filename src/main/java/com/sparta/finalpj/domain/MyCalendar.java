package com.sparta.finalpj.domain;

import com.sparta.finalpj.controller.request.calendar.MyCalendarRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MyCalendar extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String startTime;

    @Column
    private Date startDateTime;

    @Column(nullable = false)
    private String endDate;

    @Column(nullable = false)
    private String endTime;

    @Column
    private Date endDateTime;

    @Column(nullable = false)
    private String filteredDate;

    @Column(nullable = false)
    private String title;

    @Column
    private String todo;

    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(MyCalendarRequestDto myCalendarRequestDto, Date startDateTime, Date endDateTime) {
        this.startDate = myCalendarRequestDto.getStartDate();
        this.startTime = myCalendarRequestDto.getStartTime();
        this.startDateTime = startDateTime;
        this.endDate = myCalendarRequestDto.getEndDate();
        this.endTime = myCalendarRequestDto.getEndTime();
        this.endDateTime = endDateTime;
        this.filteredDate = myCalendarRequestDto.getFilteredDate();
        this.title = myCalendarRequestDto.getTitle();
        this.todo = myCalendarRequestDto.getTodo();
    }

}
