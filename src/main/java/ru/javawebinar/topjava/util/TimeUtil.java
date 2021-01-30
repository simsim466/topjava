package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtil {
    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }

    /*public static boolean isSameDay(LocalDateTime firstTime, LocalDateTime secondTime)  {
        return firstTime.getDayOfYear() == secondTime.getDayOfYear() &&
                firstTime.getYear() == secondTime.getYear();
    }*/
}
