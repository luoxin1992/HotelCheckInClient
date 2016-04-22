package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 初始化界面
 */
public class InitActivity extends BaseActivity {
	private static final String TAG = InitActivity.class.getSimpleName();

	private InitReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView(false, null, false, 0, R.layout.activity_init, false);
	}

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver();
		bindCoreService();
		bindThirdpartyService();
	}

	@Override
	protected void onPause() {
		super.onPause();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_SERIVCE_BOUND);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_INIT_OK);
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		receiver = new InitReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	class InitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent newIntent;
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INIT_WELCOME);
				break;
			case Broadcast.CORE_SERIVCE_BOUND:
				getCoreServiceBinder().init();
				break;
			case Broadcast.CORE_INIT_OK:
				newIntent = new Intent(InitActivity.this, SwipeCardActivity.class);
				newIntent.putExtra("from", InitActivity.class.getSimpleName());
				newIntent.putExtra("next", VoiceprintActivity.class.getSimpleName());
				SystemUtil.setPreferences(InitActivity.this, "notice", intent.getStringExtra("notice"));
				startActivity(newIntent);
				finish();
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INIT_FAIL);
				finish();
				break;
			}
		}
	}
}
