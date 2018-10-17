package me.maiz.se.mini.datatype;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * 参考自 http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html
 * Created by Lucas on 2017-11-13.
 */
public class NewDateTimeAPI {

    public static void main(String[] args) {
        //创建
        //1.当前时间
        LocalDateTime now = LocalDateTime.now();
        //2.of（年，月，日，时，分，秒，纳秒）
        LocalDateTime ldt1 = LocalDateTime.of(2017, Month.AUGUST, 5, 12, 12, 12, 12);
        System.out.println(ldt1);
        //3.parse(格式字符串)
        LocalDateTime ldt2 = LocalDateTime.parse("2017-12-12T12:00:00");
        System.out.println(ldt2);

        //访问
        System.out.println("年：" + now.getYear() + " 月：" + now.getMonth().getValue() + " 日：" + now.getDayOfMonth());
        System.out.println("时：" + now.getHour() + " 分：" + now.getMinute() + " 秒：" + now.getSecond());

        //操作
        //1.通用加减法
        LocalDateTime nextDay = now.plus(1, ChronoUnit.DAYS);
        LocalDateTime nextHour = now.plus(1, ChronoUnit.HOURS);
        LocalDateTime nextMinu = now.plus(1, ChronoUnit.MINUTES);
        System.out.println("nextDay : " + nextDay);
        System.out.println("nextHour : " + nextHour);
        System.out.println("nextMinu : " + nextMinu);
        LocalDateTime lastDay = now.minus(1, ChronoUnit.DAYS);
        System.out.println("lastDay : " + lastDay);

        //2.简便方法
        System.out.println("next weeks :" + now.plusWeeks(1));

        //3.区间处理，Period、Duration
        //加上3年2月1日
        Period period = Period.of(3, 2, 1);
        LocalDateTime nextPeriod = now.plus(period);
        System.out.println("nextPeriod : " + nextPeriod);
        //加上300秒
        Duration duration = Duration.ofSeconds(300);
        LocalDateTime next300s = now.plus(duration);
        System.out.println("next300s : " + next300s);

    }

}

