package com.tip.b18.electronicsales.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {
    private LocalDateTimeUtil(){}

    private static DateTimeFormatter formatter(){
        return DateTimeFormatter.ofPattern("yyyy-M-d");
    }

    public static LocalDateTime parseStartDay(String day){
        return day != null ? LocalDate.parse(day, formatter()).atStartOfDay() : null;
    }

    public static LocalDateTime parseEndDay(String day){
        return day != null ? LocalDate.parse(day, formatter()).atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
    }

    public static boolean isAfter(LocalDate time){
        return time == null || time.isAfter(LocalDate.now());
    }
}
