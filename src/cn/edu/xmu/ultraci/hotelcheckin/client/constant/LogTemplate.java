package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

public class LogTemplate {
	// 第三方服务日志
	public static final String THIRDPARTY_SERIVCE_BOUND = "第三方服务绑定";
	public static final String THIRDPARTY_SERIVCE_UNBOUND = "第三方服务解绑";

	public static final String IFLYTEK_INIT_OK = "科大讯飞SDK初始化成功";
	public static final String IFLYTEK_INIT_FAIL = "科大讯飞SDK初始化失败(%s)";
	public static final String IFLYTEK_SYNTHESIS_OK = "合成语音播放成功";
	public static final String IFLYTEK_SYNTHESIS_FAIL = "合成语音播放失败(%s)";
	public static final String IFLYTEK_VERIFY_OK = "声纹密码验证成功(%s)";
	public static final String IFLYTEK_VERIFY_FAIL = "声纹密码验证失败(%s)";

	public static final String MOB_CAPTCHA_SMS_SEND = "短信验证码已发送";
	public static final String MOB_CAPTCHA_VOICE_SEND = "语音验证码已发送";
	public static final String MOB_CAPTCHA_VERIFY_OK = "验证码校验成功";
	public static final String MOB_CAPTCHA_VERIFY_FAIL = "验证码校验失败";
	public static final String MOB_CAPTCHA_UNKNOWN_FAIL = "掌淘SDK未知错误(%s)";

	// 核心服务日志
	public static final String CORE_SERIVCE_BOUND = "核心服务绑定";
	public static final String CORE_SERIVCE_UNBOUND = "核心服务解绑";

	public static final String CORE_SERVER_PROCESS_FAIL = "服务端程序错误(%s)";
	public static final String CORE_SERVER_REQUEST_FAIL = "服务端请求失败(%s)";

	public static final String CORE_INIT_OK = "系统初始化成功";
	public static final String CORE_LOGIN_OK = "登录成功(%s)";
	public static final String CORE_LOGIN_NO_PREMISSION = "登录失败(%s)，未授权";
	public static final String CORE_LOGIN_NO_SUCH_CARD = "登录失败(%s)，无此员工";
	public static final String CORE_LOGOUT_OK = "登出成功(%s)";
	public static final String CORE_LOGOUT_NO_PREMISSION = "登出失败(%s)，未授权";
	public static final String CORE_LOGOUT_NO_SUCH_CARD = "登出失败(%s)，无此员工";

	public static final String CORE_QUERY_NOTICE_OK = "查询系统公告成功";
	public static final String CORE_QUERY_MEMBER_OK = "查询会员信息成功(%s)";
	public static final String CORE_QUERY_MEMBER_NO_SUCH_CARD = "查询会员信息失败(%s)，无此会员";
	public static final String CORE_QUERY_TYPE_OK = "查询房型信息成功，共%s种";
	public static final String CORE_QUERY_FLOOR_OK = "查询楼层信息成功，共%s层";
	public static final String CORE_QUERY_STATUS_OK = "查询房态信息成功，共%s间";
	public static final String CORE_QUERY_ROOM_OK = "查询房间信息成功(%s)";
	public static final String CORE_QUERY_ROOM_NO_CHECKIN = "查询房间信息失败(%s)，房间未入住";
	public static final String CORE_QUERY_ROOM_NO_SUCH_CARD = "查询房间信息失败(%s)，无此房间";
	public static final String CORE_QUERY_INFO_OK = "查询其他信息成功";

	public static final String CORE_GUEST_OK = "提交散客信息成功(%s)";
	public static final String CORE_CHECKIN_OK = "办理入住手续成功(%s)";
	public static final String CORE_EXTENSION_OK = "办理续住手续成功(%s)";
	public static final String CORE_CHECKOUT_OK = "办理退房手续成功(%s)";
	public static final String CORE_CHECKOUT_NEED_PAY = "办理退房手续失败(%s)，房间欠费";
	public static final String CORE_CHECKOUT_NO_CHECKIN = "办理退房手续失败(%s)，房间未入住";

	public static final String CORE_FILE_UPLOAD_OK = "文件上传成功(%s)";
	public static final String CORE_FILE_NOT_FOUND = "未找到要上传的文件(%s)";
	public static final String CORE_FILE_DOWNLOAD_OK = "文件下载成功(%s)";

	// 杂项服务日志
	public static final String MISC_SERIVCE_BOUND = "杂项服务绑定";
	public static final String MISC_SERIVCE_UNBOUND = "杂项服务解绑";

	public static final String MISC_BLUETOOTH_NONSUPPORT = "不支持蓝牙";
	public static final String MISC_BLUETOOTH_DISABLE = "蓝牙未启用";
	public static final String MISC_BLUETOOTH_OK = "蓝牙正常";

	public static final String MISC_NFC_NONSUPPORT = "不支持NFC";
	public static final String MISC_NFC_DISABLE = "NFC未启用";
	public static final String MISC_NFC_OK = "NFC正常";

	public static final String MISC_PRINTER_CONN_OK = "打印机连接成功";
	@Deprecated
	public static final String MISC_PRINTER_CONN_FAIL = "打印机连接失败(%s)";
	public static final String MISC_PRINTER_DISCONN_OK = "打印机断开成功";
	@Deprecated
	public static final String MISC_PRINTER_DISCONN_FAIL = "打印机断开失败(%s)";
	public static final String MISC_PRINTER_FAIL = "打印机通讯错误(%s)";
}
