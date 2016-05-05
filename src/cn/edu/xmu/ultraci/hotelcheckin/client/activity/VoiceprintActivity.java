package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 声纹验证界面
 */
public class VoiceprintActivity extends BaseActivity {
	private static final String TAG = VoiceprintActivity.class.getSimpleName();

	private VoiceprintReceiver receiver;

	private String action;
	private Bundle extras;

	private ImageView[] ivPwd = new ImageView[8];
	private ProgressBar pbVolume;

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
		bindMiscService();
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

	/**
	 * 初始化广播
	 */
	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.IFLYTEK_RECORD_START);
		filter.addAction(Broadcast.IFLYTEK_RECORD_END);
		filter.addAction(Broadcast.IFLYTEK_RECORD_VOLUME_CHANGE);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_OK);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_FAIL_VOICE);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_FAIL_TEXT);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_FAIL_OTHER);
		receiver = new VoiceprintReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	/**
	 * 初始化布局
	 */
	public void initView() {
		setContent(true, getTitle().toString(), true, 30, R.layout.activity_voiceprint, false);

		ivPwd[0] = (ImageView) findViewById(R.id.iv_pwd1);
		ivPwd[1] = (ImageView) findViewById(R.id.iv_pwd2);
		ivPwd[2] = (ImageView) findViewById(R.id.iv_pwd3);
		ivPwd[3] = (ImageView) findViewById(R.id.iv_pwd4);
		ivPwd[4] = (ImageView) findViewById(R.id.iv_pwd5);
		ivPwd[5] = (ImageView) findViewById(R.id.iv_pwd6);
		ivPwd[6] = (ImageView) findViewById(R.id.iv_pwd7);
		ivPwd[7] = (ImageView) findViewById(R.id.iv_pwd8);
		pbVolume = (ProgressBar) findViewById(R.id.pb_volume);
	}

	/**
	 * 获取并显示声纹密码
	 */
	public void showPwd() {
		String pwd = getThirdpartyServiceBinder().getVoiceprintPassword();
		extras.putString("pwd", pwd);
		for (int i = 0; i < 8; i++) {
			switch (pwd.charAt(i)) {
			case '0':
				ivPwd[i].setImageResource(R.drawable.num0);
				break;
			case '1':
				ivPwd[i].setImageResource(R.drawable.num1);
				break;
			case '2':
				ivPwd[i].setImageResource(R.drawable.num2);
				break;
			case '3':
				ivPwd[i].setImageResource(R.drawable.num3);
				break;
			case '4':
				ivPwd[i].setImageResource(R.drawable.num4);
				break;
			case '5':
				ivPwd[i].setImageResource(R.drawable.num5);
				break;
			case '6':
				ivPwd[i].setImageResource(R.drawable.num6);
				break;
			case '7':
				ivPwd[i].setImageResource(R.drawable.num7);
				break;
			case '8':
				ivPwd[i].setImageResource(R.drawable.num8);
				break;
			case '9':
				ivPwd[i].setImageResource(R.drawable.num9);
				break;
			}
		}
	}

	class VoiceprintReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				showPwd();
				getThirdpartyServiceBinder()
						.synthesicSpeech(String.format(TTS.VOICEPRINT_HINT, extras.getString("name")));
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				// 待语音播放完毕后才开始验证过程
				getMiscServiceBinder().playEffect(R.raw.beep);
				getThirdpartyServiceBinder().verifyVoiceprint(extras.getString("uid"), extras.getString("pwd"));
				break;
			case Broadcast.IFLYTEK_RECORD_START:
				pbVolume.setVisibility(View.VISIBLE);
				break;
			case Broadcast.IFLYTEK_RECORD_END:
				pbVolume.setVisibility(View.INVISIBLE);
				break;
			case Broadcast.IFLYTEK_RECORD_VOLUME_CHANGE:
				pbVolume.setProgress(intent.getIntExtra("volume", 0));
				break;
			case Broadcast.IFLYTEK_VERIFY_OK:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.VOICEPRINT_OK);
				// 登录验证成功进主界面，登出验证成功设置标记关闭主界面
				switch (action) {
				case Action.CLIENT_LOGIN:
					startActivity(new Intent(VoiceprintActivity.this, MainActivity.class));
					finish();
					break;
				case Action.CLIENT_LOGOUT:
					VoiceprintActivity.this.setResult(RESULT_OK);
					finish();
					break;
				}
				break;
			case Broadcast.IFLYTEK_VERIFY_FAIL_VOICE:
				showPwd();
				getThirdpartyServiceBinder().synthesicSpeech(TTS.VOICEPRINT_FAIL_VOICE);
				break;
			case Broadcast.IFLYTEK_VERIFY_FAIL_TEXT:
				showPwd();
				getThirdpartyServiceBinder().synthesicSpeech(TTS.VOICEPRINT_FAIL_TEXT);
				break;
			case Broadcast.IFLYTEK_VERIFY_FAIL_OTHER:
				showPwd();
				getThirdpartyServiceBinder().synthesicSpeech(TTS.VOICEPRINT_FAIL_OTHER);
				break;
			}
		}
	}
}
