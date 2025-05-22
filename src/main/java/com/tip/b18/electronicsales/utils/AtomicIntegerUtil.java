package com.tip.b18.electronicsales.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerUtil {
    private static final AtomicInteger count = new AtomicInteger(0);

    private AtomicIntegerUtil(){}

    public static int incrementAndGet(){
        return count.incrementAndGet();
    }
}
