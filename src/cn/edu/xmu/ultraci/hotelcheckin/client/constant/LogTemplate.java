package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

public class LogTemplate {
	//语音服务日志
	public static final String IFLYTEK_INIT_SUCC = "科大讯飞SDK初始化成功";
	public static final String IFLYTEK_INIT_FAIL = "科大讯飞SDK初始化失败，错误%s";
	public static final String IFLYTEK_SYNTHESIS_SUCC = "合成语音播放成功";
	public static final String IFLYTEK_SYNTHESIS_FAIL = "合成语音播放失败，错误%s";
	public static final String IFLYTEK_VERIFY_SUCC = "声纹密码(%s)验证成功，评分%s";
	public static final String IFLYTEK_VERIFY_FAIL = "声纹密码验证失败，错误%s";
	//核心业务日志
	public static final String CORE_SERVER_EXCEPTION = "服务端程序异常，错误%s";
}
