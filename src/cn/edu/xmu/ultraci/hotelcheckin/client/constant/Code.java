package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

/**
 * 服务端全局返回码&客户端Activity跳转请求码
 * 
 * @author LuoXin
 *
 */
public class Code {
	// 客户端Activity跳转请求码
	public static final int NEXT_ACTIVITY = 1000;
	public static final int POPUP_DIALOG = 2000;

	// 服务端全局返回码
	// 鉴权失败
	public static final int ERRORCODE_AUTH_FAIL = -1;
	// 成功
	public static final int ERRORCODE_OK = 0;
	// 内部错误
	public static final int ERRORCODE_INTERNAL_ERR = 30000;
	// 非法请求
	public static final int ERRORCODE_INVALID_REQ = 40001;
	// 登录登出
	public static final int ERRORCODE_LOGIN_OUT_NO_SUCH_CARD = 40101;
	public static final int ERRORCODE_LOGIN_OUT_NO_PREMISSION = 40102;
	// 查询
	public static final int ERRORCODE_QUERY_MEMBER_NO_SUCH_CARD = 40201;
	public static final int ERRORCODE_QUERY_STATUS_INVALID_FILTER = 40301;
	public static final int ERRORCODE_QUERY_ROOM_NO_SUCH_CARD = 40401;
	public static final int ERRORCODE_QUERY_ROOM_NO_CHECK_IN = 40402;
	public static final int ERRORCODE_QUERY_INFO_NO_SUCH_TYPE = 40501;
	// 房务
	public static final int ERRORCODE_NEW_GUEST_FILE_NOT_FOUND = 40601;
	public static final int ERRORCODE_CHECKOUT_NO_SUCH_CARD = 40801;
	public static final int ERRORCODE_CHECKOUT_NO_CHECKIN = 40802;
	public static final int ERRORCODE_CHECKOUT_NEED_PAY = 40803;
	// 文件上传
	public static final int ERRORCODE_FILE_UPLOAD_ERROR = 40901;
}
