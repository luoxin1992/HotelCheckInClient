package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;

/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {
	private static final String TAG = AboutActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Code.CHANGE_UI && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	public void initView() {
		setContent(true, getTitle().toString(), false, 0, R.layout.activity_about, false);
	}

	public void onExit(View v) {
		Intent intent = new Intent(this, SwipeCardActivity.class);
		intent.putExtra("action", Action.CLIENT_LOGOUT);
		intent.putExtra("extras", new Bundle());
		startActivityForResult(intent, Code.CHANGE_UI);
	}

}
