package com.hzg.tools;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateUtil {
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getCurrentDayStr() {
        Date date = new Date();
        return dayFormat.format(date);
    }

    public String getCurrentDayStr(String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(new Date());
    }

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

    /**
     * 获得当前日期的指定天数的日期
     *
     * @param days
     * @return
     */
    public Date getDay(int days) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;

        try {
            date = new SimpleDateFormat("yy-M-d").parse(calendar.get(Calendar.YEAR) + "-" +
                    (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day + days);

        return calendar.getTime();
    }

    public Timestamp getSecondCurrentTimestamp(){
        long currentMillis = System.currentTimeMillis();
        return new Timestamp(currentMillis - (currentMillis % 1000));
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
}
