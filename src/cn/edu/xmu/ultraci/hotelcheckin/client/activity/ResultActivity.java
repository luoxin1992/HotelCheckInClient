package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 结果界面
 */
public class ResultActivity extends BaseActivity {
	private static final String TAG = ResultActivity.class.getSimpleName();

	private ResultReceiver receiver;

	private String action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action = getIntent().getStringExtra("action");

		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver();
		bindThirdpartyService();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	public void initView() {
		setContent(false, getTitle().toString(), true, 15, R.layout.activity_result, false);
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		receiver = new ResultReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void onClose(View v) {
		setResult(RESULT_OK);
		finish();
	}

	class ResultReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				switch (action) {
				case Action.CLIENT_MEMBER_CHECKIN:
				case Action.CLIENT_GUEST_CHECKIN:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.RESULT_CHECKIN_HINT);
					break;
				case Action.CLIENT_EXTENSION:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.RESULT_EXTENSION_HINT);
					break;
				case Action.CLIENT_CHECKOUT:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.RESULT_CHECKOUT_HINT);
					break;
				}
				break;
			}
		}
	}

}
