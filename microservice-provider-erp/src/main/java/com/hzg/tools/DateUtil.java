package com.hzg.tools;

import com.hzg.erp.SysClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class DateUtil {
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final long expire_second_7_days = 3600 * 24 * 7;

    @Autowired
    private SysClient sysClient;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

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
     * 获得当前日期的指定天数后的日期
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
        long sysCurrentTimeMillis;

        BoundValueOperations<String, Object> operation = redisTemplate.boundValueOps("sysCurrentTimeMillis");

        Long currentTimeMillisStr = (Long)operation.get();
        if (currentTimeMillisStr != null) {
            sysCurrentTimeMillis = currentTimeMillisStr + (expire_second_7_days - operation.getExpire()) * 1000L;

        } else {
            sysCurrentTimeMillis = sysClient.computeSysCurrentTimeMillis();
            operation.set(sysCurrentTimeMillis);
            operation.expire(expire_second_7_days, TimeUnit.SECONDS);
        }

        return new Timestamp(sysCurrentTimeMillis - (sysCurrentTimeMillis % 1000));
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
}
