package com.core.aop.bean;

import java.util.Calendar;
import java.util.Date;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimes
{
  public static final String FORMAT_yyyyMMdd = "yyyyMMdd";
  public static final String FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
  public static final String FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
  public static final String FORMAT_CHAINESE = "yyyy年MM月dd日 HH时mm分ss秒";
  
  public static Date toDateTime(String dateText, String format)
  {
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
    LocalDateTime localDateTime = LocalDateTime.parse(dateText, dateTimeFormatter);
    return localDateTime.toDate();
  }
  
  public static String toString(Date date)
  {
    return toString(date, "yyyy-MM-dd HH:mm:ss");
  }
  
  public static String toString(Date date, String format)
  {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
    return formatter.print(date.getTime());
  }
  
  public static Date toDate(String dateText)
  {
    return toDate(dateText, "yyyy-MM-dd HH:mm:ss");
  }
  
  public static Date toDate(String dateText, String format)
  {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
    LocalDate localDate = LocalDate.parse(dateText, formatter);
    return localDate.toDate();
  }
  
  public static String toDate(Date date, String format)
  {
    return new LocalDate(date).toString(format);
  }
  
  public static Date yesterday()
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(5, -1);
    return cal.getTime();
  }
  
  public static Date shiftDay(int day)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(5, day);
    return cal.getTime();
  }
  
  public static Date shiftMonth(int month)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(2, month);
    return cal.getTime();
  }
}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.DateTimes
 * JD-Core Version:    0.7.0.1
 */