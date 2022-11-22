package com.sparta.finalpj.service;

import com.sparta.finalpj.controller.request.calendar.MyCalendarRequestDto;
import com.sparta.finalpj.controller.response.ResponseDto;
import com.sparta.finalpj.controller.response.calendar.MyCalendarResponseDto;
import com.sparta.finalpj.domain.MyCalendar;
import com.sparta.finalpj.domain.Member;
import com.sparta.finalpj.exception.CustomException;
import com.sparta.finalpj.exception.ErrorCode;
import com.sparta.finalpj.repository.MyCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyCalendarService {
    private final CommonService commonService;
    private final MyCalendarRepository myCalendarRepository;

    // 내일정 등록
    @Transactional
    public ResponseDto<?> createCalendar(MyCalendarRequestDto requestDto, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String startDateTime = requestDto.getStartDate() + " " + requestDto.getStartTime();
        String endDateTime = requestDto.getEndDate() + " " + requestDto.getEndTime();
        // 3.일정 등록
        MyCalendar myCalendar = MyCalendar.builder()
                .startDate(requestDto.getStartDate())
                .startTime(requestDto.getStartTime())
                .startDateTime(dateFormat(startDateTime))
                .endDate(requestDto.getEndDate())
                .endTime(requestDto.getEndTime())
                .endDateTime(dateFormat(endDateTime))
                .filteredDate(requestDto.getFilteredDate())
                .title(requestDto.getTitle())
                .todo(requestDto.getTodo())
                .member(member)
                .build();
        myCalendarRepository.save(myCalendar);

        return ResponseDto.success("생성 완료!");
    }

    // 내일정 수정
    @Transactional
    public ResponseDto<?> updateCalendar(Long calendarId, MyCalendarRequestDto requestDto, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        MyCalendar myCalendar = isPresentCalendar(calendarId);
        if(myCalendar == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CALNFO);
        }

        String startDateTime = requestDto.getStartDate() + " " + requestDto.getStartTime();
        String endDateTime = requestDto.getEndDate() + " " + requestDto.getEndTime();
        myCalendar.update(requestDto, dateFormat(startDateTime), dateFormat(endDateTime));
        return ResponseDto.success("수정 완료");
    }

    // 내일정 삭제
    @Transactional
    public ResponseDto<?> deleteCalendar(Long calendarId, HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        MyCalendar myCalendar = isPresentCalendar(calendarId);
        if(myCalendar == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_CALNFO);
        }

        myCalendarRepository.deleteById(calendarId);
        return ResponseDto.success("삭제 완료");
    }

    // 내일정 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCalendar(HttpServletRequest request) {
        // 1. 로그인 확인
        commonService.loginCheck(request);

        // 2. Token validation => member 생성
        Member member = commonService.validateMember(request);
        if (member == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        List<MyCalendar> myCalendarList = myCalendarRepository.findAllByOrderByStartDateTimeAscCreatedAtAsc();
        List<MyCalendarResponseDto> calendarAllList = new ArrayList<>();

        for (MyCalendar myCalendar : myCalendarList) {
            calendarAllList.add(
                    MyCalendarResponseDto.builder()
                            .id(myCalendar.getId())
                            .startDate(myCalendar.getStartDate())
                            .startTime(myCalendar.getStartTime())
                            .endDate(myCalendar.getEndDate())
                            .endTime(myCalendar.getEndTime())
                            .filteredDate(myCalendar.getFilteredDate())
                            .title(myCalendar.getTitle())
                            .todo(myCalendar.getTodo())
                            .createdAt(myCalendar.getCreatedAt())
                            .modifiedAt(myCalendar.getModifiedAt())
                            .build()
            );
        }
        return ResponseDto.success(calendarAllList);
    }

    @Transactional(readOnly = true)
    public MyCalendar isPresentCalendar(Long calendarId) {
        Optional<MyCalendar> optionalCard = myCalendarRepository.findById(calendarId);
        return optionalCard.orElse(null);
    }

    // String -> Date 형변환
    public Date dateFormat(String dateTimeStr) {
        // String -> Date 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateTime = null;
        try {
            dateTime = format.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dateTime;
    }
}
