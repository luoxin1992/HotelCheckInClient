package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

public class ServerCrashActivity extends BaseActivity {
	private static final String TAG = ServerCrashActivity.class.getSimpleName();

	private ServerCrashReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver();
	}

	@Override
	protected void onStop() {
		super.onStop();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	public void initView() {
		setContent(false, null, false, 0, R.layout.activity_server_crash, true);
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(Broadcast.CORE_SERVER_OK);
		receiver = new ServerCrashReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	class ServerCrashReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.CORE_SERVER_OK:
				ServerCrashActivity.this.setResult(RESULT_OK);
				ServerCrashActivity.this.finish();
				break;
			}
		}
	}
}
