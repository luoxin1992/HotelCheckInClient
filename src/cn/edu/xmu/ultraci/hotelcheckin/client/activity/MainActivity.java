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
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private MainReceiver receiver;

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Code.CHANGE_UI && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(Broadcast.CORE_SERVER_REQUEST_FAIL);
		receiver = new MainReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(false, null, false, 0, R.layout.activity_main, true);
	}

	public void toSubfunction(View v) {
		Intent newIntent;
		switch (v.getId()) {
		case R.id.ib_member_checkin:
			newIntent = new Intent(this, SwipeCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_MEMBER_CHECKIN);
			newIntent.putExtra("extras", new Bundle());
			startActivity(newIntent);
			break;
		case R.id.ib_guest_checkin:
			newIntent = new Intent(this, CaptureIdCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_GUEST_CHECKIN);
			newIntent.putExtra("extras", new Bundle());
			startActivity(newIntent);
			break;
		case R.id.ib_extension:
			newIntent = new Intent(this, SwipeCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_EXTENSION);
			newIntent.putExtra("extras", new Bundle());
			startActivity(newIntent);
			break;
		case R.id.ib_checkout:
			newIntent = new Intent(this, SwipeCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_CHECKOUT);
			newIntent.putExtra("extras", new Bundle());
			startActivity(newIntent);
			break;
		case R.id.ib_about:
			newIntent = new Intent(this, AboutActivity.class);
			startActivity(newIntent);
			break;
		}
	}

	class MainReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
				Intent newIntent = new Intent(MainActivity.this, ServerCrashActivity.class);
				MainActivity.this.startActivity(newIntent);
				break;
			}
		}
	}

}
