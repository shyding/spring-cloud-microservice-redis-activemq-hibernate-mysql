package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StrUtil {
    private char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private char[] numbers = "0123456789".toCharArray();


    public String generateRandomStr(int strLength) {
        return generateStr(strLength, chars);
    }

    public String generateDateRandomStr(int strLength) {
        return System.currentTimeMillis() + generateRandomStr(strLength);
    }

    public String generateRandomNumberStr(int strLength) {
        return generateStr(strLength, numbers);
    }

    public String generateStr(int strLength, char[] chars) {
        String randomStr = null;

        if (strLength > 0) {
            randomStr = "";
        }

        Random random = new Random();

        for ( int i = 0 ; i < strLength ; i++) {
            randomStr += String.valueOf(chars[random.nextInt(chars.length)]);
        }

        return randomStr;
    }
}
