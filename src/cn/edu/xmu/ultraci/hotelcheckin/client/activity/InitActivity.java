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
	protected void onStart() {
		super.onStart();

		registerReceiver();
		bindCoreService();
		bindThirdpartyService();
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_SERIVCE_BOUND);
		filter.addAction(Broadcast.MISC_SERIVCE_BOUND);
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.CORE_INIT_OK);
		filter.addAction(Broadcast.CORE_QUERY_INFO_OK);
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

	public boolean isInitOk() {
		if (++eventCounter >= 5) {
			return true;
		} else {
			return false;
		}
	}

	public void toMainActivity() {
		Intent intent = new Intent(InitActivity.this, SwipeCardActivity.class);
		intent.putExtra("action", Action.CLIENT_LOGIN);
		intent.putExtra("extras", new Bundle());
		startActivity(intent);
		finish();
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
				getThirdpartyServiceBinder().synthesicSpeech(TTS.WELCOME);
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (isInitOk()) {
					toMainActivity();
				}
				break;
			case Broadcast.CORE_INIT_OK:
				SystemUtil.setPreferences(InitActivity.this, "announcement", intent.getStringExtra("announcement"));
				if (isInitOk()) {
					toMainActivity();
				}
				break;
			case Broadcast.CORE_QUERY_INFO_OK:
				InfoDTO retModel = (InfoDTO) intent.getSerializableExtra("retModel");
				SystemUtil.setPreferences(InitActivity.this, "name", retModel.getContent().get("name"));
				SystemUtil.setPreferences(InitActivity.this, "address", retModel.getContent().get("address"));
				SystemUtil.setPreferences(InitActivity.this, "telephone", retModel.getContent().get("telephone"));
				SystemUtil.setPreferences(InitActivity.this, "notice", retModel.getContent().get("notice"));
				if (isInitOk()) {
					toMainActivity();
				}
				break;
			case Broadcast.MISC_BLUETOOTH_NONSUPPORT:
				// TODO 弹对话框提示不支持NFC
				break;
			case Broadcast.MISC_BLUETOOTH_DISABLE:
				// TODO 弹对话框提示启动蓝牙
				break;
			case Broadcast.MISC_BLUETOOTH_OK:
				if (isInitOk()) {
					toMainActivity();
				}
				break;
			case Broadcast.MISC_NFC_NONSUPPORT:
				// TODO 弹对话框提示不支持NFC
				break;
			case Broadcast.MISC_NFC_DISABLE:
				// TODO 弹对话框提示启动NFC
				break;
			case Broadcast.MISC_NFC_OK:
				if (isInitOk()) {
					toMainActivity();
				}
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				finish();
				break;
			}
		}
	}
}
