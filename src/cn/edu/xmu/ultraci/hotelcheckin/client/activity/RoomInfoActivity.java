package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;

/**
 * 房间信息界面
 */
public class RoomInfoActivity extends BaseActivity {
	private static final String TAG = RoomInfoActivity.class.getSimpleName();

	private RoomInfoReceiver receiver;

	private String action;
	private Bundle extras;

	private TextView tvRoom;
	private TextView tvType;
	private TextView tvPrice;
	private TextView tvMobile;
	private TextView tvCheckin;
	private TextView tvCheckout;
	private Button btnConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action = getIntent().getStringExtra("action");
		extras = getIntent().getBundleExtra("extras");

		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver();
		bindCoreService();
		bindThirdpartyService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Code.CHANGE_UI && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_CHECKOUT_OK);
		filter.addAction(Broadcast.CORE_CHECKOUT_NEED_PAY);
		receiver = new RoomInfoReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(true, getTitle().toString(), true, 30, R.layout.activity_room_info, false);

		tvRoom = (TextView) findViewById(R.id.tv_room);
		tvType = (TextView) findViewById(R.id.tv_type);
		tvPrice = (TextView) findViewById(R.id.tv_price);
		tvMobile = (TextView) findViewById(R.id.tv_mobile);
		tvCheckin = (TextView) findViewById(R.id.tv_checkin);
		tvCheckout = (TextView) findViewById(R.id.tv_checkout);
		btnConfirm = (Button) findViewById(R.id.btn_confirm);

		tvRoom.setText(extras.getString("room"));
		tvType.setText(extras.getString("type"));
		tvPrice.setText(extras.getString("price"));
		tvMobile.setText(extras.getString("mobile"));
		tvCheckin.setText(extras.getString("checkin"));
		tvCheckout.setText(extras.getString("checkout"));

		switch (action) {
		case Action.CLIENT_MEMBER_CHECKIN:
		case Action.CLIENT_GUEST_CHECKIN:
			btnConfirm.setText(R.string.btn_checkin);
			break;
		case Action.CLIENT_EXTENSION:
			btnConfirm.setText(R.string.btn_extension);
			break;
		case Action.CLIENT_CHECKOUT:
			btnConfirm.setText(R.string.btn_checkout);
			break;
		}
	}

	public void onConfirm(View v) {
		switch (action) {
		case Action.CLIENT_MEMBER_CHECKIN:
		case Action.CLIENT_GUEST_CHECKIN:
		case Action.CLIENT_EXTENSION:
			// 跳转到支付界面
			Intent intent = new Intent(this, PayActivity.class);
			intent.putExtra("action", action);
			intent.putExtra("extras", extras);
			startActivityForResult(intent, Code.CHANGE_UI);
			break;
		case Action.CLIENT_CHECKOUT:
			// 先判断是否可以退房
			if (TimeUtil.timeIntervalLimited(extras.getString("checkout"), 12 * 60 * 60)) {
				getThirdpartyServiceBinder().synthesicSpeech(TTS.ROOM_INFO_NEED_PAY);
				showDialog(R.drawable.warn, TTS.ROOM_INFO_NEED_PAY);
			} else {
				// 提交退房信息
				showProcess();
				getCoreServiceBinder().checkout(extras.getString("cardid"));
			}
			break;
		}
	}

	class RoomInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.ROOM_INFO_HINT);
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (!isForeground) {
					dismissDialog();
				}
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				dismissProcess();
				// 内部错误
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INTERNAL_ERROR);
				showDialog(R.drawable.warn, TTS.INTERNAL_ERROR);
				break;
			case Broadcast.CORE_CHECKOUT_OK:
				dismissProcess();
				// 退房成功
				Intent newIntent = new Intent(RoomInfoActivity.this, ResultActivity.class);
				newIntent.putExtra("action", action);
				newIntent.putExtra("extras", extras);
				startActivityForResult(newIntent, Code.CHANGE_UI);
				break;
			}
		}
	}
}
