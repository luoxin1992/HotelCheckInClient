package cn.edu.xmu.ultraci.hotelcheckin.client.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
	/**
	 * 获得当前系统时间
	 * 
	 * @return 系统时间
	 */
	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 格式化给定时间
	 * 
	 * @param timeMillis
	 *            要格式化的时间
	 * @return 格式化的时间
	 */
	public static String formatDateTime(long timeMillis) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
		return df.format(new Date(timeMillis));
	}
}
