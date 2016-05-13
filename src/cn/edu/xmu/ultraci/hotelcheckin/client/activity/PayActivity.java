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
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_EXTENSION_OK);
		filter.addAction(Broadcast.CORE_CHECKIN_OK);
		filter.addAction(Broadcast.MISC_PRINTER_OK);
		filter.addAction(Broadcast.MISC_PRINTER_FAIL);
		receiver = new PayReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	private void initView() {
		setContent(true, getTitle().toString(), true, 90, R.layout.activity_pay, false);

		tvDays = (TextView) findViewById(R.id.tv_days);
		tvTotal = (TextView) findViewById(R.id.tv_total);

		long date1 = TimeUtil.parseDateTime(extras.getString("checkin"), "yyyy-MM-dd");
		long date2 = TimeUtil.parseDateTime(extras.getString("checkout"), "yyyy-MM-dd");
		int dayInvl = TimeUtil.dateInterval(date1, date2);
		tvDays.setText(String.valueOf(dayInvl));
		tvTotal.setText(String.valueOf((extras.getDouble("price") * dayInvl)));
	}

	public void onPay(View v) {
		switch (action) {
		case Action.CLIENT_MEMBER_CHECKIN:
		case Action.CLIENT_GUEST_CHECKIN:
			showProcess();
			getCoreServiceBinder().checkin(extras.getString("customer"), String.valueOf(extras.getInt("roomid")),
					extras.getString("checkout"));
			break;
		case Action.CLIENT_EXTENSION:
			showProcess();
			getCoreServiceBinder().extension(String.valueOf(extras.getInt("roomid")), extras.getString("checkout"));
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
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (!isForeground) {
					dismissDialog();
				}
				if (isChangingUI) {
					isChangingUI = false;
					Intent newIntent;
					newIntent = new Intent(PayActivity.this, ResultActivity.class);
					newIntent.putExtra("action", action);
					newIntent.putExtra("extras", extras);
					startActivityForResult(newIntent, Code.CHANGE_UI);
				}
				break;
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
				dismissProcess();
				// 内部错误
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INTERNAL_ERROR);
				showDialog(R.drawable.warn, TTS.INTERNAL_ERROR);
				break;
			case Broadcast.CORE_CHECKIN_OK:
			case Broadcast.CORE_EXTENSION_OK:
				// 打印小票
				getMiscServiceBinder().printTicket(extras);
				break;
			case Broadcast.MISC_PRINTER_FAIL:
				dismissProcess();
				isChangingUI = true;
				// 小票未能打印
				getThirdpartyServiceBinder().synthesicSpeech(TTS.PAY_PRINT_FAIL);
				showDialog(R.drawable.warn, TTS.PAY_PRINT_FAIL);
				break;
			case Broadcast.MISC_PRINTER_OK:
				dismissProcess();
				isChangingUI = true;
				// 小票打印成功
				getThirdpartyServiceBinder().synthesicSpeech(TTS.PAY_PRINT_OK);
				showDialog(R.drawable.plain, TTS.PAY_PRINT_OK);
				break;
			}
		}
	}
}
