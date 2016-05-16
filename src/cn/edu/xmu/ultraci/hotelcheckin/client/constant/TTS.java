package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

/**
 * 语音合成文本&对话框提示文本
 * 
 * @author LuoXin
 *
 */
public class TTS {
	public static final String INTERNAL_ERROR = "内部错误";

	// 初始化界面
	public static final String INIT_WELCOME = "欢迎使用酒店自助入住终端";
	public static final String INIT_BLUETOOTH_NONSUPPORT = "设备不支持蓝牙";
	public static final String INIT_BLUETOOTH_DISABLE = "请先启用蓝牙";
	public static final String INIT_NFC_NONSUPPORT = "设备不支持NFC";
	public static final String INIT_NFC_DISABLE = "请先启用NFC";

	// 刷卡界面
	public static final String SWIPE_CARD_STAFF_HINT = "请将您的员工卡放置在感应区内";
	public static final String SWIPE_CARD_STAFF_OK = "您好，员工%s";
	public static final String SWIPE_CARD_STAFF_NO_PREMISSION = "%s，您没有权限使用此功能";
	public static final String SWIPE_CARD_STAFF_NO_SUCH_CARD = "请刷员工卡";

	public static final String SWIPE_CARD_MEMBER_HINT = "请将您的会员卡放置在感应区内";
	public static final String SWIPE_CARD_MEMBER_OK = "您好，会员%s";
	public static final String SWIPE_CARD_MEMBER_NO_SUCH_CARD = "请刷会员卡";

	public static final String SWIPE_CARD_ROOM_HINT = "请将您的房卡放置在感应区内";
	public static final String SWIPE_CARD_ROOM_OK = "您好，%s房间";
	public static final String SWIPE_CARD_ROOM_NO_CHECKIN = "没有%s房间的入住信息，请联系前台";
	public static final String SWIPE_CARD_ROOM_NO_SUCH_CARD = "请刷房卡";

	// 声纹验证界面
	public static final String VOICEPRINT_HINT = "请在听到提示音后，朗读屏幕上的数字，以验证您的身份";
	public static final String VOICEPRINT_OK = "员工身份验证成功";
	public static final String VOICEPRINT_LIMITED = "身份验证次数已超过限制";
	public static final String VOICEPRINT_FAIL_TEXT = "您朗读的数字有误，让我们再试一次";
	public static final String VOICEPRINT_FAIL_VOICE = "听不清您的声音，让我们再试一次";
	public static final String VOICEPRINT_FAIL_OTHER = "验证失败，让我们再试一次";

	// 选择时间界面
	public static final String SELECT_TIME_HINT = "请选择您的预离时间";
	public static final String SELECT_TIME_OK = "您选择了%s";
	public static final String SELECT_TIME_EARLY = "预离时间不应早于%s，请重新选择";

	// 选择房间界面
	public static final String SELECT_ROOM_HINT = "请选择您的意向房间，您可以通过右侧的复选框进行房型筛选";
	public static final String SELECT_ROOM_OK = "您选择了%s房间";

	// 房间信息界面
	public static final String ROOM_INFO_HINT = "请您核对屏幕上显示信息准确无误";
	public static final String ROOM_INFO_NEED_PAY = "您的房间已经欠费，请联系前台";

	// 支付界面
	public static final String PAY_HINT = "请您扫描屏幕上的二维码完成支付";
	public static final String PAY_PRINT_OK = "支付成功。小票打印完毕，请沿锯齿撕下";
	public static final String PAY_PRINT_FAIL = "支付成功。如需打印小票，请联系前台";

	// 拍摄身份证界面
	public static final String CAPTURE_ID_CARD_HINT = "请将您的身份证放置在拍摄区域内，然后点击屏幕完成拍照";
	public static final String CAPTURE_ID_CARD_UPLOADED = "身份证照已上传";

	// 散客登记界面
	public static final String GUEST_HINT = "请验证您的手机号码";
	public static final String GUEST_MOBILE_INVALID = "手机号码格式不正确";
	public static final String GUEST_CAPTCHA_INVALID = "验证码应为4位数字";
	public static final String GUEST_CAPTCHA_WAITING = "您获取验证码太频繁，请等待%s秒";
	public static final String GUEST_CAPTCHA_SEND = "验证码已发送至您的手机，请查收";
	public static final String GUEST_CAPTCHA_OK = "散客身份验证成功";
	public static final String GUEST_CAPTCHA_FAIL = "您填写的验证码已过期或不正确";

	// 结果界面
	public static final String RESULT_CHECKIN_HINT = "入住手续办理成功，愿您在此度过一段美好时光";
	public static final String RESULT_EXTENSION_HINT = "续住手续办理成功，愿您在此度过一段美好时光";
	public static final String RESULT_CHECKOUT_HINT = "退房手续办理成功，真诚期待您的再次光临，祝您一路顺风";

}
