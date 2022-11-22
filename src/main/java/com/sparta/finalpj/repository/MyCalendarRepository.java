package com.sparta.finalpj.repository;

import com.sparta.finalpj.domain.MyCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyCalendarRepository extends JpaRepository<MyCalendar, Long> {

    List<MyCalendar> findAllByOrderByStartDateTimeAscCreatedAtAsc();
}
