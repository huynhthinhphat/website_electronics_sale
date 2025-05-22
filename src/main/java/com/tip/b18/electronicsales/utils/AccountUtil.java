package com.tip.b18.electronicsales.utils;

public class AccountUtil {
    private AccountUtil(){}

    public static String generateUserName(){
        return "user" + DateUtil.generateCreationDateForUser() + AtomicIntegerUtil.incrementAndGet();
    }
}
