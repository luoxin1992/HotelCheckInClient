package cn.edu.xmu.ultraci.hotelcheckin.client.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
	/**
	 * 以系统默认格式格式化给定时间
	 * 
	 * @param timeMillis
	 *            要格式化的时间
	 * @return 格式化的时间
	 */
	public static String formatDateTime(long timeMillis) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
		return df.format(new Date(timeMillis));
	}

	/**
	 * 以自定义格式格式化给定时间
	 * 
	 * @param timeMillis
	 *            要格式化的时间
	 * @param pattern
	 *            自定义格式
	 * @return 格式化的时间
	 */
	public static String formatDateTime(long timeMillis, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
		return sdf.format(new Date(timeMillis));
	}

	/**
	 * 以系统默认格式解析给定时间
	 * 
	 * @param time
	 *            要解析的时间
	 * @return 格式化的时间
	 */
	public static long parseDateTime(String time) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
		try {
			return df.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 以自定义格式解析给定时间
	 * 
	 * @param time
	 *            要解析的时间
	 * @param pattern
	 *            自定义格式
	 * @return 解析的时间
	 */
	public static long parseDateTime(String time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
		try {
			return sdf.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 计算两个日期之差
	 * 
	 * @param date1
	 *            靠前的日期
	 * @param date2
	 *            靠后的日期
	 * @return 日期之差
	 */
	public static int dateInterval(long date1, long date2) {
		if (date1 > date2) {
			return -1;
		} else {
			int y1, d1, y2, d2;
			Calendar c1 = Calendar.getInstance(Locale.getDefault());
			Calendar c2 = Calendar.getInstance(Locale.getDefault());
			// 取出两个给定的日期分别是哪一年的第几天
			c1.setTimeInMillis(date1);
			y1 = c1.get(Calendar.YEAR);
			d1 = c1.get(Calendar.DAY_OF_YEAR);
			c2.setTimeInMillis(date2);
			y2 = c2.get(Calendar.YEAR);
			d2 = c2.get(Calendar.DAY_OF_YEAR);

			if (y1 == y2) {
				// 如果两个日期位于同一年，直接相减即为日期之差
				return d2 - d1;
			} else {
				// 否则，(假设两个日期间隔不超过一年)
				// 计算前一年份还剩多少天、后一个年份已过多少天，再两者相加
				// TODO 如果假设不成立，注意闰年要多一天
				int remain = c1.getActualMaximum(Calendar.DAY_OF_YEAR) - d1;
				return remain + d2;
			}
		}
	}

	/**
	 * 判断给定时间与当前时间只差是否超出指定阈值
	 * 
	 * @param time
	 *            时间
	 * @param threshold
	 *            阈值(秒)
	 * @return 判断结果
	 */
	public static boolean timeIntervalLimited(String time, long threshold) {
		return (System.currentTimeMillis() - parseDateTime(time) >= threshold * 1000);
	}
}
