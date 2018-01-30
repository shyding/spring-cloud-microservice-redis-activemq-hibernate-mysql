
package com.sf.aries.core.common.util;

import com.sf.aries.core.exception.AriesRuntimeException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

public class DateUtils {
    private static final Logger logger = Logger.getLogger(DateUtils.class);
    public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDDHHMMSS_OBLIQUE = "yyyy/MM/dd HH:mm:ss";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String YYYYMMDD = "yyyyMMdd";

    public DateUtils() {
    }

    public static String format(Date date) {
        return format(date, "yyyyMMddHHmmss");
    }

    public static String format(Date date, String partten) {
        SimpleDateFormat sf = new SimpleDateFormat(partten);
        return sf.format(date);
    }

    public static boolean isExpiry(Date date, long expiryTime) {
        Date currentTime = new Date();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentTime);
        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.setTime(date);
        expiryCalendar.add(13, (int)expiryTime);
        if(currentCalendar.compareTo(expiryCalendar) > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("当前值currentTime=");
            sb.append(format(currentTime));
            sb.append("   逾期值expiryTime= ");
            sb.append(format(expiryCalendar.getTime()));
            logger.warn(sb.toString());
            return true;
        } else {
            return false;
        }
    }

    public static int dateCompare(Date startDate, Date endDate) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(startDate);
        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.setTime(endDate);
        return currentCalendar.compareTo(expiryCalendar);
    }

    public static Date inCurrentindeTime(Date date, int inSideTime) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(date);
        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.setTime(date);
        expiryCalendar.add(13, -inSideTime);
        return expiryCalendar.getTime();
    }

    public static final Date getCurrentTime(String time, String pattern) {
        String formatTime = format(new Date(), "yyyy-MM-dd HH:mm:ss");
        String timeStr = formatTime.substring(0, 11) + time;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return sd.parse(timeStr);
        } catch (ParseException var6) {
            logger.error(var6.getMessage());
            throw new AriesRuntimeException("处理日期失败" + var6.getMessage(), var6);
        }
    }

    public static final boolean afterCurrentTime(String time, String pattern) {
        Date currentDate = new Date();
        Date date = getCurrentTime(time, pattern);
        return date.before(currentDate);
    }

    public static final Date parse(String time, String pattern) {
        SimpleDateFormat sd = new SimpleDateFormat(pattern);

        try {
            return sd.parse(time);
        } catch (ParseException var4) {
            logger.error(var4.getMessage());
            throw new AriesRuntimeException("处理日期失败" + var4.getMessage(), var4);
        }
    }

    public static final Date parse(String time, String pattern, String pattern2) {
        try {
            return parse(time, pattern);
        } catch (Exception var4) {
            return parse(time, pattern2);
        }
    }

    public static final boolean beforeCurrentTime(String time, String pattern) {
        Date currentDate = new Date();
        Date date = getCurrentTime(time, pattern);
        return date.after(currentDate);
    }
}
