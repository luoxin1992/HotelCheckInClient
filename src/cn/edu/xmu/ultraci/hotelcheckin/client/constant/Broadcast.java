package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

public class Broadcast {
	private static final String PREFIX = "cn.edu.xmu.ultraci.hotelcheckin.client.";
	// 语音相关服务广播
	public static final String IFLYTEK_INIT_FAIL = PREFIX + "iflytek.INIT_FAIL";
	public static final String IFLYTEK_START_RECORD = PREFIX + "iflytek.START_RECORD";
	public static final String IFLYTEK_VOLUME_CHANGE = PREFIX + "iflytek.VOLUME_CHANGE";
	public static final String IFLYTEK_END_RECORD = PREFIX + "iflytek.END_RECORD";
	public static final String IFLYTEK_VERIFY_SUCC = PREFIX + "iflytek.VERIFY_SUCC";
	public static final String IFLYTEK_VERIFY_FAIL_VOICE = PREFIX + "iflytek.VERIFY_FAIL_VOICE";
	public static final String IFLYTEK_VERIFY_FAIL_TEXT = PREFIX + "iflytek.VERIFY_FAIL_TEXT";
	public static final String IFLYTEK_VERIFY_FAIL_OTHER = PREFIX + "iflytek.VERIFY_FAIL_OTHER";
}
