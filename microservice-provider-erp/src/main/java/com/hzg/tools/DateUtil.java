package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright © 2012-2025 云南红掌柜珠宝有限公司 版权所有
 * 文件名: DateUtil.java
 * 类的详细说明
 *
 * @author smjie
 * @version 1.00
 * @Date 2017/4/26
 */
@Component
public class DateUtil {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getCurrentDateStr() {
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public String getDaysDate(String dateStr, String dateFormat, int num) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);

        Date date = null;
        try {
             date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return df.format(new Date(date.getTime() + (long)num * 24 * 60 * 60 * 1000));
    }
}
