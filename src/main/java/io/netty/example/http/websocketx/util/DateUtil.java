package io.netty.example.http.websocketx.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.0";
    public static final String DATE_PATTERN = "yyyyMMdd";

    /**
     * 字符串转日期输出
     *
     * @param time
     * @param pattern
     * @return
     */
    public static LocalDateTime stringToDate(String time, String pattern) {

        return LocalDateTime.parse(time,DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期转字符串输出
     * @param date
     * @param pattern
     * @return
     */
    public static String dateToString(Date date, String pattern){

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
