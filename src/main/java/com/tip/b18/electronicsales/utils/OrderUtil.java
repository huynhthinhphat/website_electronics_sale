package com.tip.b18.electronicsales.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderUtil {
    private OrderUtil(){}

//    public static String generateOrderCode(){
//        return "HD-" + DateUtil.generateCreationDate() + "-" + AtomicIntegerUtil.incrementAndGet();
//    }

    public static String generateOrderCode(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + AtomicIntegerUtil.incrementAndGet();
    }
}
