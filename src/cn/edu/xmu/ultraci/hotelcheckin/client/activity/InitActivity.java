package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.CoreService;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.CoreService.CoreServiceBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.ThirdpartyService;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.ThirdpartyService.ThirdpartyServiceBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 初始化界面
 */
public class InitActivity extends Activity {

	private ServiceConnection coreConn;
	private ServiceConnection thirdpartyConn;
	private CoreServiceBinder coreBinder;
	private ThirdpartyServiceBinder thirdpartyBinder;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);

		bindService();
		initReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(coreConn);
		unbindService(thirdpartyConn);
	}

	public void bindService() {
		coreConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				coreBinder = (CoreServiceBinder) service;
				coreBinder.init();
			}
		};
		thirdpartyConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				thirdpartyBinder = (ThirdpartyServiceBinder) service;
				thirdpartyBinder.synthesicSpeech(TTS.INIT_OK);
			}
		};
		bindService(new Intent(this, CoreService.class), coreConn, BIND_AUTO_CREATE);
		bindService(new Intent(this, ThirdpartyService.class), thirdpartyConn, BIND_AUTO_CREATE);
	}

	public void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_INIT_OK);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (intent.getAction()) {
				case Broadcast.CORE_INIT_OK:
					SystemUtil.setPreferences(InitActivity.this, "notice", intent.getStringExtra("notice"));
					startActivity(new Intent(InitActivity.this, MainActivity.class));
					finish();
					break;
				case Broadcast.CORE_SERVER_REQUEST_FAIL:
				case Broadcast.CORE_SERVER_PROCESS_FAIL:
					thirdpartyBinder.synthesicSpeech(TTS.INIT_FAIL);
					finish();
					break;
				}
			}
		};
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}
}
