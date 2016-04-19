package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

public class Broadcast {
	private static final String PREFIX = "cn.edu.xmu.ultraci.hotelcheckin.client.";
	private static final String IFLYTEK_PREFIX = PREFIX + "iflytek.";
	private static final String CORE_PREFIX = PREFIX + "core.";
	// 语音服务广播
	public static final String IFLYTEK_INIT_FAIL = IFLYTEK_PREFIX + "INIT_FAIL";
	public static final String IFLYTEK_START_RECORD = IFLYTEK_PREFIX + "START_RECORD";
	public static final String IFLYTEK_VOLUME_CHANGE = IFLYTEK_PREFIX + "VOLUME_CHANGE";
	public static final String IFLYTEK_END_RECORD = IFLYTEK_PREFIX + "END_RECORD";
	public static final String IFLYTEK_VERIFY_SUCC = IFLYTEK_PREFIX + "VERIFY_SUCC";
	public static final String IFLYTEK_VERIFY_FAIL_VOICE = IFLYTEK_PREFIX + "VERIFY_FAIL_VOICE";
	public static final String IFLYTEK_VERIFY_FAIL_TEXT = IFLYTEK_PREFIX + "VERIFY_FAIL_TEXT";
	public static final String IFLYTEK_VERIFY_FAIL_OTHER = IFLYTEK_PREFIX + "VERIFY_FAIL_OTHER";
	// 核心业务广播
	public static final String CORE_SERVER_EXCEPTION = CORE_PREFIX + "SERVER_EXCEPTION";
	public static final String CORE_INIT_SUCC = CORE_PREFIX + "INIT_SUCC";
	public static final String CORE_LOGIN_OUT_SUCC = CORE_PREFIX + "LOGIN_OUT_SUCC";
	public static final String CORE_LOGIN_OUT_NO_PREMISSION = CORE_PREFIX + "LOGIN_OUT_NO_PREMISSION";
	public static final String CORE_LOGIN_OUT_NO_SUCH_CARD = CORE_PREFIX + "LOGIN_OUT_NO_SUCH_CARD";

	public static final String CORE_QUERY_MEMBER_SUCC = CORE_PREFIX + "QUERY_MEMBER_SUCC";
	public static final String CORE_QUERY_MEMBER_NO_SUCH_CARD = CORE_PREFIX + "QUERY_MEMBER_NO_SUCH_CARD";
	public static final String CORE_GUEST_SUCC = CORE_PREFIX + "GUEST_SUCC";
	public static final String CORE_CHECKIN_SUCC = CORE_PREFIX + "CHECKIN_SUCC";
	public static final String CORE_CHECKOUT_SUCC = CORE_PREFIX + "CHECKOUT_SUCC";
	public static final String CORE_CHECKOUT_NEED_PAY = CORE_PREFIX + "CHECKOUT_NEED_PAY";
	public static final String CORE_CHECKOUT_NO_CHECKIN = CORE_PREFIX + "CHECKOUT_NO_CHECKIN";
	public static final String CORE_CHECKOUT_NO_SUCH_CARD = CORE_PREFIX + "CHECKOUT_NO_SUCH_CARD";
	// 外设相关广播
}
