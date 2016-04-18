package cn.edu.xmu.ultraci.hotelcheckin.client.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

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
}
