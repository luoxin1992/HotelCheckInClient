package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

public class ServerCrashActivity extends BaseActivity {

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

	public void initView() {
		setContent(false, null, false, 0, R.layout.activity_server_crash, true);
	}
}
