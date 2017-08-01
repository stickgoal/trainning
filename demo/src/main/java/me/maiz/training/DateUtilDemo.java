package me.maiz.training;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lucas on 2017-08-01.
 */
public class DateUtilDemo {

    public static void main(String[] args) {
        try {
            //尝试用多种方式解析日期
            println(DateUtils.parseDate("2017-07-29 12:17:21","yyyy","yy/dd/mm","yyyy-MM-dd HH:mm:ss"));
            //加法
            println(DateUtils.addDays(new Date(),3));
            println(DateUtils.addHours(new Date(),3));
            //按某个字段舍入，其他设为初始值
            println(DateUtils.round(new Date(), Calendar.YEAR));
            //清空指定字段更小尺度的日期
            println(DateUtils.truncate(new Date(),Calendar.YEAR));
            //是否为同一天
            println(DateUtils.isSameDay(new Date(),new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void println(Object data){
        System.out.println(data);
    }


}
