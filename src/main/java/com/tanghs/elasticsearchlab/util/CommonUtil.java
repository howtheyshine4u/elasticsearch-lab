package com.tanghs.elasticsearchlab.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/7/10 16:09
 * @version:
 */
public class CommonUtil {

    /**
     * 一天的毫秒值
     */
    private static long DAY_MILLI = 86400000L;

    /**
     * 获取当天零点
     * @return
     */
    public static long getTodayStartTime() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取昨天零点
     * @return
     */
    public static long getYesterdayStartTime() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now() , LocalTime.MIN);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - DAY_MILLI;
    }

    /**
     * 获取当天截止时间
     * @return
     */
    public static long getTodayEndTime() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取昨天截止时间
     * @return
     */
    public static long getYesterdayEndTime() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now() , LocalTime.MAX);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - DAY_MILLI;
    }
}
