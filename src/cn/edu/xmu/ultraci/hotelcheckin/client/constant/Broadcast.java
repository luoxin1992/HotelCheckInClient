package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

public class Broadcast {
	private static final String IFLYTEK_PREFIX = "action.thirdparty.iflytek.";
	private static final String MOB_PREFIX = "action.thirdparty.mob.";
	private static final String CORE_PREFIX = "action.core.";
	private static final String MISC_PREFIX = "action.misc.";

	// 第三方服务广播
	public static final String IFLYTEK_INIT_FAIL = IFLYTEK_PREFIX + "INIT_FAIL";
	public static final String IFLYTEK_RECORD_START = IFLYTEK_PREFIX + "record.START";
	public static final String IFLYTEK_RECORD_END = IFLYTEK_PREFIX + "record.END";
	public static final String IFLYTEK_RECORD_VOLUME_CHANGE = IFLYTEK_PREFIX + "record.VOLUME_CHANGE";
	public static final String IFLYTEK_VERIFY_OK = IFLYTEK_PREFIX + "verify.OK";
	public static final String IFLYTEK_VERIFY_FAIL_VOICE = IFLYTEK_PREFIX + "verify.FAIL_VOICE";
	public static final String IFLYTEK_VERIFY_FAIL_TEXT = IFLYTEK_PREFIX + "verify.FAIL_TEXT";
	public static final String IFLYTEK_VERIFY_FAIL_OTHER = IFLYTEK_PREFIX + "verify.FAIL_OTHER";

	public static final String MOB_CAPTCHA_SMS_SEND = MOB_PREFIX + "captcha.SMS_SEND";
	public static final String MOB_CAPTCHA_VOICE_SEND = MOB_PREFIX + "captcha.VOICE_SEND";
	public static final String MOB_CAPTCHA_VERIFY_OK = MOB_PREFIX + "captcha.VERIFY_OK";
	public static final String MOB_CAPTCHA_VERIFY_FAIL = MOB_PREFIX + "captcha.VERITY_FAIL";

	// 核心服务广播
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

	// 杂项服务广播
	public static final String MISC_BLUETOOTH_NONSUPPORT = MISC_PREFIX + "bluetooth.NONSUPPORT";
	public static final String MISC_BLUETOOTH_DISABLE = MISC_PREFIX + "bluetooeh.DISABLE";
	public static final String MISC_NFC_NONSUPPORT = MISC_PREFIX + "nfc.NONSUPPORT";
	public static final String MISC_NFC_DISABLE = MISC_PREFIX + "nfc.DISABLE";
	public static final String MISC_PRINTER_CONN_FAIL = MISC_PREFIX + "printer.CONN_FAIL";
}
