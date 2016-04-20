package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

/**
 * 基础布局<br>
 * <ul>
 * 顶部:标题&时钟<br>
 * 中间:动态填充其他布局<br>
 * 底部:公告<br>
 * </ul>
 * 
 * @author LuoXin
 *
 */
public class BaseActivity extends Activity {

	private LinearLayout llMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
	}

	/**
	 * 初始化UI控件
	 */
	public void initView(int resId) {
		llMain = (LinearLayout) findViewById(R.id.ll_main);
		// 用布局填充器动态设置布局
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(resId, null);
		llMain.addView(view);
	}

	/**
	 * 配置界面超时
	 * 
	 * @param seconds
	 *            超时时间
	 */
	public void setTimeout(int seconds) {
	}

}
