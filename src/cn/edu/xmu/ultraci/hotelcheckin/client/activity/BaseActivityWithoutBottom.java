package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

/**
 * 基础布局(无底栏)<br>
 * <ul>
 * 顶部:标题&倒计时<br>
 * 中间:动态填充其他布局<br>
 * </ul>
 * 
 * @author LuoXin
 *
 */
public abstract class BaseActivityWithoutBottom extends Activity {

	private TextView tvTitle;
	private TextView tvCountdown;
	private LinearLayout llMain;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_without_bottom);
	}

	@Override
	public void onBackPressed() {
		// 屏蔽系统返回键
		// super.onBackPressed();
	}

	public void onSoftBackPressed(View v) {
		finish();
	}

	/**
	 * 用布局填充器动态填充布局
	 * 
	 * @param resId
	 *            布局文件ID
	 */
	public void setView(int resId) {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvCountdown = (TextView) findViewById(R.id.tv_countdown);
		llMain = (LinearLayout) findViewById(R.id.ll_main);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(resId, null);
		llMain.addView(view);

		tvTitle.setText(getTitle());
	}

	/**
	 * 配置界面超时返回<br>
	 * 0表示永不超时
	 * 
	 * @param seconds
	 *            超时时间
	 */
	public void setTimeout(int seconds) {
		if (seconds > 0) {
			mHandler = new Handler();
			mHandler.post(new Ticker(seconds));
		}
	}

	class Ticker implements Runnable {
		private int countdown;

		public Ticker(int countdown) {
			this.countdown = countdown;
		}

		@Override
		public void run() {
			if ((countdown--) != 0) {
				tvCountdown.setText(String.valueOf(countdown));
			} else {
				finish();
			}
			mHandler.postDelayed(this, 1000);
		}
	}
}
