package com.dysjsjy.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 获取当前时间的默认格式字符串
     * @return 格式化的当前时间字符串
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }
    
    /**
     * 获取当前时间的自定义格式字符串
     * @param pattern 日期时间格式模式
     * @return 格式化的当前时间字符串
     */
    public static String getCurrentDateTime(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 将LocalDateTime格式化为字符串
     * @param dateTime 日期时间对象
     * @return 格式化的日期时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    /**
     * 将LocalDateTime按指定格式格式化为字符串
     * @param dateTime 日期时间对象
     * @param pattern 日期时间格式模式
     * @return 格式化的日期时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将字符串解析为LocalDateTime
     * @param dateTimeStr 日期时间字符串
     * @return 解析后的LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }
}