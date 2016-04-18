package cn.edu.xmu.ultraci.hotelcheckin.client;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;

import android.app.Application;

/**
 * 应用程序入口
 * 
 * @author LuoXin
 *
 */
public class AppEntry extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化科大讯飞语音云
		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
		Setting.setShowLog(false);
	}
}
