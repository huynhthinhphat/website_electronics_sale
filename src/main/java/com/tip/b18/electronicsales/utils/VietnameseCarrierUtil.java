package com.tip.b18.electronicsales.utils;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.enums.VietnameseCarrier;
import com.tip.b18.electronicsales.exceptions.InvalidValueException;

import java.util.Arrays;

public class VietnameseCarrierUtil {
    public static void checkPhoneNumber(String phoneNumber) {
        if(phoneNumber.trim().contains(" ") || !containsOnlyDigits(phoneNumber)){
            throw new InvalidValueException(MessageConstant.INVALID_CHARACTER_PHONE_NUMBER);
        }
        if(phoneNumber.length() != 10){
            throw new InvalidValueException(MessageConstant.INVALID_LENGTH_PHONE_NUMBER);
        }
        if(!existPrefix(phoneNumber.substring(0, 3))){
            throw new InvalidValueException(MessageConstant.INVALID_PHONE_PREFIX);
        }
    }

    private static boolean containsOnlyDigits(String phone) {
        return phone.matches("\\d+");
    }

    private static boolean existPrefix(String prefix){
        return Arrays.stream(VietnameseCarrier.values())
                .anyMatch(carrier -> carrier.getPrefixes().contains(prefix));
    }
}
