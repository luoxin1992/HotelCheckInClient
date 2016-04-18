package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;

import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.RandomUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 核心业务逻辑实现<br>
 * 主要用于同服务端交互
 * 
 * @author LuoXin
 *
 */
public class CoreService extends Service {
	private static final String TAG = CoreService.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new CoreServiceBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void getBaseUrl() {
		String baseUrl = SystemUtil.getPreferences(this, "server");
	}

	/**
	 * 根据文档5.1节所述算法计算加密签名
	 * 
	 * @param context
	 *            上下文
	 * @param random
	 *            随机字符串
	 * @return 加密签名
	 */
	private String getSignature(Context context, String random) {
		// 从配置中取Token
		String token = SystemUtil.getPreferences(this, "token");
		// 排序和合并
		String[] array = new String[] { token, random };
		Arrays.sort(array);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}
		// 计算加密签名
		return DigestUtils.sha1Hex(sb.toString());
	}

	private void addAuthParams(RequestParams params) {
		String random = RandomUtil.generateRandomStr(6);
		String signature = getSignature(this, random);
		params.add("random", RandomUtil.generateRandomStr(6));
		params.add("signature", signature);
	}

	public void heartbeat() {
		RequestParams params = new RequestParams();
		params.add("action", Action.HEARTBEAT);
		params.add("device", SystemUtil.getMacAddress(this));
		addAuthParams(params);
		// HttpUtil.post(url, params, new );
	}

	class CoreServiceBinder extends Binder {
	}

}
