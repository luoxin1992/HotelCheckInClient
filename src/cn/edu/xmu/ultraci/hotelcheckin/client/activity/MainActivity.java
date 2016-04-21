package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.os.Bundle;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {

	private TextView tvNotice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView(R.layout.activity_main);
	}

	public void initView(int resId) {
		super.initView(R.layout.activity_main);
		tvNotice = (TextView) findViewById(R.id.tv_notice);
		tvNotice.setText(SystemUtil.getPreferences(this, "notice"));
	}
}
