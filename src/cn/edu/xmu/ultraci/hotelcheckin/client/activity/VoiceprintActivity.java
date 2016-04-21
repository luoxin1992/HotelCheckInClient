package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.MiscService;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.MiscService.MiscServiceBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.ThirdpartyService;
import cn.edu.xmu.ultraci.hotelcheckin.client.service.ThirdpartyService.ThirdpartyServiceBinder;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 声纹验证界面
 */
public class VoiceprintActivity extends BaseActivity {

	private ImageView[] ivPwd = new ImageView[8];
	private ProgressBar pbVolume;

	private ServiceConnection miscConn;
	private ServiceConnection thirdPartyConn;
	private MiscServiceBinder miscBinder;
	private ThirdpartyServiceBinder thirdPartyBinder;
	private VoiceprintActivityReceiver receiver;

	private String from;
	private String uid;
	private int retry = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initReceiver();
		bindService();

		// 取来源Activity和要验证的用户ID
		from = getIntent().getStringExtra("from");
		from = "init";
		uid = getIntent().getStringExtra("uid");
		uid = "hello2";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(miscConn);
		unbindService(thirdPartyConn);
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	/**
	 * 绑定到所需服务
	 */
	public void bindService() {
		miscConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				miscBinder = (MiscServiceBinder) service;
			}
		};
		thirdPartyConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				thirdPartyBinder = (ThirdpartyServiceBinder) service;
				// 服务绑定成功后即开始验证过程
				thirdPartyBinder.synthesicSpeech(TTS.VOICEPRINT_VERIFY);
			}
		};
		bindService(new Intent(this, MiscService.class), miscConn, BIND_AUTO_CREATE);
		bindService(new Intent(this, ThirdpartyService.class), thirdPartyConn, BIND_AUTO_CREATE);
	}

	/**
	 * 初始化布局
	 */
	public void initView() {
//		initView(R.layout.activity_voiceprint);

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
	 * 初始化广播
	 */
	public void initReceiver() {
		receiver = new VoiceprintActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.IFLYTEK_RECORD_START);
		filter.addAction(Broadcast.IFLYTEK_RECORD_END);
		filter.addAction(Broadcast.IFLYTEK_RECORD_VOLUME_CHANGE);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_OK);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_FAIL_VOICE);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_FAIL_TEXT);
		filter.addAction(Broadcast.IFLYTEK_VERIFY_FAIL_OTHER);
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	/**
	 * 进行声纹验证
	 */
	public void startVerify() {
		miscBinder.playEffect(R.raw.beep);
		String pwd = thirdPartyBinder.getVoiceprintPassword();
		showPwd(pwd);
		thirdPartyBinder.verifyVoiceprint(uid, pwd);
	}

	/**
	 * 显示声纹密码
	 * 
	 * @param pwd
	 *            密码
	 */
	public void showPwd(String pwd) {
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

	class VoiceprintActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				// 待语音播放完毕后才开始验证过程
				// 最大验证次数限制为3次
				if (retry < 3) {
					startVerify();
				}
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
				thirdPartyBinder.synthesicSpeech(TTS.VOICEPRINT_VERIFY_OK);
				// 验证成功后根据来源Activity确定下一个Activity
				finish();
				if (from.equals("init")) {
					startActivity(new Intent(VoiceprintActivity.this, MainActivity.class));
				}
				break;
			case Broadcast.IFLYTEK_VERIFY_FAIL_VOICE:
				thirdPartyBinder.synthesicSpeech(TTS.VOICEPRINT_VERIFY_FAIL_VOICE);
				retry++;
				break;
			case Broadcast.IFLYTEK_VERIFY_FAIL_TEXT:
				thirdPartyBinder.synthesicSpeech(TTS.VOICEPRINT_VERIFY_FAIL_TEXT);
				retry++;
				break;
			case Broadcast.IFLYTEK_VERIFY_FAIL_OTHER:
				thirdPartyBinder.synthesicSpeech(TTS.VOICEPRINT_VERIFY_FAIL_OTHER);
				retry++;
				break;
			}
		}

	}
}
