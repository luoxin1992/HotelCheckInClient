package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.LogTemplate;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.MethodName;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.StringUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;

public class VoiceService extends Service {
	private static final String TAG = VoiceService.class.getSimpleName();

	private InitListener mInitListener;
	private SpeechSynthesizer mSynthesizer;
	private SynthesizerListener mSynthesizerListener;
	private SpeakerVerifier mVerifier;
	private VerifierListener mVerifierListener;

	private SoundPool mSoundPool;
	private Map<Integer, Integer> mSoundMap;

	@Override
	public void onCreate() {
		super.onCreate();

		initSDK();
		initSoundPool();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 销毁语音SDK实例
		if (mSynthesizer != null) {
			if (mSynthesizer.isSpeaking()) {
				mSynthesizer.stopSpeaking();
			}
			mSynthesizer.destroy();
		}
		if (mVerifier != null) {
			if (mVerifier.isListening()) {
				mVerifier.stopListening();
			}
			mVerifier.destroy();
		}
		// 销毁SoundPool
		if (mSoundPool != null) {
			mSoundPool.release();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new VoiceServiceBinder();
	}

	/**
	 * 初始化科大讯飞SDK
	 */
	public void initSDK() {
		// SDK初始化监听器
		mInitListener = new InitListener() {
			@Override
			public void onInit(int errorCode) {
				if (errorCode != ErrorCode.SUCCESS) {
					Log.e(TAG, String.format(LogTemplate.IFLYTEK_INIT_FAIL, errorCode));
					SystemUtil.sendLocalBroadcast(VoiceService.this, new Intent(Broadcast.IFLYTEK_INIT_FAIL));
				} else {
					Log.i(TAG, LogTemplate.IFLYTEK_INIT_SUCC);
				}
			}
		};
		// 语音合成监听器
		mSynthesizerListener = new SynthesizerListener() {
			@Override
			public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			}

			@Override
			public void onCompleted(SpeechError arg0) {
				if (arg0 != null && arg0.getErrorCode() != ErrorCode.SUCCESS) {
					Log.e(TAG, String.format(LogTemplate.IFLYTEK_SYNTHESIS_FAIL, arg0.getErrorCode()));
				} else {
					Log.i(TAG, LogTemplate.IFLYTEK_SYNTHESIS_SUCC);
				}
			}

			@Override
			public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			}

			@Override
			public void onSpeakBegin() {
			}

			@Override
			public void onSpeakPaused() {
			}

			@Override
			public void onSpeakProgress(int arg0, int arg1, int arg2) {
			}

			@Override
			public void onSpeakResumed() {
			}
		};
		// 声纹验证监听器
		mVerifierListener = new VerifierListener() {
			@Override
			public void onVolumeChanged(int arg0, byte[] arg1) {
				// 更新音量电平指示器
				Intent intent = new Intent(Broadcast.IFLYTEK_VOLUME_CHANGE);
				intent.putExtra("volume", arg0);
				SystemUtil.sendLocalBroadcast(VoiceService.this, intent);
			}

			@Override
			public void onResult(VerifierResult arg0) {
				// 处理声纹密码验证结果
				if (arg0.ret == ErrorCode.SUCCESS) {
					Log.i(TAG, String.format(LogTemplate.IFLYTEK_VERIFY_SUCC, arg0.vid, arg0.score));
					SystemUtil.sendLocalBroadcast(VoiceService.this, new Intent(Broadcast.IFLYTEK_VERIFY_SUCC));
				}
			}

			@Override
			public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			}

			@Override
			public void onError(SpeechError arg0) {
				if (arg0 != null && arg0.getErrorCode() != ErrorCode.SUCCESS) {
					Log.e(TAG, String.format(LogTemplate.IFLYTEK_VERIFY_FAIL, arg0.getErrorCode()));
					switch (arg0.getErrorCode()) {
					// 太多噪音、声音太小、没检测到音频
					case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
					case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
					case VerifierResult.MSS_ERROR_IVP_ZERO_AUDIO:
						SystemUtil.sendLocalBroadcast(VoiceService.this,
								new Intent(Broadcast.IFLYTEK_VERIFY_FAIL_VOICE));
						break;
					// 音频内容与给定文本不一致
					case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
						SystemUtil.sendLocalBroadcast(VoiceService.this,
								new Intent(Broadcast.IFLYTEK_VERIFY_FAIL_TEXT));
						break;
					// 其他错误
					default:
						SystemUtil.sendLocalBroadcast(VoiceService.this,
								new Intent(Broadcast.IFLYTEK_VERIFY_FAIL_OTHER));
						break;
					}
				}
			}

			@Override
			public void onEndOfSpeech() {
				// 隐藏音量电平指示器
				SystemUtil.sendLocalBroadcast(VoiceService.this, new Intent(Broadcast.IFLYTEK_END_RECORD));
			}

			@Override
			public void onBeginOfSpeech() {
				// 显示音量电平指示器
				SystemUtil.sendLocalBroadcast(VoiceService.this, new Intent(Broadcast.IFLYTEK_START_RECORD));
			}
		};
		// 创建实例
		mSynthesizer = SpeechSynthesizer.createSynthesizer(this, mInitListener);
		mVerifier = SpeakerVerifier.createVerifier(this, mInitListener);
	}

	@TargetApi(21)
	public void initSoundPool() {
		mSoundMap = new HashMap<Integer, Integer>();
		mSoundPool = new SoundPool.Builder().build();
		mSoundMap.put(R.raw.beep, mSoundPool.load(this, R.raw.beep, 1));
		mSoundMap.put(R.raw.ding, mSoundPool.load(this, R.raw.ding, 1));
	}

	/**
	 * 设置语音合成参数
	 */
	public void setSynthesisParams() {
		// 设置引擎类型
		mSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置合成发音人
		mSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		// 设置合成语速
		mSynthesizer.setParameter(SpeechConstant.SPEED, "50");
		// 设置合成音调
		mSynthesizer.setParameter(SpeechConstant.PITCH, "50");
		// 设置合成音量
		mSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
		// 设置播放器音频流类型
		mSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC + "");
		// 设置播放合成音频打断音乐播放
		mSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		// 设置音频保存路径
		mSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
		mSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH,
				getCacheDir() + "/iflytek/tts/" + TimeUtil.getCurrentTime() + ".pcm");
	}

	public void setVerifierParams(String uid, String pwd) {
		// 设置声纹业务类型
		mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
		// 设置声纹密码类型
		mVerifier.setParameter(SpeechConstant.ISV_PWDT, "3");
		// 设置用户唯一标识
		mVerifier.setParameter(SpeechConstant.AUTH_ID, uid);
		// 设置验证使用的声纹密码
		mVerifier.setParameter(SpeechConstant.ISV_PWD, pwd);
		// 设置声纹录音保存路径
		mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
				getCacheDir() + "/iflytek/isv/" + TimeUtil.getCurrentTime() + ".pcm");
		// mVerifier.setParameter(SpeechConstant.AUDIO_SOURCE,
		// MediaRecorder.AudioSource.VOICE_RECOGNITION + "");
	}

	public String generatePassword() {
		return mVerifier.generatePassword(8);
	}

	public void verifyVoiceprint(String uid, String pwd) {
		if (StringUtil.isUsername(uid) && StringUtil.isNumeric(pwd)) {
			setVerifierParams(uid, pwd);
			if (mVerifier.isListening()) {
				mVerifier.stopListening();
			}
			mVerifier.startListening(mVerifierListener);
		}
	}

	public void speechSynthesis(String text) {
		if (!StringUtil.isBlank(text)) {
			setSynthesisParams();
			if (mSynthesizer.isSpeaking()) {
				mSynthesizer.stopSpeaking();
			}
			mSynthesizer.startSpeaking(text, mSynthesizerListener);
		}
	}

	public void playEffect(int resId) {
		mSoundPool.play(mSoundMap.get(resId), 1, 1, 0, 0, 1);
	}

	public class VoiceServiceBinder extends Binder {
		public Object invokeMethod(String methodName, Object[] paramValues) {
			try {
				switch (methodName) {
				case MethodName.VOICE_GENERATE_PASSWORD:
					return SystemUtil.invokeServiceMethod(VoiceService.this, methodName, null, null);
				case MethodName.VOICE_VERIFY_VOICEPRINT:
					return SystemUtil.invokeServiceMethod(VoiceService.this, methodName,
							new Class<?>[] { String.class, String.class }, paramValues);
				case MethodName.VOICE_SPEECH_SYNTHESIS:
					return SystemUtil.invokeServiceMethod(VoiceService.this, methodName,
							new Class<?>[] { String.class }, paramValues);
				case MethodName.VOICE_PLAY_EFFECT:
					return SystemUtil.invokeServiceMethod(VoiceService.this, methodName, new Class<?>[] { int.class },
							paramValues);
				}
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
