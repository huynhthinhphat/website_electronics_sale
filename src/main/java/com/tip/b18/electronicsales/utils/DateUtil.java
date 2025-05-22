package com.tip.b18.electronicsales.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String generateCreationDate(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss"));
    }

    public static String generateCreationDateForUser(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
    }
}
