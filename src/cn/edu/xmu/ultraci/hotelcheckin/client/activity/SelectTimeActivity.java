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
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;

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

		SystemUtil.unregisterLocalBroadcast(this, receiver);
		unbindService();
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
		receiver = new SelectTimeReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(true, getTitle().toString(), true, 30, R.layout.activity_select_time, false);
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
		switch (action) {
		case Action.CLIENT_MEMBER_CHECKIN:
		case Action.CLIENT_GUEST_CHECKIN:
			extras.putString("checkin", selectedDays.getFirst().toString());
			extras.putString("checkout", selectedDays.getLast().toString());
			break;
		case Action.CLIENT_EXTENSION:
			if (TimeUtil.dateEarlyThan(selectedDays.getLast().toString(), extras.getString("checkout"))) {
				// 办理续住时需判断所选时间不早于原定的预离时间
				getThirdpartyServiceBinder()
						.synthesicSpeech(String.format(TTS.SELECT_TIME_EARLY, extras.getString("checkout")));
				showDialog(R.drawable.warn, String.format(TTS.SELECT_TIME_EARLY, extras.getString("checkout")));
				return;
			} else {
				// 续住时新的入住时间就是原先的预离时间
				extras.putString("checkin", extras.getString("checkout"));
				extras.putString("checkout", selectedDays.getLast().toString());
			}
			break;
		}
		// 提示用户所选择的日期
		isChangingUI = true;
		getThirdpartyServiceBinder().synthesicSpeech(String.format(TTS.SELECT_TIME_OK, extras.getString("checkout")));
		showDialog(R.drawable.plain, String.format(TTS.SELECT_TIME_OK, extras.getString("checkout")));
	}

	class SelectTimeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SELECT_TIME_HINT);
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (!isForeground) {
					dismissDialog();
				}
				if (isChangingUI) {
					isChangingUI = false;
					// 跳转到选房间界面或确认房间信息界面
					Intent newIntent = null;
					switch (action) {
					case Action.CLIENT_MEMBER_CHECKIN:
					case Action.CLIENT_GUEST_CHECKIN:
						newIntent = new Intent(SelectTimeActivity.this, SelectRoomActivity.class);
						break;
					case Action.CLIENT_EXTENSION:
						newIntent = new Intent(SelectTimeActivity.this, RoomInfoActivity.class);
						break;
					}
					newIntent.putExtra("action", action);
					newIntent.putExtra("extras", extras);
					startActivityForResult(newIntent, Code.CHANGE_UI);
				}
				break;
			}
		}
	}

}
