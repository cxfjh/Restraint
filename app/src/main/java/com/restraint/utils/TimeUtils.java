package com.restraint.utils;

import android.widget.TimePicker;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;


/* 时间差计算工具类 */
public class TimeUtils {
    // 中国时区ID
    private static final String CHINA_TIMEZONE_ID = "Asia/Shanghai";


    /**
     * 初始化TimePicker的时间为当前中国时区的时间
     * @param timePicker TimePicker对象
     */
    public static void initTime(final TimePicker timePicker) {
        // 使用中国时区获取当前时间
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(CHINA_TIMEZONE_ID));
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        // 设置TimePicker初始时间
        timePicker.setHour(hour);
        timePicker.setMinute(minute);
    }


    /**
     * 计算目标时间与当前中国时区的时间之间的时间差（毫秒）
     * @param targetHour 目标时间小时
     * @param targetMinute 目标时间分钟
     * @return 时间差（毫秒）
     */
    public static long TimeDifference(final int targetHour, final int targetMinute) {
        // 获取中国时区的当前时间
        final ZonedDateTime chinaDateTime = ZonedDateTime.now(ZoneId.of(CHINA_TIMEZONE_ID));
        final LocalTime currentTime = chinaDateTime.toLocalTime();

        // 创建目标时间
        final LocalTime targetTime = LocalTime.of(targetHour, targetMinute);

        // 计算时间差（毫秒）
        final long currentTimeMillis = currentTime.toNanoOfDay() / 1_000_000;
        long targetTimeMillis = targetTime.toNanoOfDay() / 1_000_000;

        // 如果目标时间早于当前时间，加上一天的毫秒数
        if (targetTimeMillis < currentTimeMillis) targetTimeMillis += 24 * 60 * 60 * 1000;

        // 计算差值
        return targetTimeMillis - currentTimeMillis;
    }
}