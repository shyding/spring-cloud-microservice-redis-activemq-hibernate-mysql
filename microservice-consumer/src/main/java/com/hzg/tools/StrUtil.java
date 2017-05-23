package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StrUtil {
    public String generateRandomStr(int strLength) {
        String randomStr = null;

        if (strLength > 0) {
            randomStr = "";
        }

        Random random = new Random();
        char [] str="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for ( int i = 0 ; i < strLength ; i++) {
            randomStr += String.valueOf(str[random.nextInt(62)]);
        }

        return randomStr;
    }
}
