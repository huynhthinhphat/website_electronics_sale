package com.tip.b18.electronicsales.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {
    private BigDecimalUtil(){}

    public static boolean isNullOrZero(BigDecimal value){
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static BigDecimal calculateDiscountPrice(BigDecimal price, BigDecimal discount) {
        if(isNullOrZero(price) && isNullOrZero(discount)){
           return null;
        }
        BigDecimal rate = discount.divide(new BigDecimal(100), 2, RoundingMode.DOWN);
        BigDecimal discountPrice = price.multiply(rate);
        price = price.subtract(discountPrice);
        return price;
    }
}
