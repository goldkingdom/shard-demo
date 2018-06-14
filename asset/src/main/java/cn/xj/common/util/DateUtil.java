package cn.xj.common.util;

import org.assertj.core.util.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date parse(String dateStr, String pattern) throws ParseException {
        if (Strings.isNullOrEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateStr);
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date date2Date(Date date, String pattern) throws ParseException {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(sdf.format(date));
    }

    public static String string2String(String dateStr, String pattern) throws ParseException {
        if (Strings.isNullOrEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(sdf.parse(dateStr));
    }

    public static Calendar calendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null) {
            return null;
        } else {
            calendar.setTime(date);
        }
        return calendar;
    }

    public static Calendar calendar(String dateStr, String pattern) throws ParseException {
        if (Strings.isNullOrEmpty(dateStr)) {
            return null;
        }
        Date date = parse(dateStr, pattern);
        Calendar calendar = calendar(date);
        return calendar;
    }

    public static Date add(Date date, int num, int type) {
        if (date == null) {
            return null;
        }
        Calendar calendar = calendar(date);
        calendar.add(num, type);
        return calendar.getTime();
    }

    public static Date add(String dateStr, String pattern, int num, int type) throws ParseException {
        if (Strings.isNullOrEmpty(dateStr)) {
            return null;
        }
        Calendar calendar = calendar(dateStr, pattern);
        if (calendar == null) {
            return null;
        }
        calendar.add(num, type);
        return calendar.getTime();
    }

    public static String add(Date date, String pattern, int num, int type) {
        if (date == null) {
            return null;
        }
        Calendar calendar = calendar(date);
        if (calendar == null) {
            return null;
        }
        calendar.add(num, type);
        return format(calendar.getTime(), pattern);
    }

    public static Long between(Date start, Date end) {
        Calendar calendar1 = calendar(start);
        Long time1 = calendar1.getTimeInMillis();
        Calendar calendar2 = calendar(end);
        Long time2 = calendar2.getTimeInMillis();
        return (time2 - time1) / (1000 * 60 * 60 * 24);
    }

    public static Long between(String startStr, String endStr, String pattern) throws ParseException {
        Date start = parse(startStr, pattern);
        Date end = parse(endStr, pattern);
        return between(start, end);
    }

    public static Long timestamp(Date date) {
        return date.getTime();
    }

    public static Long timestamp(String dateStr, String pattern) throws ParseException {
        Date date = parse(dateStr, pattern);
        return date.getTime();
    }

    public static Date timestamp2Date(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }

    public static String timestamp2String(Long timestamp, String pattern) {
        if (timestamp == null) {
            return null;
        }
        Date date = new Date(timestamp);
        return format(date, pattern);
    }

}
