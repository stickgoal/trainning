package me.maiz.se.mini.datatype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  ��ϰ��
 *  "2014-5-25"��"2012-12-28"
 *  �м��ж����죿
 */
public class DateTest {

    public static void main(String[] args) {
        //Ĭ�ϴ���Ϊ��ǰʱ��������
        Date now = new Date();
        System.out.println(now);
        //��long��ת��,longֵ��ʾ���Ǵ�1970-01-01 00:00�ĺ���ֵ
        //long ->Date
        Date date1 = new Date(0L);
        System.out.println(date1);
        //Date -> long
        long time = date1.getTime();
        //����longֵ���������㣬��1��ȼ��� 1*24*60*60*1000
        date1.setTime(time+1*24*60*60*1000);
        System.out.println(date1);

        //���ڱȽ�
        System.out.println(date1.before(now));
        System.out.println(date1.after(now));

        testFormat();
    }

    private static void testCalendar(){
        long millis = new Date().getTime();
        long oneDay = 24*60*60*1000L;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millis+oneDay));
        //�Ӝp
        cal.add(Calendar.YEAR, 2);
        cal.add(Calendar.MONTH, -2);
        //�@��
        System.out.println(cal.get(Calendar.YEAR));
        System.out.println(cal.get(Calendar.MONTH));
        System.out.println(cal.get(Calendar.DATE));
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
        System.out.println(cal.getTime());
        //�O��
        cal.set(Calendar.YEAR, 1998);
        System.out.println(cal.getTime());

    }

    private static void testFormat(){
        //��ʽ����������ڣ�String   <==>   Date��
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd�� hhʱmm��ss��SSS����");
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
