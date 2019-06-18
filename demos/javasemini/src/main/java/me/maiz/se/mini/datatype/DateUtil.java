package me.maiz.se.mini.datatype;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String LONG_PATTERN = "yyyyMMddHHmmssSSS";

    public static String format(Date date,String pattern){
        return getFormat(pattern).format(date);
    }

    public static DateFormat getFormat(String pattern){
        return new SimpleDateFormat(pattern);
    }


    public static String formatLong(Date date){
        return format(date,LONG_PATTERN);
    }


    public static String format(Date date){
        return format(date,DEFAULT_PATTERN);
    }

    public static Date parse(String dateStr,String pattern){
        try {
            return getFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parse(String dateStr){
        return parse(dateStr,DEFAULT_PATTERN);
    }
}
