package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

public class LogTemplate {
	// 第三方服务日志
	public static final String IFLYTEK_INIT_OK = "科大讯飞SDK初始化成功";
	public static final String IFLYTEK_INIT_FAIL = "科大讯飞SDK初始化失败(%s)";
	public static final String IFLYTEK_SYNTHESIS_OK = "合成语音播放成功";
	public static final String IFLYTEK_SYNTHESIS_FAIL = "合成语音播放失败(%s)";
	public static final String IFLYTEK_VERIFY_OK = "声纹密码验证成功(%s)";
	public static final String IFLYTEK_VERIFY_FAIL = "声纹密码验证失败(%s)";
	public static final String MOB_CAPTCHA_SMS_SEND =  "短信验证码已发送";
	public static final String MOB_CAPTCHA_VOICE_SEND =  "语音验证码已发送";
	public static final String MOB_CAPTCHA_VERIFY_OK =  "验证码校验成功";
	public static final String MOB_CAPTCHA_VERIFY_FAIL =  "验证码校验失败";
	public static final String MOB_CAPTCHA_UNKNOWN_FAIL =  "掌淘SDK未知错误(%s)";
	// 核心服务日志
	public static final String CORE_SERVER_EXCEPTION = "服务端程序异常，错误%s";
	// 杂项服务日志
	public static final String MISC_BLUETOOTH_NONSUPPORT = "不支持蓝牙";
	public static final String MISC_BLUETOOTH_DISABLE = "蓝牙未启用";
	public static final String MISC_BLUETOOTH_OK = "蓝牙正常";
	public static final String MISC_NFC_NONSUPPORT = "不支持NFC";
	public static final String MISC_NFC_DISABLE = "NFC未启用";
	public static final String MISC_NFC_OK = "NFC正常";
	public static final String MISC_PRINTER_CONN_OK = "打印机连接成功";
	public static final String MISC_PRINTER_CONN_FAIL = "打印机连接失败(%s)";
	public static final String MISC_PRINTER_DISCONN_OK = "打印机断开成功";
	public static final String MISC_PRINTER_DISCONN_FAIL = "打印机断开失败(%s)";
	public static final String MISC_PRINTER_SEND_FAIL = "打印机通讯失败(%s)";
}
