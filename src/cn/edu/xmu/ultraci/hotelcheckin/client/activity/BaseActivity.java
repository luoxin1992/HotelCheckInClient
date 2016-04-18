package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

public class BaseActivity extends Activity {

	private TextClock tcClock;
	private TextView tvCountdown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);

	}

	public void initView() {
		tcClock = (TextClock) findViewById(R.id.tc_clock);
		tvCountdown = (TextView) findViewById(R.id.tv_countdown);
	}

	/**
	 * 配置界面超时返回<br>
	 * 0表示永不超时，倒计时区域将显示当前时间
	 * 
	 * @param seconds
	 *            超时时间
	 */
	public void setCountdown(int seconds) {
		if (seconds == 0) {
			tcClock.setVisibility(View.VISIBLE);
			tvCountdown.setVisibility(View.GONE);
		} else {
			tcClock.setVisibility(View.GONE);
			tvCountdown.setVisibility(View.VISIBLE);
		}
	}
}
