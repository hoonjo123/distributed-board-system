package joney.board.hotarticle.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculatorUtils {
    public static Duration calculateDurationToMidnight() {
        // 자정까지 얼마나 남았는지 시간 반환
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        return Duration.between(now, midnight);
    }
}
