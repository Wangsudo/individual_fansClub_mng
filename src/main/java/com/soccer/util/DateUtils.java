package com.soccer.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final long DEFAULT_DATE = -5364691200000L;

    public static final String DATE_YYYYMM_PATTERN = "yyyyMM";
    public static final String DATE_YEAR_MONTH_PATTERN = "yyyy-MM";
    public static final String DATE_YEAR_PATTERN = "yyyy";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_SHORTTIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN_2 = "yyyy/MM/dd";
    public static final String DATE_POINT_PATTERN = "yyyy.MM.dd";
    public static final String DATE_POINT2_PATTERN = "yyyy.MM";
    public static final String DATE_YYYYMMDDHHmm="yyyy年MM月dd日 HH:mm";

    public static final long TIME_OF_A_DAY = 24 * 60 * 60 * 1000;

    public static final long TIME_OF_A_HOUR = 60 * 60 * 1000;

    /** 获取当前时间戳 */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /** 将字符串转换成日期信息（格式：yyyy/mm/dd） */
    public static Date string2Date(String dateString, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(dateString);
        return date;
    }

    /** 将给定时间戳转换成指定格式字符串 */
    public static String timestamp2String(String format, Date time) {
        if (format == null) {
            format = DATE_PATTERN;
        }
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(format);

        if (time == null) {
            return null;
        }
        return formatter.format(time);
    }

    /** 将字符串转换成日期信息（格式：yyyy-mm） */
    public static Date string2Month(String dateString) throws ParseException {
        Date date = new SimpleDateFormat(DATE_YEAR_MONTH_PATTERN).parse(dateString);
        return date;
    }
    
    /** 将字符串转换成日期信息（格式：yyyy-mm-dd） */
    public static Date string2Date(String dateString) throws ParseException {
        Date date = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA).parse(dateString);
        return date;
    }

    /** 将字符串转换成日期信息（格式：yyyy-mm-dd hh:MM:ss） */
    public static Date string2DateTime(String dateString) throws ParseException {
        Date date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA)
                .parse(dateString);
        return date;
    }

    /** 获取当前时间戳并转换成默认格式（精确到秒）的字符串 */
    public static String getCurrentTime() {
        return getTimeFromTimestamp(getCurrentTimestamp());
    }

    /** 将指定时间戳转换成默认格式（精确到秒）的字符串 */
    public static String getTimeFromTimestamp(Date timestamp) {
        return timestamp2String(DATE_TIME_PATTERN, timestamp);
    }

    /** 获取当前时间戳并转换成默认格式（精确到天）的字符串 */
    public static String getCurrentDate() {
        return getDateFromTimestamp(getCurrentTimestamp());
    }

    /** 将指定时间戳转换成默认格式（精确到天）的字符串 */
    public static String getDateFromTimestamp(Date timestamp) {
        return timestamp2String(DATE_PATTERN, timestamp);
    }
    
    /** 将指定时间戳转换成默认格式（精确到月）的字符串 */
    public static String getDateFromMonthstamp(Date timestamp) {
        return timestamp2String(DATE_YEAR_MONTH_PATTERN, timestamp);
    }

    /** 将指定时间戳转换成默认格式（精确到天，时分）的字符串 */
    public static String getDateFromShortTimestamp(Date timestamp) {
        return timestamp2String(DATE_SHORTTIME_PATTERN, timestamp);
    }

    /** 将指定时间戳的毫秒数转换成默认格式（精确到天）的字符串 */
    public static String getDateFromMillisecond(Long s) {
        return timestamp2String(DATE_PATTERN, new Date(s));
    }

    /** 将指定时间戳转换成默认格式（精确到秒）的字符串 */
    public static String getDateTimeFromTimestamp(Date timestamp) {
        return timestamp2String(DATE_TIME_PATTERN, timestamp);
    }

    /** 将指定时间戳的毫秒数转换成默认格式（精确到秒）的字符串 */
    public static String getDateTimeFromMillisecond(Long s) {
        return timestamp2String(DATE_TIME_PATTERN, new Date(s));
    }
    
    /** 判断传入的时间是不是当天 */
    public static boolean validateDayIsToday(Date timestamp) {
        return timestamp2String(DATE_PATTERN, timestamp).equals(timestamp2String(DATE_PATTERN, new Date()));
    }
    
    /** 判断给定第一个字符串形式时间是否在第二个时间之后 */
    public static boolean afterAll(String time1, String time2) throws ParseException {
        
        Locale locale;
        locale = Locale.ENGLISH;
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.after(date2);
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.after(date2);
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.after(date2);
        } catch (ParseException e2) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", locale);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.after(date2);
        } catch (ParseException e3) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", locale);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.after(date2);
        } catch (ParseException e3) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            return date1.after(date2);
        } catch (Exception e4) {

        }
        throw new IllegalArgumentException("参数非法:" + time1);
    }
    
    /** 判断给定第一个字符串形式时间是否在第二个时间之后 */
    public static boolean after(String time1, String time2) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date date1 = dateFormat.parse(time1);
        Date date2 = dateFormat.parse(time2);
        return date1.after(date2);
    }
    
    /** 判断给定第一个时间是否在第二个时间之后 */
    public static boolean after(Date date1, Date date2) throws ParseException {
        return date1.after(date2);
    }

    /** 判断给定第一个字符串形式时间是否在第二个时间之前 */
    public static boolean before(String time1, String time2) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date date1 = dateFormat.parse(time1);
        Date date2 = dateFormat.parse(time2);
        return date1.before(date2);
    }
    
    /** 判断给定第一个字符串形式时间是否在第二个时间之前 */
    public static boolean before(Date date1, Date date2) throws ParseException {
        return date1.before(date2);
    }

    /** 判断给定第一个字符串形式时间是否在第二个时间之前 (精确到秒) */
    public static boolean beforeTime(String time1, String time2) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA);
        Date date1 = dateFormat.parse(time1);
        Date date2 = dateFormat.parse(time2);
        return date1.before(date2);
    }

    /**
     * 判断一个日期是否在另两个日期之间，日期为字符串形式
     * 
     * @param midDate
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static boolean between(String midDate, String startDate, String endDate) throws ParseException {
        boolean rt = false;
        if (!(before(midDate, startDate)) && !after(midDate, endDate)) {
            rt = true;
        }
        return rt;
    }
    
    
    /**
     * 判断一个日期是否在另两个日期之间，日期为字符串形式
     * 
     * @param midDate
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static boolean between(Date midDate, Date startDate, Date endDate) throws ParseException {
        boolean rt = false;
        if (!(before(midDate, startDate)) && !after(midDate, endDate)) {
            rt = true;
        }
        return rt;
    }

    /** 判断给定第一个字符串形式时间是否与第二个时间相等 */
    public static boolean equals(String time1, String time2) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date date1 = dateFormat.parse(time1);
        Date date2 = dateFormat.parse(time2);
        return date1.equals(date2);
    }

    /**
     * @author kongchun
     * @param time1
     * @param time2
     * @return 返回 time1 - time2 的天数差
     * @throws ParseException
     */
    public static int compareDate(String time1, String time2) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date date1 = dateFormat.parse(time1);
        Date date2 = dateFormat.parse(time2);
        long quot;
        quot = date1.getTime() - date2.getTime();
        quot = quot / 1000 / 60 / 60 / 24;
        int day = new Long(quot).intValue();
        return day;
    }

    /** 将给定字符串形式时间添加指定天数后返回 */
    public static String addDaysToDate(String date, int addDays) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);
        long t1 = oldDate.getTime();
        return getDateFromTimestamp(new Timestamp(t1 + addDays * TIME_OF_A_DAY));
    }
    /** 将给定字符串形式时间添加指定天数后返回时分秒 */
    public static String addDaysToDateSecond(String date, int addDays) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);
        long t1 = oldDate.getTime();
        return getDateTimeFromTimestamp(new Timestamp(t1 + addDays * TIME_OF_A_DAY));
    }

    /** 将给定日期形式时间添加指定天数后返回 */
    public static Date addDaysToDate(Date date, int addDays){
        long t1 = date.getTime();
        return new Timestamp(t1 + addDays * TIME_OF_A_DAY);
    }
    

    /** 将给定字符串形式时间添加指定小时后返回 */
    public static String addHoursToHour(Date date, int addHours) throws ParseException {
        long t1 = date.getTime();
        return getDateTimeFromTimestamp(new Timestamp(t1 + addHours * TIME_OF_A_HOUR));
    }

    /** 将给定字符串形式时间添加指定天数后返回 （精确到秒） */
    public static String addDaysToDateTime(String date, int addDays) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);
        long t1 = oldDate.getTime();
        return getDateTimeFromTimestamp(new Timestamp(t1 + addDays * TIME_OF_A_DAY));
    }

    /** 将给定字符串形式时间添加指定月数后返回 */
    public static String addMonthsToDate(String date, int addMonths) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);

        Calendar c = Calendar.getInstance();
        c.setTime(oldDate);
        c.add(Calendar.MONTH, addMonths);
        return fullfillDateTime(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA).format(c.getTime()));
    }

    /** 将给定字符串形式时间添加指定月数后返回(精确到秒) */
    public static String addMonthsToDateTime(String date, int addMonths) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);

        Calendar c = Calendar.getInstance();
        c.setTime(oldDate);
        c.add(Calendar.MONTH, addMonths);
        return fullDateTime(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA).format(
                c.getTime()));
    }

    /** 将给定字符串形式时间在给定日历中添加指定工期（工作日）后返回 */
    public static String addDaysToDateAccordingGivenCalendar(String date, int addDays, Collection<String> holidays)
            throws ParseException {
        String result = date;

        if (addDays > 0) {
            for (int i = 0; i < addDays; i++) {
                result = getNextWorkday(result, holidays);
            }
        } else if (addDays < 0) {
            for (int i = 0; i > addDays; i--) {
                result = getLastWorkday(result, holidays);
            }
        }

        return result;
    }

    /** 获取下一个工作日 */
    public static String getNextWorkday(String today, Collection<String> holidays) throws ParseException {
        String next = today;
        for (;;) {
            next = addDaysToDate(next, 1);
            if (!isHoliday(next, holidays)) {
                return next;
            }
        }
    }

    /** 获取上一个工作日 */
    public static String getLastWorkday(String today, Collection<String> holidays) throws ParseException {
        String last = today;
        for (;;) {
            last = addDaysToDate(last, -1);
            if (!isHoliday(last, holidays)) {
                return last;
            }
        }
    }

    /** 根据开始时间、工期和日历计算结算结束时间 */
    public static String getEndDate(String startDate, int workDates, Collection<String> holidays) throws ParseException {
        return addDaysToDateAccordingGivenCalendar(startDate, workDates - 1, holidays);
    }

    /** 根据日历计算两个日期间的工作日差距 */
    public static int getDelay(String oldDate, String newDate, Collection<String> holidays) throws ParseException {
        int workDates = getWorkdates(oldDate, newDate, holidays);
        return (workDates > 0) ? workDates - 1 : ((workDates < 0) ? workDates + 1 : 0);
    }

    /** 判断给定日期是否节假日 */
    public static boolean isHoliday(String date, Collection<String> holidays) {
        return holidays.contains(date);
    }

    /** 根据开始时间、结束时间和日历计算工期 */
    public static int getWorkdates(String startDate, String endDate, Collection<String> holidays) throws ParseException {
        if (equals(startDate, endDate)) {
            return 1;
        }

        boolean asc = before(startDate, endDate);
        String temp = startDate;
        int result = 0;
        for (;;) {
            if (asc) {
                result++;
            } else {
                result--;
            }
            temp = asc ? getNextWorkday(temp, holidays) : getLastWorkday(temp, holidays);

            if ((asc && after(temp, endDate)) || (!asc && before(temp, endDate))) {
                return result;
            }
        }
    }

    /** 补全时间，如果2007-6-2补全为2007-06-02 */
    public static String fullfillDateTime(String date) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);
        long t = oldDate.getTime();
        return getDateFromTimestamp(new Timestamp(t));
    }

    /** 补全时间，如果2007-6-2 00:00:00补全为2007-06-02 00:00:01精确到秒 */
    public static String fullDateTime(String date) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate = dateFormat.parse(date);
        long t = oldDate.getTime();
        return getDateTimeFromTimestamp(new Timestamp(t));
    }

    /** 获取给定日期的周信息（如2007-12-04返回的是2007-12-02 ~ 2007-12-08） */
    public static String getWeekRange(String date) throws ParseException {
        return getWeekRange(string2Date(date));
    }

    /** 获取给定日期的周信息（如2007-12-04返回的是2007-12-02 ~ 2007-12-08） */
    public static String getWeekRange(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        /*
         * 日期从周一到周日 Date monday = (Date) date.clone(); monday.setTime(monday.getTime() - TIME_OF_A_DAY *
         * (dayOfWeek-1-1)); Date sunday = (Date) date.clone(); sunday.setTime(sunday.getTime() + TIME_OF_A_DAY *
         * (7-dayOfWeek+1)); return getDateFromTimestamp(monday) + " ~ " + getDateFromTimestamp(sunday);
         */
        Date sunday = (Date) date.clone();
        sunday.setTime(sunday.getTime() - TIME_OF_A_DAY * (dayOfWeek - 1));
        Date saturday = (Date) date.clone();
        saturday.setTime(saturday.getTime() + TIME_OF_A_DAY * (7 - dayOfWeek));

        return getDateFromTimestamp(sunday) + " ~ " + getDateFromTimestamp(saturday);

    }

    /** 获取给当前日期的周信息（如2007-12-04返回的是2007-12-02 ~ 2007-12-08） */
    public static String getWeekRange() {
        return getWeekRange(new Date());
    }

    /** 获取给定日期是在当年的第几周 */
    public static int getWeekOfYear(String date) throws ParseException {
        return getWeekOfYear(string2Date(date));
    }

    /** 获取给定日期是在当年的第几周 */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /** 获取当前日期是在当年的第几周 */
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    /** 获取给定日期是在当月的第几天（如2008-03-27返回的是27） */
    public static int getDayOfMonth(String date) throws ParseException {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
        Date oldDate;
        oldDate = dateFormat.parse(date);
        Calendar c = Calendar.getInstance();
        c.setTime(oldDate);
        return c.get(Calendar.DAY_OF_MONTH);

    }

    public static String getWeekStart() {
        return getWeekStart(getWeekRange());
    }

    public static String getWeekStart(Date date) {
        return getWeekStart(getWeekRange(date));
    }

    public static String getWeekStart(String weekRange) {
        return weekRange.split("\\s~\\s")[0];
    }

    public static String getWeekEnd() {
        return getWeekEnd(getWeekRange());
    }

    public static String getWeekEnd(Date date) {
        return getWeekEnd(getWeekRange(date));
    }

    public static String getWeekEnd(String weekRange) {
        return weekRange.split("\\s~\\s")[1];
    }

    /**
     * 将秒换成小时:分:秒
     * 
     * @param s
     * @return
     * @throws ParseException
     */
    public static String convertSecondTohhMMss(Long sec) throws ParseException {
        StringBuffer timeStr = new StringBuffer();
        int h = (int) (sec / (60 * 60));// 小时
        int minTemp = (int) (sec % (60 * 60));
        int m = (int) minTemp / 60;// 分
        int s = (int) (minTemp % 60);// 秒

        timeStr.append(h < 10 ? "0" + h : h).append(":").append(m < 10 ? "0" + m : m).append(":")
                .append(s < 10 ? "0" + s : s);
        return timeStr.toString();
    }

    /**
     * 将所给日期字符串重新格式化<br>
     * 如：<br>
     * <code>TimeUtils.reformatDate("20080204", "yyyyMMdd", "yyyy-MM-dd");</code> <br>
     * <code>TimeUtils.reformatDate("2008-02-04", "yyyy-MM-dd", "yyyyMMdd");</code> <br>
     */
    public static String reformatDate(String date, String oldFormat, String newFormat) throws ParseException {
        DateFormat format = new SimpleDateFormat(oldFormat);
        Date d = format.parse(date);
        return format.format(d);
    }

    public static Timestamp toDateTime(String arg) {
        Locale locale;
        Timestamp ret;
        locale = Locale.ENGLISH;
        if (arg == null || "".equals(arg))
            return null;
        ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e2) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e3) {

        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", locale);
            return new Timestamp(sdf.parse(arg).getTime());
        } catch (Exception e4) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.M", locale);
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e5) {

        }
        throw new IllegalArgumentException("参数非法:" + arg);
    }

    public static Timestamp toDateTime(String arg, boolean defaultValue) {
        Timestamp ret;
        if (!defaultValue)
            return toDateTime(arg);
        if (arg == null || "".equals(arg))
            return new Timestamp(System.currentTimeMillis());
        ret = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ret = new Timestamp(sdf.parse(arg).getTime());
            return ret;
        } catch (ParseException e2) {

        }
        return new Timestamp(System.currentTimeMillis());
    }

    public static Date toDate(String arg) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(arg);
        } catch (ParseException e) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.parse(arg);
        } catch (ParseException e1) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(arg);
        } catch (ParseException e2) {

        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            return sdf.parse(arg);
        } catch (ParseException e2) {

        }
        return new Date();
    }

    public static String toDateFormat(Timestamp datetime) {
        return toDateFormat(datetime, null);
    }

    public static String toDateFormat(Date datetime) {
        return toDateFormat(datetime, null);
    }

    public static String toDateFormat(Timestamp datetime, String pattern) {
        SimpleDateFormat sdf = null;
        if (datetime == null)
            return null;
        try {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:ss";
            sdf = new SimpleDateFormat(pattern);
        } catch (Exception e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss");
        }
        return sdf.format(datetime);
    }

    public static String toDateFormat(Date datetime, String pattern) {
        SimpleDateFormat sdf = null;
        if (datetime == null)
            return null;
        try {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:ss";
            sdf = new SimpleDateFormat(pattern);
        } catch (Exception e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss");
        }
        return sdf.format(datetime);
    }

    /**
     * 得到某一天的凌晨时间
     * 
     * @param date
     * @return
     */
    public static Date getToday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 得到某一天的23：59 时间
     * 
     * @param date
     * @return
     */
    public static Date getTodayLast(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * 得到某一天的23：59 时间
     * 
     * @param date
     * @return
     */
    public static Date getToday23(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 得到今天凌晨的时间. GameFunction.getToday().getTime() 相当于 php中 date('Y-m-d');
     * 
     * @return
     */
    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 根据用户生日计算年龄
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        return age;
    }

    /**
     * 
     * Created on 2014年10月2日
     * <p>
     * Description:显示日期，周，上下午，时间（精确到秒）
     * </p>
     * 
     * @author:刘宝仲
     * @param date
     * @return
     */
    public static String getDateTimeInstanceFull(Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        return dateFormat.format(date);
    }

    /**
     * 
     * Created on 2014年10月2日
     * <p>
     * Description:显示日期，上下午,时间（精确到分）
     * </p>
     * 
     * @author:刘宝仲
     * @param date
     * @return
     */
    public static String getDateTimeInstanceShort(Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return dateFormat.format(date);
    }

    public static String getDateTimeInstanceMEDIUM(Date date) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        return dateFormat.format(date);
    }

    /**
     * 
     * Created on 2014年10月2日
     * <p>
     * Description:获取年月日(上午/下午)时分
     * </p>
     * 
     * @author:刘宝仲
     * @param date
     * @return
     */
    public static String time2ChineseDate(Date date) {
        String formatDate = timestamp2String("yyyy年M月d日", date);
        String hour = timestamp2String("H", date);
        Integer hourInt = Integer.parseInt(hour);
        String str = "";
        if (hourInt > 12) {
            str = "下午" + (hourInt - 12);
        } else {
            str = "上午" + hourInt;
        }
        return formatDate + " " + str + timestamp2String(":mm", date);
    }
    /**
     * 
     * Created on 2014年10月2日
     * <p>
     * Description:获取年月日(上午/下午)时分
     * </p>
     * 
     * @author:刘宝仲
     * @param date
     * @return
     */
    public static String[] time2ChineseStrs(Date date) {
        String formatDate = timestamp2String("yyyy年M月d日", date);
        String hour = timestamp2String("H", date);
        Integer hourInt = Integer.parseInt(hour);
        String str = "";
        if (hourInt > 12) {
            str = "下午";
            hourInt=hourInt - 12;
        } else {
            str = "上午";
        }
        String [] strs=new String[3];
        strs[0]=hourInt+""+timestamp2String(":mm", date);
        strs[1]=str;
        strs[2]=formatDate;
        return strs;
    }
    /**
     * 
     *  Created on 2015年1月29日 
    * Description:获取年月号(上午/下午)时分
     * @author:刘宝仲
     * @param date
     * @return
     */
    public static String time2ChineseDateFormat(Date date) {
        String formatDate = timestamp2String("yyyy年M月d号", date);
        String hour = timestamp2String("H", date);
        Integer hourInt = Integer.parseInt(hour);
        String str = "";
        if (hourInt > 12) {
            str = "下午" + (hourInt - 12);
        } else {
            str = "上午" + hourInt;
        }
        return formatDate + " " + str + timestamp2String(":mm分", date);
    }
    
    /**
     * 
     *  Created on 2015年1月29日 
    * Description:获取年月号(上午/下午)时分
     * @author:刘宝仲
     * @param date
     * @return
     */
    public static String time3ChineseDateFormat(Date date) {
        return timestamp2String("yyyy年M月d日", date);
    }
    /**
     * 
     * 功能描述: <br>
     * 计算传入时间和当前时间的间隔天数
     * 
     * @param date
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static long calculationIntervalDay(Date date) {
        long time = date.getTime();
        long current = (new Date()).getTime();
        return (current - time) / (1000 * 60 * 60 * 24);
    }
    
    /**
     * 
     * 功能描述: <br>
     * 2个时间之间的间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Long getDaysBetween(Date startDate, Date endDate) {  
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(startDate);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(endDate);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);  
    } 
    
    public static Date addSeconds(Date date, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, amount);
        return c.getTime();
    }

    /**
     * 
     * 功能描述: <br>
     * 计算传入时间和当前时间的间隔天数
     * 
     * @param date
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static String showBeInterviewDateFormat(Date date) {

        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }

        SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat dateFm1 = new SimpleDateFormat("HH:mm");

        return new StringBuilder().append(dateFm.format(date)).append(" ").append(weekDays[w]).append(" ")
                .append(dateFm1.format(date)).toString();
    }
    public static void main(String[] args) {
/*    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
    try {
        String dfString=dateFormat.format(new Date());
        Date date=dateFormat.parse(dfString);
        System.out.println(dfString);
        System.out.println(date);
//        System.out.println(dateFormat.parse("2014-01"));
//        System.out.println(afterAll("2014-10-23 21:10:01","2014-10-22 21:10:01"));
    } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }*/
//    System.out.println(getTodayLast(addDaysToDate(new Date(), 1)));
//    System.out.println(Math.ceil(0.0 /10));
//    System.out.println(timestamp2String("yyyy年MM月dd日 HH:mm",new Date()));
    System.out.println(time2ChineseDateFormat(new Date()));
}
}
