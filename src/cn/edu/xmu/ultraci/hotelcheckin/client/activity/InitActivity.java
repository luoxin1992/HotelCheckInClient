package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.InfoDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 初始化界面
 */
public class InitActivity extends BaseActivity {
	private static final String TAG = InitActivity.class.getSimpleName();

	private InitReceiver receiver;

	private int eventCounter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver();
		bindCoreService();
		bindMiscService();
		bindThirdpartyService();
	}

	@Override
	protected void onPause() {
		super.onPause();

		SystemUtil.unregisterLocalBroadcast(this, receiver);
		unbindService();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_SERIVCE_BOUND);
		filter.addAction(Broadcast.MISC_SERIVCE_BOUND);
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.CORE_INIT_OK);
		filter.addAction(Broadcast.CORE_QUERY_INFO_OK);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.MISC_BLUETOOTH_NONSUPPORT);
		filter.addAction(Broadcast.MISC_BLUETOOTH_DISABLE);
		filter.addAction(Broadcast.MISC_NFC_NONSUPPORT);
		filter.addAction(Broadcast.MISC_NFC_DISABLE);
		filter.addAction(Broadcast.MISC_BLUETOOTH_OK);
		filter.addAction(Broadcast.MISC_NFC_OK);
		receiver = new InitReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(false, null, false, 0, R.layout.activity_init, false);
	}

	public void initOk() {
		if (++eventCounter >= 5) {
			Intent intent = new Intent(InitActivity.this, SwipeCardActivity.class);
			intent.putExtra("action", Action.CLIENT_LOGIN);
			intent.putExtra("extras", new Bundle());
			startActivity(intent);
			finish();
		}
	}

	class InitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.CORE_SERIVCE_BOUND:
				getCoreServiceBinder().init();
				getCoreServiceBinder().info("basic");
				break;
			case Broadcast.MISC_SERIVCE_BOUND:
				getMiscServiceBinder().checkBluetooth();
				getMiscServiceBinder().checkNFC();
				break;
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INIT_WELCOME);
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				initOk();
				break;
			case Broadcast.CORE_INIT_OK:
				SystemUtil.setPreferences(InitActivity.this, "announcement", intent.getStringExtra("announcement"));
				initOk();
				break;
			case Broadcast.CORE_QUERY_INFO_OK:
				InfoDTO retModel = (InfoDTO) intent.getSerializableExtra("retModel");
				SystemUtil.setPreferences(InitActivity.this, "name", retModel.getContent().get("name"));
				SystemUtil.setPreferences(InitActivity.this, "address", retModel.getContent().get("address"));
				SystemUtil.setPreferences(InitActivity.this, "telephone", retModel.getContent().get("telephone"));
				SystemUtil.setPreferences(InitActivity.this, "notice", retModel.getContent().get("notice"));
				initOk();
				break;
			case Broadcast.MISC_BLUETOOTH_NONSUPPORT:
				showDialog(R.drawable.warn, TTS.INIT_BLUETOOTH_NONSUPPORT);
				finish();
				break;
			case Broadcast.MISC_BLUETOOTH_DISABLE:
				showDialog(R.drawable.warn, TTS.INIT_BLUETOOTH_DISABLE);
				finish();
				break;
			case Broadcast.MISC_BLUETOOTH_OK:
				initOk();
				break;
			case Broadcast.MISC_NFC_NONSUPPORT:
				showDialog(R.drawable.warn, TTS.INIT_NFC_NONSUPPORT);
				finish();
				break;
			case Broadcast.MISC_NFC_DISABLE:
				showDialog(R.drawable.warn, TTS.INIT_NFC_DISABLE);
				finish();
				break;
			case Broadcast.MISC_NFC_OK:
				initOk();
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				showDialog(R.drawable.warn, TTS.INTERNAL_ERROR);
				finish();
				break;
			}
		}
	}
}
