package com.sparta.finalpj.controller;

import com.sparta.finalpj.controller.request.calendar.MyCalendarRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.service.MyCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class MyCalendarController {
    private final MyCalendarService myCalendarService;

    // 내일정 등록
    @PostMapping(value = "/calendar")
    public ResponseDto<?> createCalendar(@RequestBody MyCalendarRequestDto requestDto, HttpServletRequest request) {
        return myCalendarService.createCalendar(requestDto, request);
    }

    // 내일정 수정
    @PutMapping(value = "/calendar/{calendarId}")
    public ResponseDto<?> updateCalendar(@PathVariable Long calendarId, @RequestBody MyCalendarRequestDto requestDto, HttpServletRequest request) {
        return myCalendarService.updateCalendar(calendarId, requestDto, request);
    }

    // 내일정 삭제
    @DeleteMapping(value = "/calendar/{calendarId}")
    public ResponseDto<?> deleteCalendar(@PathVariable Long calendarId, HttpServletRequest request) {
        return myCalendarService.deleteCalendar(calendarId, request);
    }

    // 내일정 전체조회
    @GetMapping(value = "/calendar")
    public ResponseDto<?> getAllCalendar(HttpServletRequest request) {
        return myCalendarService.getAllCalendar(request);
    }

    // 내일정 상세조회
    @GetMapping(value = "/calendar/{calendarId}")
    public ResponseDto<?> getCalendarDetail(@PathVariable Long calendarId, HttpServletRequest request) {
        return myCalendarService.getCalendarDetail(calendarId, request);
    }
}
