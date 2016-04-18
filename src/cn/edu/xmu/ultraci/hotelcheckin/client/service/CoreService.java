package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 核心业务逻辑实现<br>
 * 主要用于同服务端交互
 * 
 * @author LuoXin
 *
 */
public class CoreService extends Service {

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
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void heartbeat(){
		RequestParams params = new RequestParams();
		params.add("action", Action.HEARTBEAT);
		params.add("device", SystemUtil.getMacAddress(this));
		
	}

}
