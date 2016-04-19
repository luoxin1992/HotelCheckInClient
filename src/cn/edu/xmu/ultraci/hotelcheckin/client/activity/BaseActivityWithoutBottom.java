package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextClock;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

public class BaseActivityWithoutBottom extends Activity {

	private TextClock tcClock;
	private TextView tvCountdown;
	private LinearLayout llMain;
	private PopupWindow pwTest;

	private Handler handler;
	private int countdown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_without_bottom);
		initView();
		setCountdown(60);
	}

	public void initView() {
		tcClock = (TextClock) findViewById(R.id.tc_clock);
		tvCountdown = (TextView) findViewById(R.id.tv_countdown);
		llMain = (LinearLayout) findViewById(R.id.ll_main);
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
			this.countdown = seconds;
			handler = new Handler();
			handler.post(mTicker);
		}
	}

	public void addView(View v) {
		View textView = View.inflate(this, R.layout.test_dync_view, null);
		llMain.addView(textView);
	}

	@SuppressLint("NewApi")
	public void popupView(View v) {
		System.out.println("popup view");
		View popupView = View.inflate(this, R.layout.test_popup_view, null);
		pwTest = new PopupWindow(popupView);
		pwTest.showAsDropDown(llMain, Gravity.CENTER, 0, 0);
	}

	private final Runnable mTicker = new Runnable() {
		public void run() {
			tvCountdown.setText((countdown--) + "这里也能点噢");
			long now = SystemClock.uptimeMillis();
			long next = now + (1000 - now % 1000);
			handler.postAtTime(mTicker, next);
		}
	};
}
