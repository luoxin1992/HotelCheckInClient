package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

/**
 * 服务对外暴露的方法名<br>
 * 用于Binder反射调用服务中的方法
 * 
 * @author LuoXin
 *
 */
public class MethodName {
	// 语音相关服务
	public static final String VOICE_GENERATE_PASSWORD = "generatePassword";
	public static final String VOICE_VERIFY_VOICEPRINT = "verifyVoiceprint";
	public static final String VOICE_SPEECH_SYNTHESIS = "speechSynthesis";
	public static final String VOICE_PLAY_EFFECT = "playEffect";

}
