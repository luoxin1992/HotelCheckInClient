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
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		receiver = new MainReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(false, null, false, 0, R.layout.activity_main, true);
	}

	public void toSubfunction(View v) {
		Intent newIntent;
		switch (v.getId()) {
		case R.id.btn_member_checkin:
			newIntent = new Intent(this, SwipeCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_MEMBER_CHECKIN);
			startActivity(newIntent);
			break;
		case R.id.btn_guest_checkin:
			newIntent = new Intent(this, CaptureIdCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_GUEST_CHECKIN);
			startActivity(newIntent);
			break;
		case R.id.btn_extension:
			newIntent = new Intent(this, SwipeCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_EXTENSION);
			startActivity(newIntent);
			break;
		case R.id.btn_checkout:
			newIntent = new Intent(this, SwipeCardActivity.class);
			newIntent.putExtra("action", Action.CLIENT_CHECKOUT);
			startActivity(newIntent);
			break;
		case R.id.btn_about:
			newIntent = new Intent(this, AboutActivity.class);
			newIntent.putExtra("action", Action.CLIENT_ABOUT);
			startActivity(newIntent);
			break;
		}
	}

	class MainReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	}

}
