package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

/**
 * 因暂不支持系统设置<br>
 * 故用此类代替配置文件
 * 
 * @author LuoXin
 *
 */
public class Config {
	// 服务端
	// 美团云
	public static final String SERVER_IP = "103.37.165.152";
	public static final String SERVER_PORT = "8081";
	// 测试服
	// public static final String SERVER_IP = "10.30.188.223";
	// public static final String SERVER_PORT = "9237";

	// 服务端请求URL
	public static final String URK_PREFIX = "http://" + Config.SERVER_IP + ":" + Config.SERVER_PORT;
	public static final String ADMIN_URL = URK_PREFIX + "/HotelCheckInServer/AdminServlet.do";
	public static final String CLIENT_URL = URK_PREFIX + "/HotelCheckInServer/ClientServlet.do";
	public static final String FILE_UPLOAD_URL = URK_PREFIX + "/HotelCheckInServer/FileUploadServlet.do";

	// 鉴权Token
	public static final String TOKEN = "cVko8367";

	// 蓝牙打印机MAC
	public static final String BT_MAC = "00:0D:18:00:06:0A";

	// 第三方库
	public static final String IFLYTEK_APPID = "5712ee34";
	public static final String MOB_APP_KEY = "11d7191a172b6";
	public static final String MOB_APP_SECRET = "350686c34143695c9fe18d5cfe96df96";

}
