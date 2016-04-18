package cn.edu.xmu.ultraci.hotelcheckin.client.util;

import android.content.Context;
import android.content.Intent;
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
}
