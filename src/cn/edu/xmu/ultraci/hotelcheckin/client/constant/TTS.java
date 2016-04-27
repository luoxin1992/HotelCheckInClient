package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

/**
 * 语音播报合成文本内容
 * 
 * @author LuoXin
 *
 */
public class TTS {
	// TTS初始化提示
	public static final String WELCOME = "欢迎使用酒店自助入住终端。";

	// 刷卡界面
	public static final String SWIPE_CRAD_LOGIN_OUT = "请将您的员工卡放置在感应区内。";
	public static final String SWIPE_CRAD_LOGIN_NO_PREMISSION = "%s，您没有权限登录本系统。";
	public static final String SWIPE_CRAD_LOGOUT_NO_PREMISSION = "%s，您没有权限登出本系统。";
	public static final String SWIPE_CRAD_LOGIN_OUT_NO_SUCH_CARD = "请刷员工卡。";

	public static final String SWIPE_CRAD_QUERY_MEMBER = "请将您的会员卡放置在感应区内。";
	public static final String SWIPE_CARD_QUERK_MEMBER_OK = "%s，欢迎光临。";
	public static final String SWIPE_CRAD_QUERY_MEMBER_NO_SUCH_CARD = "请刷会员卡。";

	public static final String SWIPE_CRAD_QUERY_ROOM = "请将您的房卡放置在感应区内。";
	public static final String SWIPE_CRAD_QUERY_ROOM_NO_CHECKIN = "没有此房间的入住信息，请联系前台。";
	public static final String SWIPE_CRAD_QUERY_ROOM_NO_SUCH_CARD = "请刷房卡。";

	// 声纹验证界面
	public static final String VOICEPRINT = "%s，我们需要验证您的身份。请在听到提示音后，朗读屏幕上的数字。";
	public static final String VOICEPRINT_OK = "身份验证成功。";
	public static final String VOICEPRINT_FAIL_TEXT = "您朗读的数字有误。";
	public static final String VOICEPRINT_FAIL_VOICE = "听不清您的声音。";
	public static final String VOICEPRINT_FAIL_OTHER = "验证失败。";
	public static final String VOICEPRINT_RETRY = "让我们再试一次。";
	public static final String VOICEPRINT_LIMITED = "验证次数已超过限制。";

	// 拍摄身份证界面
	public static final String CAPTURE_ID_CARD = "请将您的身份证放置在拍摄区域内，然后，点击屏幕完成拍照。";

	// 散客登记界面
	public static final String GUEST = "请验证您的手机号码。";

	// 支付界面
	public static final String PAY = "请核对屏幕上显示信息准确无误。然后，扫描左侧二维码完成支付。";

	// 选择时间界面
	public static final String SELECT_TIME = "请选择您的预离时间。";

	// 选择房间界面
	public static final String SELECT_ROOM = "请选择您的意向房间，您可以通过屏幕右侧";

	// 查看房间信息界面
	public static final String ROOM_INFO_EXTENSION = "请确认要办理续住的房间信息。";
	public static final String ROOM_INFO_CHECKOUT = "请确认要办理退房的房间信息。";
	public static final String ROOM_INFO_CHECKOUT_NEED_PAY = "您的房间已经欠费，请联系前台。";
}
