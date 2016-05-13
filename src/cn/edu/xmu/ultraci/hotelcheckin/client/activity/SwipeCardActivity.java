package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import java.io.Serializable;

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
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.MemberDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.RoomDTO;
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
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action = getIntent().getStringExtra("action");
		extras = getIntent().getBundleExtra("extras");

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

		registerReceiver();
		bindCoreService();
		bindMiscService();
		bindThirdpartyService();
	}

	@Override
	protected void onResume() {
		super.onResume();

		isForeground = true;
		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		isForeground = false;
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
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

	@Override
	public void onNewIntent(Intent intent) {
		// 播放刷卡音效
		getMiscServiceBinder().playEffect(R.raw.ding);
		// 提取卡ID
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String cardid = StringUtil.byte2HexString(tagFromIntent.getId());
		extras.putString("cardid", cardid);
		// 处理卡ID
		switch (action) {
		case Action.CLIENT_LOGIN:
			showProcess();
			getCoreServiceBinder().login(cardid);
			break;
		case Action.CLIENT_LOGOUT:
			showProcess();
			getCoreServiceBinder().logout(cardid);
			break;
		case Action.CLIENT_MEMBER_CHECKIN:
			showProcess();
			getCoreServiceBinder().member(cardid);
			break;
		case Action.CLIENT_EXTENSION:
			showProcess();
			getCoreServiceBinder().room(cardid);
			break;
		case Action.CLIENT_CHECKOUT:
			showProcess();
			getCoreServiceBinder().room(cardid);
			break;
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
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
			Serializable retModel;
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				// 播放语音提示
				switch (action) {
				case Action.CLIENT_LOGIN:
				case Action.CLIENT_LOGOUT:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CARD_STAFF_HINT);
					break;
				case Action.CLIENT_MEMBER_CHECKIN:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CARD_MEMBER_HINT);
					break;
				case Action.CLIENT_EXTENSION:
				case Action.CLIENT_CHECKOUT:
					getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CARD_ROOM_HINT);
					break;
				}
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (!isForeground) {
					dismissDialog();
				}
				if (isChangingUI) {
					// 界面跳转标志只能使用一次，防止点击返回时再次跳转
					isChangingUI = false;
					Intent newIntent;
					switch (action) {
					case Action.CLIENT_LOGIN:
					case Action.CLIENT_LOGOUT:
						// 跳转到声纹验证界面
						newIntent = new Intent(SwipeCardActivity.this, VoiceprintActivity.class);
						newIntent.putExtra("action", action);
						newIntent.putExtra("extras", extras);
						startActivityForResult(newIntent, Code.CHANGE_UI);
						break;
					case Action.CLIENT_MEMBER_CHECKIN:
						// 跳转到选时间界面
						newIntent = new Intent(SwipeCardActivity.this, SelectTimeActivity.class);
						newIntent.putExtra("action", action);
						newIntent.putExtra("extras", extras);
						startActivityForResult(newIntent, Code.CHANGE_UI);
						break;
					case Action.CLIENT_EXTENSION:
						// 跳转到续住或退房界面
						newIntent = new Intent(SwipeCardActivity.this, SelectTimeActivity.class);
						newIntent.putExtra("action", action);
						newIntent.putExtra("extras", extras);
						startActivityForResult(newIntent, Code.CHANGE_UI);
						break;
					case Action.CLIENT_CHECKOUT:
						newIntent = new Intent(SwipeCardActivity.this, RoomInfoActivity.class);
						newIntent.putExtra("action", action);
						newIntent.putExtra("extras", extras);
						startActivityForResult(newIntent, Code.CHANGE_UI);
						break;
					}
				}
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				dismissProcess();
				// 内部错误
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INTERNAL_ERROR);
				showDialog(R.drawable.warn, TTS.INTERNAL_ERROR);
				break;
			case Broadcast.CORE_LOGIN_OK:
			case Broadcast.CORE_LOGOUT_OK:
				dismissProcess();
				isChangingUI = true;
				// 登录、登出成功
				// 讯飞API不允许用户ID数字开头
				extras.putString("uid", "u" + extras.getString("cardid"));
				extras.putString("name", intent.getStringExtra("name"));
				// 合成语音并弹出对话框
				getThirdpartyServiceBinder()
						.synthesicSpeech(String.format(TTS.SWIPE_CARD_STAFF_OK, extras.getString("name")));
				showDialog(R.drawable.plain, String.format(TTS.SWIPE_CARD_STAFF_OK, extras.getString("name")));
				break;
			case Broadcast.CORE_LOGIN_NO_PREMISSION:
			case Broadcast.CORE_LOGOUT_NO_PREMISSION:
				dismissProcess();
				// 登录、登出失败无权限
				getThirdpartyServiceBinder().synthesicSpeech(
						String.format(TTS.SWIPE_CARD_STAFF_NO_PREMISSION, intent.getStringExtra("name")));
				showDialog(R.drawable.info,
						String.format(TTS.SWIPE_CARD_STAFF_NO_PREMISSION, intent.getStringExtra("name")));
				break;
			case Broadcast.CORE_LOGIN_NO_SUCH_CARD:
			case Broadcast.CORE_LOGOUT_NO_SUCH_CARD:
				dismissProcess();
				// 登陆(登出)失败无此卡
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CARD_STAFF_NO_SUCH_CARD);
				showDialog(R.drawable.warn, TTS.SWIPE_CARD_STAFF_NO_SUCH_CARD);
				break;
			case Broadcast.CORE_QUERY_MEMBER_OK:
				dismissProcess();
				isChangingUI = true;
				// 查询会员成功
				retModel = intent.getSerializableExtra("retModel");
				extras.putInt("memberid", ((MemberDTO) retModel).getId());
				extras.putString("name", ((MemberDTO) retModel).getName());
				extras.putString("mobile", ((MemberDTO) retModel).getMobile());
				// 合成语音和弹出对话框
				getThirdpartyServiceBinder()
						.synthesicSpeech(String.format(TTS.SWIPE_CARD_MEMBER_OK, extras.getString("name")));
				showDialog(R.drawable.plain, String.format(TTS.SWIPE_CARD_MEMBER_OK, extras.getString("name")));
				break;
			case Broadcast.CORE_QUERY_MEMBER_NO_SUCH_CARD:
				dismissProcess();
				// 查询会员失败无此卡
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CARD_MEMBER_NO_SUCH_CARD);
				showDialog(R.drawable.warn, TTS.SWIPE_CARD_MEMBER_NO_SUCH_CARD);
				break;
			case Broadcast.CORE_QUERY_ROOM_OK:
				dismissProcess();
				isChangingUI = true;
				// 查询房间成功
				retModel = intent.getSerializableExtra("retModel");
				extras.putInt("roomid", ((RoomDTO) retModel).getId());
				extras.putString("room", ((RoomDTO) retModel).getName());
				extras.putString("type", ((RoomDTO) retModel).getType());
				extras.putDouble("price", ((RoomDTO) retModel).getPrice());
				extras.putString("mobile", ((RoomDTO) retModel).getMobile());
				extras.putString("checkin", ((RoomDTO) retModel).getCheckin());
				extras.putString("checkout", ((RoomDTO) retModel).getCheckout());
				// 弹出对话框
				getThirdpartyServiceBinder()
						.synthesicSpeech(String.format(TTS.SWIPE_CARD_ROOM_OK, extras.getString("room")));
				showDialog(R.drawable.plain, String.format(TTS.SWIPE_CARD_ROOM_OK, extras.getString("room")));
				break;
			case Broadcast.CORE_QUERY_ROOM_NO_CHECKIN:
				dismissProcess();
				// 查询房间失败未入住
				getThirdpartyServiceBinder().synthesicSpeech(String.format(TTS.SWIPE_CARD_ROOM_NO_CHECKIN,
						((RoomDTO) intent.getSerializableExtra("retModel")).getName()));
				showDialog(R.drawable.info, String.format(TTS.SWIPE_CARD_ROOM_NO_CHECKIN,
						((RoomDTO) intent.getSerializableExtra("retModel")).getName()));
				break;
			case Broadcast.CORE_QUERY_ROOM_NO_SUCH_CARD:
				dismissProcess();
				// 查询房间失败无此卡
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SWIPE_CARD_ROOM_NO_SUCH_CARD);
				showDialog(R.drawable.warn, TTS.SWIPE_CARD_ROOM_NO_SUCH_CARD);
				break;
			}
		}
	}
}
