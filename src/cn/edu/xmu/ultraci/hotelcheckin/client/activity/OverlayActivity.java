package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

/**
 * 覆盖层界面<br>
 * 以非全屏的方式叠加在其他Activity之上
 * 
 * @author LuoXin
 *
 */
public class OverlayActivity extends Activity {

	private LinearLayout llMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_third_party_service);
	}

	public void initView(int resId) {
		llMain = new LinearLayout(this);
		setContentView(llMain);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		llMain.addView(inflater.inflate(resId, null));
	}

}
