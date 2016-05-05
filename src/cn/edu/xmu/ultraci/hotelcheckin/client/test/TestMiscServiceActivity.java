package cn.edu.xmu.ultraci.hotelcheckin.client.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Bluetooth;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.MiscService;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.MiscService.MiscServiceBinder;

public class TestMiscServiceActivity extends Activity {

	private ServiceConnection conn;
	private MiscServiceBinder binder;

	private RadioGroup rgSize;
	private RadioGroup rgType;
	private RadioGroup rgAlign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_misc_service);

		rgSize = (RadioGroup) findViewById(R.id.radioGroup1);
		rgType = (RadioGroup) findViewById(R.id.radioGroup2);
		rgAlign = (RadioGroup) findViewById(R.id.radioGroup3);

		conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				System.out.println(TestMiscServiceActivity.class.getSimpleName() + "服务断开");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				System.out.println(TestMiscServiceActivity.class.getSimpleName() + "服务绑定");
				binder = (MiscServiceBinder) service;
			}
		};
		bindService(new Intent(this, MiscService.class), conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
		System.out.println(TestMiscServiceActivity.class.getSimpleName() + "服务解绑");
	}

	public void makeToast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

	public void print(View v) {
		int size = 0, type = 0, align = 0;
		switch (rgSize.getCheckedRadioButtonId()) {
		case R.id.radioButton1:
			size = Bluetooth.FONT_1;
			makeToast("字号1");
			break;
		case R.id.radioButton2:
			size = Bluetooth.FONT_2;
			makeToast("字号2");
			break;
		case R.id.radioButton3:
			size = Bluetooth.FONT_3;
			makeToast("字号3");
			break;
		case R.id.radioButton4:
			size = Bluetooth.FONT_4;
			makeToast("字号4");
			break;
		}
		switch (rgType.getCheckedRadioButtonId()) {
		case R.id.radioButton5:
			type = Bluetooth.FONT_REGULAR;
			makeToast("正常");
			break;
		case R.id.radioButton6:
			type = Bluetooth.FONT_BOLD;
			makeToast("加粗");
			break;
		}
		switch (rgAlign.getCheckedRadioButtonId()) {
		case R.id.radioButton7:
			align = Bluetooth.ALIGN_LEFT;
			makeToast("左对齐");
			break;
		case R.id.radioButton8:
			align = Bluetooth.ALIGN_CENTER;
			makeToast("居中");
			break;
		case R.id.radioButton9:
			align = Bluetooth.ALIGN_RIGHT;
			makeToast("右对齐");
			break;
		}
		// binder.setPrintFormat(size, type, align);
		// binder.printTest("您好世界！");
		// binder.setPrintFormat(0, 0, 0);
		// binder.printTest("\n");
	}
}
