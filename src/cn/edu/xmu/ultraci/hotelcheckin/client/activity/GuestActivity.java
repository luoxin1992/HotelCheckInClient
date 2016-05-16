package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 散客登记界面
 */
public class GuestActivity extends BaseActivity implements OnFocusChangeListener {
	private static final String TAG = GuestActivity.class.getSimpleName();

	private GuestReceiver receiver;

	private String action;
	private Bundle extras;

	private long lastCaptchaTime;

	private EditText etMobile;
	private EditText etCaptcha;
	private Button btnCaptcha;
	private Button btnSubmit;

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
		filter.addAction(Broadcast.MOB_CAPTCHA_SMS_SEND);
		filter.addAction(Broadcast.MOB_CAPTCHA_VERIFY_OK);
		filter.addAction(Broadcast.MOB_CAPTCHA_VERIFY_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_GUEST_OK);
		receiver = new GuestReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(true, getTitle().toString(), true, 60, R.layout.activity_guest, false);

		etMobile = (EditText) findViewById(R.id.et_mobile);
		etCaptcha = (EditText) findViewById(R.id.et_captcha);
		btnCaptcha = (Button) findViewById(R.id.btn_captcha);
		btnSubmit = (Button) findViewById(R.id.btn_submit);

		etMobile.setInputType(InputType.TYPE_NULL);
		etMobile.setOnFocusChangeListener(this);
		etCaptcha.setInputType(InputType.TYPE_NULL);
		etCaptcha.setOnFocusChangeListener(this);
	}

	public void onNumPadClick(View v) {
		switch (v.getId()) {
		case R.id.btn_num_0:
			if (etMobile.hasFocus()) {
				etMobile.append("0");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("0");
			}
			break;
		case R.id.btn_num_1:
			if (etMobile.hasFocus()) {
				etMobile.append("1");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("1");
			}
			break;
		case R.id.btn_num_2:
			if (etMobile.hasFocus()) {
				etMobile.append("2");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("2");
			}
			break;
		case R.id.btn_num_3:
			if (etMobile.hasFocus()) {
				etMobile.append("3");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("3");
			}
			break;
		case R.id.btn_num_4:
			if (etMobile.hasFocus()) {
				etMobile.append("4");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("4");
			}
			break;
		case R.id.btn_num_5:
			if (etMobile.hasFocus()) {
				etMobile.append("5");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("5");
			}
			break;
		case R.id.btn_num_6:
			if (etMobile.hasFocus()) {
				etMobile.append("6");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("6");
			}
			break;
		case R.id.btn_num_7:
			if (etMobile.hasFocus()) {
				etMobile.append("7");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("7");
			}
			break;
		case R.id.btn_num_8:
			if (etMobile.hasFocus()) {
				etMobile.append("8");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("8");
			}
			break;
		case R.id.btn_num_9:
			if (etMobile.hasFocus()) {
				etMobile.append("9");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.append("9");
			}
			break;
		case R.id.btn_reset:
			if (etMobile.hasFocus()) {
				etMobile.setText("");
			} else if (etCaptcha.hasFocus()) {
				etCaptcha.setText("");
			}
			break;
		case R.id.btn_delete:
			if (etMobile.hasFocus()) {
				String t = etMobile.getText().toString();
				etMobile.setText(t.substring(0, t.length() > 0 ? t.length() - 1 : t.length()));
			} else if (etCaptcha.hasFocus()) {
				String t = etCaptcha.getText().toString();
				etCaptcha.setText(t.substring(0, t.length() > 0 ? t.length() - 1 : t.length()));
			}
			break;
		}
	}

	public void onCaptchaClick(View v) {
		String mobile = etMobile.getText().toString().trim();
		extras.putString("mobile", mobile);
		// 检查手机号码格式
		if (!mobile.matches("1[3|4|5|7|8|]\\d{9}")) {
			getThirdpartyServiceBinder().synthesicSpeech(TTS.GUEST_MOBILE_INVALID);
			showDialog(R.drawable.warn, TTS.GUEST_MOBILE_INVALID);
			return;
		}
		// 检查上次获取验证码时间
		if (System.currentTimeMillis() - lastCaptchaTime <= 30 * 1000) {
			getThirdpartyServiceBinder().synthesicSpeech(String.format(TTS.GUEST_CAPTCHA_WAITING,
					30 - (System.currentTimeMillis() - lastCaptchaTime) / 1000));
			showDialog(R.drawable.warn, String.format(TTS.GUEST_CAPTCHA_WAITING,
					30 - (System.currentTimeMillis() - lastCaptchaTime) / 1000));
			return;
		}
		// 获取验证码
		showProcess();
		lastCaptchaTime = System.currentTimeMillis();
		getThirdpartyServiceBinder().sendSMSCaptcha(mobile);
	}

	public void onSubmitClick(View v) {
		String mobile = etMobile.getText().toString().trim();
		String captcha = etCaptcha.getText().toString().trim();
		// 检查验证码格式
		if (!captcha.matches("\\d{4}")) {
			getThirdpartyServiceBinder().synthesicSpeech(TTS.GUEST_CAPTCHA_INVALID);
			showDialog(R.drawable.warn, TTS.GUEST_CAPTCHA_INVALID);
			return;
		}
		// 提交验证码
		showProcess();
		getThirdpartyServiceBinder().verifyCaptcha(mobile, captcha);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_mobile:
			if (hasFocus) {
				btnCaptcha.setVisibility(View.VISIBLE);
			} else {
				btnCaptcha.setVisibility(View.GONE);
			}
			break;
		case R.id.et_captcha:
			if (hasFocus) {
				btnSubmit.setVisibility(View.VISIBLE);
			} else {
				btnSubmit.setVisibility(View.GONE);
			}
			break;
		}
	}

	class GuestReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.GUEST_HINT);
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INTERNAL_ERROR);
				showDialog(R.drawable.warn, TTS.INTERNAL_ERROR);
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (!isForeground) {
					dismissDialog();
				}
				if (isChangingUI) {
					isChangingUI = false;
					Intent newIntent = new Intent(GuestActivity.this, SelectTimeActivity.class);
					newIntent.putExtra("action", action);
					newIntent.putExtra("extras", extras);
					startActivityForResult(newIntent, Code.CHANGE_UI);
				}
				break;
			case Broadcast.CORE_GUEST_OK:
				isChangingUI = true;
				getThirdpartyServiceBinder().synthesicSpeech(TTS.GUEST_CAPTCHA_OK);
				showDialog(R.drawable.warn, TTS.GUEST_CAPTCHA_OK);
				break;
			case Broadcast.MOB_CAPTCHA_SMS_SEND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.GUEST_CAPTCHA_SEND);
				showDialog(R.drawable.warn, TTS.GUEST_CAPTCHA_SEND);
				break;
			case Broadcast.MOB_CAPTCHA_VERIFY_OK:
				getCoreServiceBinder().guest(extras.getString("mobile"), extras.getString("idcard"));
				break;
			case Broadcast.MOB_CAPTCHA_VERIFY_FAIL:
				lastCaptchaTime = 0;
				getThirdpartyServiceBinder().synthesicSpeech(TTS.GUEST_CAPTCHA_FAIL);
				showDialog(R.drawable.warn, TTS.GUEST_CAPTCHA_FAIL);
				break;
			}
		}
	}

}
