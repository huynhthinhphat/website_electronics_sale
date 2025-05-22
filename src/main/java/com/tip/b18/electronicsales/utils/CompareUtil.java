package com.tip.b18.electronicsales.utils;

public class CompareUtil {
    public static boolean compare(Object objectFirst, Object objectSecond){
        return objectFirst == null || objectFirst.equals(objectSecond);
    }

    public static boolean isFirstNumberSmaller(int numberFirst, int numberSecond){
        return numberFirst < numberSecond;
    }
}
