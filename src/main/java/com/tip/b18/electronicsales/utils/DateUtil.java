package com.tip.b18.electronicsales.utils;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private DateUtil(){}

    public static String generateCreationDate(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss"));
    }

    public static String generateCreationDateForUser(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
    }

    public static String formatDate(LocalDateTime date){
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
