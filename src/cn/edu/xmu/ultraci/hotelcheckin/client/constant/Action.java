package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

/**
 * 服务端请求&客户端操作标识
 * 
 * @author LuoXin
 *
 */
public class Action {
	// 服务端系统类请求
	public static final String SERVER_HEARTBEAT = "heartbeat";
	public static final String SERVER_INIT = "init";
	public static final String SERVER_LOGIN = "login";
	public static final String SERVER_LOGOUT = "logout";
	// 服务端查询类请求
	public static final String SERVER_QUERY_NOTICE = "notice";
	public static final String SERVER_QUERY_MEMBER = "member";
	public static final String SERVER_QUERY_TYPE = "type";
	public static final String SERVER_QUERY_FLOOR = "floor";
	public static final String SERVER_QUERY_STATUS = "status";
	public static final String SERVER_QUERY_ROOM = "room";
	public static final String SERVER_QUERY_INFO = "info";
	// 服务端房务类请求
	public static final String SERVER_GUEST = "guest";
	public static final String SERVER_CHECKIN = "checkin";
	public static final String SERVER_EXTENSION = "extension";
	public static final String SERVER_CHECKOUT = "checkout";
	// 客户端功能操作
	public static final String CLIENT_LOGIN = "login";
	public static final String CLIENT_LOGOUT = "logout";
	public static final String CLIENT_MEMBER_CHECKIN = "member";
	public static final String CLIENT_GUEST_CHECKIN = "guest";
	public static final String CLIENT_EXTENSION = "extension";
	public static final String CLIENT_CHECKOUT = "checkout";

}
