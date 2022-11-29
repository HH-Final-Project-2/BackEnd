package com.sparta.finalpj.chatting;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
public class Time {
    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC){
            return diffTime + "초전";
        }
        diffTime = diffTime / TIME_MAXIMUM.SEC;
        if (diffTime < TIME_MAXIMUM.MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / TIME_MAXIMUM.MIN;
        if (diffTime < TIME_MAXIMUM.HOUR) {
            return diffTime + "시간 전";
        }
        diffTime = diffTime / TIME_MAXIMUM.HOUR;
        if (diffTime < TIME_MAXIMUM.DAY) {
            return diffTime + "일 전";
        }
        diffTime = diffTime / TIME_MAXIMUM.DAY;
        if (diffTime < TIME_MAXIMUM.MONTH) {
            return diffTime + "개월 전";
        }
        diffTime = diffTime / TIME_MAXIMUM.MONTH;
        return diffTime + "년 전";
    }

    public String times(long time) {
        // 게시글이 현재로 부터 몇 분전에 작성되었는지 보기 좋게 변경
        if (time < 60) {
            return String.format("%s분 전", time);
        }
        time = time / 60;  // 시간
        if (time < 24) {
            return String.format("%s시간 전", time);
        }
        time = time / 24;
        if (time < 7) {
            return String.format("%s일 전", time);
        }

        time = time / 30;
        if (time < 12) {
            return String.format("%s개월 전", time);
        }

        time = time / 12;
        return String.format("%s년 전", time);
    }
}
