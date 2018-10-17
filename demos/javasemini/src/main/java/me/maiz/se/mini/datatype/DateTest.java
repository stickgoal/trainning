package me.maiz.se.mini.datatype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  练习：
 *  "2014-5-25"到"2012-12-28"
 *  中间有多少天？
 */
public class DateTest {

    public static void main(String[] args) {
        //默认创建为当前时间点的日期
        Date now = new Date();
        System.out.println(now);
        //与long的转换,long值表示的是从1970-01-01 00:00的毫秒值
        //long ->Date
        Date date1 = new Date(0L);
        System.out.println(date1);
        //Date -> long
        long time = date1.getTime();
        //基于long值的日期运算，加1天等价于 1*24*60*60*1000
        date1.setTime(time+1*24*60*60*1000);
        System.out.println(date1);

        //日期比较
        System.out.println(date1.before(now));
        System.out.println(date1.after(now));

        testFormat();
    }

    private static void testCalendar(){
        long millis = new Date().getTime();
        long oneDay = 24*60*60*1000L;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millis+oneDay));
        //加減
        cal.add(Calendar.YEAR, 2);
        cal.add(Calendar.MONTH, -2);
        //獲得
        System.out.println(cal.get(Calendar.YEAR));
        System.out.println(cal.get(Calendar.MONTH));
        System.out.println(cal.get(Calendar.DATE));
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
        System.out.println(cal.getTime());
        //設置
        cal.set(Calendar.YEAR, 1998);
        System.out.println(cal.getTime());

    }

    private static void testFormat(){
        //格式化与解析日期（String   <==>   Date）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒SSS毫秒");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date someDate = Calendar.getInstance().getTime();
        String formatted = sdf.format(someDate);
        System.out.println(formatted);

        try {
            Date parsedDate = sdf.parse("2008-05-12 14:28:12.123");
            System.out.println(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
