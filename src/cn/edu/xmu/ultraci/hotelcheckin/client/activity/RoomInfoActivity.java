package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 房间信息界面
 */
public class RoomInfoActivity extends BaseActivity {
	private static final String TAG = RoomInfoActivity.class.getSimpleName();

	private RoomInfoReceiver receiver;
	
	private String action;

	private TextView activity_roominfo_tv_roomname;
	private TextView activity_roominfo_tv_roomfloor;
	private TextView activity_roominfo_tv_roomtype;
	private TextView activity_roominfo_tv_roomprice;
	private TextView activity_roominfo_tv_checkintime;
	private TextView activity_roominfo_tv_checkouttime;
	private TextView activity_roominfo_tv_userphone;
	private Button activity_roominfo_btn_repeatorder;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver();
		bindCoreService();
		bindThirdpartyService();
	}

	@Override
	protected void onPause() {
		super.onPause();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_CHECKOUT_OK);
		filter.addAction(Broadcast.CORE_CHECKOUT_NEED_PAY);
		filter.addAction(Broadcast.CORE_CHECKOUT_OK);
		filter.addAction(Broadcast.CORE_CHECKOUT_OK);
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		receiver = new RoomInfoReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {

	}

	public void updateView() {

	}

	class RoomInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	}
}
