package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import com.andexert.calendarlistview.library.DatePickerController;
import com.andexert.calendarlistview.library.DayPickerView;
import com.andexert.calendarlistview.library.SimpleMonthAdapter.CalendarDay;
import com.andexert.calendarlistview.library.SimpleMonthAdapter.SelectedDays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 选时间界面
 */
public class SelectTimeActivity extends BaseActivity implements DatePickerController {
	private static final String TAG = SelectTimeActivity.class.getSimpleName();

	private SelectTimeReceiver receiver;

	private String action;
	private Bundle extras;

	private DayPickerView dayPickerView;

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
		bindThirdpartyService();
	}

	@Override
	protected void onStop() {
		super.onStop();

		SystemUtil.unregisterLocalBroadcast(this, receiver);
		unbindService();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		receiver = new SelectTimeReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(true, getTitle().toString(), true, 60, R.layout.activity_select_time, false);
		dayPickerView = (DayPickerView) findViewById(R.id.day_picker_view);
		dayPickerView.setController(this);
	}

	@Override
	public int getMaxYear() {
		// 这个返回值已经没什么用了
		return 0;
	}

	@Override
	public void onDayOfMonthSelected(int year, int month, int day) {
	}

	@Override
	public void onDateRangeSelected(SelectedDays<CalendarDay> selectedDays) {
		extras.putString("checkin", selectedDays.getFirst().toString());
		extras.putString("checkout", selectedDays.getLast().toString());

		Intent newIntent = null;
		switch (action) {
		case Action.CLIENT_MEMBER_CHECKIN:
		case Action.CLIENT_GUEST_CHECKIN:
			newIntent = new Intent(this, SelectRoomActivity.class);
			break;
		case Action.CLIENT_EXTENSION:
			newIntent = new Intent(SelectTimeActivity.this, PayActivity.class);
			break;
		}
		newIntent.putExtra("action", action);
		newIntent.putExtra("extras", extras);
		startActivity(newIntent);
		finish();
	}

	class SelectTimeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SELECT_TIME_HINT);
				break;
			}
		}
	}

}
