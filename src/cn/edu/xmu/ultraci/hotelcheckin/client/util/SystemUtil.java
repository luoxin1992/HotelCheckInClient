package cn.edu.xmu.ultraci.hotelcheckin.client.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

public class SystemUtil {
	/**
	 * 发送应用内广播
	 * 
	 * @param context
	 *            上下文
	 * @param broadcast
	 *            广播内容
	 */
	public static void sendLocalBroadcast(Context context, Intent broadcast) {
		LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
	}

	/**
	 * 查询设备ID(通常为手机的IMEI或MEID)
	 * 
	 * 
	 * @param context
	 *            上下文
	 * @return 查询结果
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * 查询设备无线网卡MAC地址
	 * 
	 * @param context
	 *            上下文
	 * @return 查询结果
	 */
	public static String getMacAddress(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}

	/**
	 * 查询SharedPreferences<br>
	 * 只支持查询类型为String的值<br>
	 * 如果给定键不存在则返回null
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            键
	 * @return 值
	 */
	public static String getPreferences(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getString(name, null);
	}

	/**
	 * 反射调用服务中的方法
	 * 
	 * @param service
	 *            服务
	 * @param name
	 *            方法名
	 * @param types
	 *            形参
	 * @param values
	 *            实参
	 * @return 方法返回值
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeServiceMethod(Service service, String name, Class<?>[] types, Object[] values)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method m = service.getClass().getMethod(name, types);
		return m.invoke(service, values);
	}
}
