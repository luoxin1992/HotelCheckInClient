package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import org.apache.commons.codec.android.digest.DigestUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.HttpUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;
import cz.msebera.android.httpclient.Header;

public class SmsService {
	public void sendCaptcha() {
		System.out.println("CALL SMS API");
		String url = "http://www.ucpaas.com/maap/sms/code";
		// 主账户ID和应用ID
		String sid = "43ef51d0d2a396003a4ffbaa4215dc06";
		String appid = "ad3110009f6e41f2960718e159da66e3";
		// 时间戳和短信模板
		String time = TimeUtil.formatDateTime(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
		String templateId = "23022";
		// 验证信息
		String token = "0e86ae119f6bea5913b373d7f5718bfa";
		String sign = DigestUtils.md5Hex(sid + time + token).toLowerCase();
		// 接收手机和参数
		String to = "13235809587";
		String params = "123456";
		// 调接口
		RequestParams paramsMap = new RequestParams();
		paramsMap.put("sid", sid);
		paramsMap.put("appid", appid);
		paramsMap.put("sign", sign);
		paramsMap.put("time", time);
		paramsMap.put("templateId", templateId);
		paramsMap.put("to", to);
		paramsMap.put("params", params);
		System.out.println(paramsMap);
		HttpUtil.post(url, paramsMap, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.i("SMS", "SMS SUCCESS!");
				Log.i("SMS", new String(arg2));
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Log.e("SMS", "SMS FAILURE!" + arg0);
			}
		});
	}
}
