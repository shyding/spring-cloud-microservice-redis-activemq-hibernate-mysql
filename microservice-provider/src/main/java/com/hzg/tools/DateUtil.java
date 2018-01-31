package com.hzg.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DateUtil {
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final long expire_second_7_days = 3600 * 24 * 7;
    private static final long validTimeError = 30 * 1000;
    private static final double validProportion = 0.6;
    private static final String currentTimeMillis_path = "/currentTimeMillis";

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private HttpUtil httpUtil;

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
            sysCurrentTimeMillis = computeSysCurrentTimeMillis();
            operation.set(sysCurrentTimeMillis);
            operation.expire(expire_second_7_days, TimeUnit.SECONDS);
        }

        return new Timestamp(sysCurrentTimeMillis - (sysCurrentTimeMillis % 1000));
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }


    /**
     * 计算业务系统的当前时间
     *
     * 计算过程如下：
     * 取服务器的当前时间与其他各台服务器的当前时间分别相减，这些差值再与约定的可允许时间误差 15 秒比较，
     * < 15 秒 则有效时间计数值 + 1，最后如果 该有效时间计数值/服务器数量 >= 0.6，表示该服务器时间为有效当前时间。
     * 获取这些服务器的有效当前时间，然后除以服务器数量，得到的平均值作为业务系统使用的当前时间
     * @return
     */
    public long computeSysCurrentTimeMillis() {
        long delay = 0, onceDelay = 0, startTime = 0;
        List<String> services = discoveryClient.getServices();
        long[] serversCurrentTimeMillis = new long[services.size()];
        int validServersCount = 0;

        for (int i = 0; i < services.size(); i++) {
            EurekaDiscoveryClient.EurekaServiceInstance serviceInstance =
                    (EurekaDiscoveryClient.EurekaServiceInstance)discoveryClient.getInstances(services.get(i)).get(0);

            startTime = System.currentTimeMillis();
            String currentTimeMillisStr = httpUtil.get("http://" + serviceInstance.getInstanceInfo().getIPAddr() +
                    ":" + serviceInstance.getPort() + currentTimeMillis_path);
            onceDelay = System.currentTimeMillis() - startTime;

            if (currentTimeMillisStr != null) {
                serversCurrentTimeMillis[i] = Long.valueOf(currentTimeMillisStr);
                serversCurrentTimeMillis[i] = serversCurrentTimeMillis[i] - delay - onceDelay/2L;
                validServersCount++;
            } else {
                serversCurrentTimeMillis[i] = -1;
            }

            delay += onceDelay;
        }

        int k = 0;
        long[] validServersCurrentTimeMillis = new long[validServersCount];
        for (int i = 0; i < serversCurrentTimeMillis.length; i++) {
            if (serversCurrentTimeMillis[i] != -1) {
                validServersCurrentTimeMillis[k++] = serversCurrentTimeMillis[i];
            }
        }

        int[] validTimeCount = new int[validServersCurrentTimeMillis.length];
        for (int i = 0; i < validTimeCount.length; i++) {
            validTimeCount[i] = 0;
        }

        for (int i = 0; i+1 < validServersCurrentTimeMillis.length; i++) {
            for (int j = i+1; j < validServersCurrentTimeMillis.length; j++) {
               if (Math.abs(validServersCurrentTimeMillis[i] - validServersCurrentTimeMillis[j]) <= validTimeError) {
                   validTimeCount[i]++;
                   validTimeCount[j]++;
               }
            }
        }

        long validServersCurrentTimeMillisSum = 0;
        long addServersCount = 0;
        for (int i = 0; i < validTimeCount.length; i++) {
            if (((double)validTimeCount[i]) / ((double)(validTimeCount.length-1)) >= validProportion) {
                validServersCurrentTimeMillisSum += validServersCurrentTimeMillis[i];
                addServersCount++;
            }
        }

        return validServersCurrentTimeMillisSum == 0 ? System.currentTimeMillis() : validServersCurrentTimeMillisSum/addServersCount;
    }
}
