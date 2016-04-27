package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.MemberDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.StringUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 刷卡界面
 */
public class SwipeCardActivity extends BaseActivity {
	private static final String TAG = SwipeCardActivity.class.getSimpleName();

	private SwipeCardReceiver receiver;

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;

	private String action;
	private String cardid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action = getIntent().getStringExtra("action");

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) };
		mTechLists = new String[][] { new String[] { NfcA.class.getName() } };

		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver();
		bindCoreService();
		bindMiscService();
		bindThirdpartyService();

		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		SystemUtil.unregisterLocalBroadcast(this, receiver);
		unbindService();

		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onNewIntent(Intent intent) {
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		cardid = StringUtil.byte2HexString(tagFromIntent.getId());
		// 播放刷卡音效
		getMiscServiceBinder().playEffect(R.raw.ding);
		// 处理读取到的卡ID
		switch (action) {
		case Action.CLIENT_LOGIN:
			getCoreServiceBinder().login(cardid);
			break;
		case Action.CLIENT_LOGOUT:
			getCoreServiceBinder().logout(cardid);
			break;
		case Action.CLIENT_MEMBER_CHECKIN:
			getCoreServiceBinder().member(cardid);
			break;
		case Action.CLIENT_EXTENSION:
			getCoreServiceBinder().room(cardid);
			break;
		case Action.CLIENT_CHECKOUT:
			getCoreServiceBinder().room(cardid);
			break;
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.CORE_LOGIN_OK);
		filter.addAction(Broadcast.CORE_LOGOUT_OK);
		filter.addAction(Broadcast.CORE_LOGIN_NO_PREMISSION);
		filter.addAction(Broadcast.CORE_LOGOUT_NO_PREMISSION);
		filter.addAction(Broadcast.CORE_LOGIN_NO_SUCH_CARD);
		filter.addAction(Broadcast.CORE_LOGOUT_NO_SUCH_CARD);
		filter.addAction(Broadcast.CORE_QUERY_MEMBER_OK);
		filter.addAction(Broadcast.CORE_QUERY_MEMBER_NO_SUCH_CARD);
		filter.addAction(Broadcast.CORE_QUERY_ROOM_OK);
		filter.addAction(Broadcast.CORE_QUERY_ROOM_NO_CHECKIN);
		filter.addAction(Broadcast.CORE_QUERY_ROOM_NO_SUCH_CARD);
		receiver = new SwipeCardReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(true, getTitle().toString(), true, 30, R.layout.activity_swipe_card, false);
	}

	class SwipeCardReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent newIntent;
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				// 播放语音提示
				switch (action) {
				case Action.CLIENT_LOGIN:
				case Action.CLIENT_LOGOUT:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_LOGIN_OUT);
					break;
				case Action.CLIENT_MEMBER_CHECKIN:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_QUERY_MEMBER);
					break;
				case Action.CLIENT_EXTENSION:
				case Action.CLIENT_CHECKOUT:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_QUERY_ROOM);
					break;
				}
				break;
			case Broadcast.CORE_LOGIN_OK:
				// 登陆成功
				newIntent = new Intent(SwipeCardActivity.this, VoiceprintActivity.class);
				newIntent.putExtra("action", action);
				// 讯飞API不允许用户ID数字开头
				newIntent.putExtra("uid", "u" + cardid);
				startActivity(newIntent);
				finish();
				break;
			case Broadcast.CORE_LOGOUT_OK:
				// 登出成功
				newIntent = new Intent(SwipeCardActivity.this, VoiceprintActivity.class);
				newIntent.putExtra("action", action);
				newIntent.putExtra("uid", "u" + cardid);
				startActivity(newIntent);
				finish();
				break;
			case Broadcast.CORE_LOGIN_NO_PREMISSION:
				// 登录失败无权限
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_LOGIN_NO_PREMISSION);
				break;
			case Broadcast.CORE_LOGOUT_NO_PREMISSION:
				// 登出失败无权限
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_LOGOUT_NO_PREMISSION);
				break;
			case Broadcast.CORE_LOGIN_NO_SUCH_CARD:
			case Broadcast.CORE_LOGOUT_NO_SUCH_CARD:
				// 登陆(登出)失败无此卡
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_LOGIN_OUT_NO_SUCH_CARD);
				break;
			case Broadcast.CORE_QUERY_MEMBER_OK:
				// 查询会员成功
				newIntent = new Intent(SwipeCardActivity.this, SelectTimeActivity.class);
				newIntent.putExtra("action", action);
				newIntent.putExtra("retModel", intent.getSerializableExtra("retModel"));
				// TEST
				MemberDTO retModel = (MemberDTO) intent.getSerializableExtra("retModel");
				SystemUtil.setPreferences(SwipeCardActivity.this, "mobile", retModel.getMobile());
				SystemUtil.setPreferences(SwipeCardActivity.this, "customer", retModel.getId() + "");
				// ENDTEST
				startActivity(newIntent);
				finish();
				break;
			case Broadcast.CORE_QUERY_MEMBER_NO_SUCH_CARD:
				// 查询会员失败无此卡
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_QUERY_MEMBER_NO_SUCH_CARD);
				break;
			case Broadcast.CORE_QUERY_ROOM_OK:
				// 查询房间成功
				newIntent = new Intent(SwipeCardActivity.this, RoomInfoActivity.class);
				newIntent.putExtra("action", action);
				newIntent.putExtra("cardid", cardid);
				newIntent.putExtra("retModel", intent.getSerializableExtra("retModel"));
				startActivity(newIntent);
				finish();
				break;
			case Broadcast.CORE_QUERY_ROOM_NO_CHECKIN:
				// 查询房间失败未入住
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_QUERY_ROOM_NO_CHECKIN);
				break;
			case Broadcast.CORE_QUERY_ROOM_NO_SUCH_CARD:
				// 查询房间失败无此卡
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CRAD_QUERY_ROOM_NO_SUCH_CARD);
				break;
			}
		}
	}
}
