package me.maiz.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final String[] DATE_PATTERNS = {"yyyy/MM","yyyy-MM-dd","yyyy年MM月"};


    public static Date parse(String dateStr){
        Date date = null;
        for (String p:DATE_PATTERNS){
            SimpleDateFormat sdf = new SimpleDateFormat(p);
            try {
                date = sdf.parse(dateStr);
                System.out.println(dateStr+" 正确的格式 "+p);
                return date;
            } catch (ParseException e) {
                System.out.println(dateStr+"不符合格式"+p);
            }

        }
        return null;
    }

}
