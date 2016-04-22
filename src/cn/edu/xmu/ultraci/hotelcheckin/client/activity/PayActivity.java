package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 支付界面
 */
public class PayActivity extends BaseActivity {
	private static final String TAG = PayActivity.class.getSimpleName();

	private PayReceiver receiver;

	private String action;

	private TextView payactivity_tv_roomnum;
	private TextView payactivity_tv_usertype;
	private TextView payactivity_tv_roomtype;
	private TextView payactivity_tv_entertime;
	private TextView payactivity_tv_exittime;
	private TextView payactivity_tv_phone;
	private TextView payactivity_tv_money;
	private Button payactivity_btn_confirm;

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

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_CHECKIN_OK);
		receiver = new PayReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	private void initView() {
		initView(true, getTitle().toString(), true, 90, R.layout.activity_pay, false);

		payactivity_tv_roomnum = (TextView) findViewById(R.id.payactivity_tv_roomnum);// 房间ID
		payactivity_tv_usertype = (TextView) findViewById(R.id.payactivity_tv_usertype);// 用户类型
		payactivity_tv_roomtype = (TextView) findViewById(R.id.payactivity_tv_roomtype);// 房间类型
		payactivity_tv_entertime = (TextView) findViewById(R.id.payactivity_tv_entertime);// 入住时间
		payactivity_tv_exittime = (TextView) findViewById(R.id.payactivity_tv_exittime);// 退房时间
		payactivity_tv_money = (TextView) findViewById(R.id.payactivity_tv_money);
		payactivity_tv_phone = (TextView) findViewById(R.id.payactivity_tv_phone);
		payactivity_btn_confirm = (Button) findViewById(R.id.payactivity_btn_confirm);

		// 根据不同的数据来源更新界面上的值
		if (action.equals("member_checkin")) {
			String selected_room_name = getIntent().getStringExtra("selected_room_name");
			String enterTime = getIntent().getStringExtra("enterTime");
			String exitTime = getIntent().getStringExtra("exitTime");
			String floor_name = getIntent().getStringExtra("selected_floor_name");
			Double room_price = getIntent().getDoubleExtra("room_price", 0);
			String room_type_name = getIntent().getStringExtra("room_type");
			Log.i("PayActivity", "room_price==" + room_price);

			Date time1 = null;
			Date time2 = null;
			try {
				time1 = new SimpleDateFormat("yyyy-MM-dd").parse(enterTime);
				time2 = new SimpleDateFormat("yyyy-MM-dd").parse(exitTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long day = (time2.getTime() - time1.getTime()) / (24 * 60 * 60 * 1000);

			payactivity_tv_roomnum.setText(selected_room_name + "");
			payactivity_tv_usertype.setText("会员");
			// TODO:缺个手机号
			payactivity_tv_roomtype.setText(room_type_name);
			payactivity_tv_entertime.setText(enterTime);
			payactivity_tv_exittime.setText(exitTime);
			payactivity_tv_money.setText("￥" + room_price * day + "元");

		} else if (action.equals("tourist_checkin")) {
			String selected_room_name = getIntent().getStringExtra("selected_room_name");
			String enterTime = getIntent().getStringExtra("enterTime");
			String exitTime = getIntent().getStringExtra("exitTime");
			String selected_floor_name = getIntent().getStringExtra("selected_floor_name");
			Double room_price = getIntent().getDoubleExtra("room_price", 0);
			String room_type_name = getIntent().getStringExtra("room_type");
			String user_phone = getIntent().getStringExtra("user_phone");
			Date time1 = null;
			Date time2 = null;
			try {
				time1 = new SimpleDateFormat("yyyy-MM-dd").parse(enterTime);
				time2 = new SimpleDateFormat("yyyy-MM-dd").parse(exitTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long day = (time1.getTime() - time2.getTime()) / (24 * 60 * 60 * 1000);

			payactivity_tv_roomnum.setText(selected_room_name + "");
			payactivity_tv_usertype.setText("非会员");
			payactivity_tv_roomtype.setText(room_type_name);
			payactivity_tv_entertime.setText(enterTime);
			payactivity_tv_exittime.setText(exitTime);
			payactivity_tv_money.setText("￥" + -room_price * day + "元");
			payactivity_tv_phone.setText(user_phone);

		} else if (action.equals("repeatorder")) {
			String enterTime = getIntent().getStringExtra("enterTime");
			String exitTime = getIntent().getStringExtra("exitTime");
			String name = getIntent().getStringExtra("name");
			String mobile = getIntent().getStringExtra("mobile");
			String typefromroom = getIntent().getStringExtra("typefromroom");
			Double price = getIntent().getDoubleExtra("price", 0);

			Date time1 = null;
			Date time2 = null;
			try {
				time1 = new SimpleDateFormat("yyyy-MM-dd").parse(enterTime);
				time2 = new SimpleDateFormat("yyyy-MM-dd").parse(exitTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long day = (time1.getTime() - time2.getTime()) / (24 * 60 * 60 * 1000);

			payactivity_tv_entertime.setText(enterTime);
			payactivity_tv_exittime.setText(exitTime);
			payactivity_tv_roomnum.setText(name);
			payactivity_tv_phone.setText(mobile);
			payactivity_tv_roomtype.setText(typefromroom);
			payactivity_tv_money.setText("￥" + -price * day + "元");
		}
	}

	public void updateView() {

	}

	class PayReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				break;
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
				break;
			case Broadcast.CORE_CHECKIN_OK:
				break;
			}
		}
	}
}
