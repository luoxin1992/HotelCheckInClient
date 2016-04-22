package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private MainReceiver receiver;

	private String action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		bindCoreService();
		bindThirdpartyService();
	}

	@Override
	protected void onPause() {
		super.onPause();

		SystemUtil.unregisterLocalBroadcast(this, receiver);
		unbindService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);

		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		receiver = new MainReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		initView(false, null, false, 0, R.layout.activity_main, true);
	}

	public void updateView() {

	}

	class MainReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	}

}
