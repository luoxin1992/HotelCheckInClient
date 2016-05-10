package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;

/**
 * 支付界面
 */
public class PayActivity extends BaseActivity {
	private static final String TAG = PayActivity.class.getSimpleName();

	private PayReceiver receiver;

	private String action;
	private Bundle extras;

	private TextView tvDays;
	private TextView tvTotal;

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
		bindMiscService();

	}

	@Override
	protected void onStop() {
		super.onStop();

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
		filter.addAction(Broadcast.CORE_SERIVCE_BOUND);
		filter.addAction(Broadcast.CORE_QUERY_STATUS_OK);
		filter.addAction(Broadcast.CORE_EXTENSION_OK);
		filter.addAction(Broadcast.CORE_CHECKIN_OK);
		receiver = new PayReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	private void initView() {
		setContent(true, getTitle().toString(), true, 90, R.layout.activity_pay, false);

		tvDays = (TextView) findViewById(R.id.tv_days);
		tvTotal = (TextView) findViewById(R.id.tv_total);

		long date1 = TimeUtil.parseDateTime(extras.getString("checkin"));
		long date2 = TimeUtil.parseDateTime(extras.getString("checkout"));
		int dayInvl = TimeUtil.dateInterval(date1, date2);
		tvDays.setText(String.valueOf(dayInvl));
		tvTotal.setText(String.valueOf((extras.getDouble("price") * dayInvl)));
	}

	public void onPay(View v) {
		switch (action) {
		case Action.CLIENT_MEMBER_CHECKIN:
		case Action.CLIENT_GUEST_CHECKIN:
			showProcess();
			getCoreServiceBinder().checkin(extras.getString("customer"), extras.getString("room"),
					extras.getString("checkout"));
			break;
		case Action.CLIENT_EXTENSION:
			showProcess();
			getCoreServiceBinder().extension(extras.getString("room"), extras.getString("checkout"));
			break;
		}
	}

	class PayReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.PAY_HINT);
				break;
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
				break;
			case Broadcast.CORE_CHECKIN_OK:
			case Broadcast.CORE_EXTENSION_OK:
				dismissProcess();
				// 打印小票
				getMiscServiceBinder().printTicket(extras);
				break;
			}
		}
	}
}
