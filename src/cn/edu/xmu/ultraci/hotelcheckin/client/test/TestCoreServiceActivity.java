package cn.edu.xmu.ultraci.hotelcheckin.client.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.CoreService;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.CoreService.CoreServiceBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.SmsService;

public class TestCoreServiceActivity extends Activity {

	private ServiceConnection conn;
	private CoreServiceBinder binder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_core_service);
		conn();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		disconn();
	}

	public void conn() {
		Intent service = new Intent(this, CoreService.class);
		conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				System.out.println("activity服务断开");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder = (CoreServiceBinder) service;
				System.out.println("activity服务连接");
			}
		};
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	public void disconn() {
		unbindService(conn);
	}

	public void init(View v) {
		binder.init();
	}

	public void heartbeat(View v) {
		binder.heartbeat();
	}

	public void sms(View v) {
		new SmsService().sendCaptcha();
	}
}
