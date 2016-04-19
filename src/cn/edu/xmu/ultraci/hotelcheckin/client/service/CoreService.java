package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.apache.commons.codec.android.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Action;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Config;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.ErrorCode;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.LogTemplate;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.URL;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.CheckinDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.CheckoutDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.FileUploadDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.GuestDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.HeartbeatDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.InitDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.LoginDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.MemberDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.HttpUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.RandomUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cz.msebera.android.httpclient.Header;

/**
 * 核心业务逻辑实现<br>
 * 主要用于同服务端交互
 * 
 * @author LuoXin
 *
 */
public class CoreService extends Service {
	private static final String TAG = CoreService.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new CoreServiceBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 根据文档5.1节所述算法计算加密签名
	 * 
	 * @param random
	 *            随机字符串
	 * @return 加密签名
	 */
	private String getSignature(String random) {
		// 排序和合并
		String[] array = new String[] { Config.TOKEN, random };
		Arrays.sort(array);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}
		// 计算加密签名
		return DigestUtils.sha1Hex(sb.toString());
	}

	/**
	 * 向HTTP请求中添加共通的参数<br>
	 * 包括：random、signature、action、device
	 * 
	 * @param params
	 *            请求参数集
	 * @param action
	 *            请求类型
	 */
	private void addCommonParams(RequestParams params, String action) {
		String random = RandomUtil.generateRandomStr(6);
		params.put("random", random);
		params.put("signature", getSignature(random));
		params.put("device", SystemUtil.getMacAddress(this));
		// 文件上传接口目前不需要action参数
		if (action != null) {
			params.put("action", action);
		}
	}

	/**
	 * 记录出错信息并报告停止服务<br>
	 * 当请求服务端出错时调用
	 * 
	 * @param statusCode
	 *            服务端HTTP状态码
	 */
	private void onServiceFailure(int statusCode) {
		Log.e(TAG, String.format(LogTemplate.CORE_SERVER_EXCEPTION, statusCode));
		SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.CORE_SERVER_EXCEPTION));
	}

	public void heartbeat() {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.HEARTBEAT);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				HeartbeatDTO retModel = JSON.parseObject(new String(arg2), HeartbeatDTO.class);
				if (retModel.getResult() != ErrorCode.OK) {
					// 心跳响应异常视为服务端错误
					onServiceFailure(-1);
				}
			}
		});
	}

	public void init() {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.INIT);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				InitDTO retModel = JSON.parseObject(new String(arg2), InitDTO.class);
				if (retModel.getResult() == ErrorCode.OK) {
					if (retModel.getUpgrade() != null) {
						// TODO 客户端在线升级
					}
					if (retModel.getAds().size() != 0) {
						// TODO 客户端空闲播放广告
					}
					Intent intent = new Intent(Broadcast.CORE_INIT_SUCC);
					intent.putExtra("notice", retModel.getNotice());
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
				} else {
					onServiceFailure(-1);
				}
			}
		});
	}

	public void login(String cardid) {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.LOGIN);
		params.put("cardid", cardid);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				LoginDTO retModel = JSON.parseObject(new String(arg2), LoginDTO.class);
				Intent intent;
				switch (retModel.getResult()) {
				case ErrorCode.OK:
					intent = new Intent(Broadcast.CORE_LOGIN_OUT_SUCC);
					intent.putExtra("name", retModel.getName());
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					break;
				case ErrorCode.LOGIN_OUT_NO_PREMISSION:
					intent = new Intent(Broadcast.CORE_LOGIN_OUT_NO_PREMISSION);
					intent.putExtra("name", retModel.getName());
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					break;
				case ErrorCode.LOGIN_OUT_NO_SUCH_CARD:
					intent = new Intent(Broadcast.CORE_LOGIN_OUT_NO_SUCH_CARD);
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					break;
				default:
					onServiceFailure(-1);
					break;
				}
			}
		});
	}

	public void logout(String cardid) {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.LOGOUT);
		params.put("cardid", cardid);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				LoginDTO retModel = JSON.parseObject(new String(arg2), LoginDTO.class);
				Intent intent;
				switch (retModel.getResult()) {
				case ErrorCode.OK:
					intent = new Intent(Broadcast.CORE_LOGIN_OUT_SUCC);
					intent.putExtra("name", retModel.getName());
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					break;
				case ErrorCode.LOGIN_OUT_NO_PREMISSION:
					intent = new Intent(Broadcast.CORE_LOGIN_OUT_NO_PREMISSION);
					intent.putExtra("name", retModel.getName());
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					break;
				case ErrorCode.LOGIN_OUT_NO_SUCH_CARD:
					intent = new Intent(Broadcast.CORE_LOGIN_OUT_NO_SUCH_CARD);
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					break;
				default:
					onServiceFailure(-1);
					break;
				}
			}
		});
	}

	public void sms() {

	}

	public void member(String cardid) {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.HEARTBEAT);
		params.put("cardid", cardid);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				MemberDTO retModel = JSON.parseObject(new String(arg2), MemberDTO.class);
				if (retModel.getResult() != ErrorCode.OK) {
					Intent intent = new Intent(Broadcast.CORE_QUERY_MEMBER_SUCC);
					intent.putExtra("member", retModel);
					SystemUtil.sendLocalBroadcast(CoreService.this, intent);
				} else if (retModel.getResult() == ErrorCode.QUERY_MEMBER_NO_SUCH_CARD) {
					SystemUtil.sendLocalBroadcast(CoreService.this,
							new Intent(Broadcast.CORE_QUERY_MEMBER_NO_SUCH_CARD));
				} else {
					onServiceFailure(-1);
				}
			}
		});
	}

	public void type() {

	}

	public void floor() {

	}

	public void status() {

	}

	public void room() {

	}

	public void guest(final String mobile, String idcard, boolean upload) {
		if (upload) {
			// 上传文件
			RequestParams params = new RequestParams();
			addCommonParams(params, null);
			params.put("type", "idcard");
			try {
				params.put("file", new File(idcard));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				onServiceFailure(-1);
				return;
			}
			HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					onServiceFailure(-1);
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					Log.d(TAG, new String(arg2));
					FileUploadDTO retModel = JSON.parseObject(new String(arg2), FileUploadDTO.class);
					if (retModel.getResult() == ErrorCode.OK) {
						// 文件上传成功，再次调用此方法提交散客信息
						CoreService.this.guest(mobile, retModel.getFilename(), false);
					} else {
						onServiceFailure(-1);
					}
				}
			});
		} else {
			// 提交信息
			RequestParams params = new RequestParams();
			addCommonParams(params, Action.NEW_GUEST);
			params.put("mobile", mobile);
			params.put("idcard", idcard);
			HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					onServiceFailure(-1);
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					Log.d(TAG, new String(arg2));
					GuestDTO retModel = JSON.parseObject(new String(arg2), GuestDTO.class);
					if (retModel.getResult() == ErrorCode.OK) {
						Intent intent = new Intent(Broadcast.CORE_GUEST_SUCC);
						intent.putExtra("id", retModel.getId());
						SystemUtil.sendLocalBroadcast(CoreService.this, intent);
					} else {
						onServiceFailure(-1);
					}
				}
			});
		}
	}

	public void checkin(String customer, String room, String time) {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.CHECKIN);
		params.put("customer", customer);
		params.put("room", room);
		params.put("time", time);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				CheckinDTO retModel = JSON.parseObject(new String(arg2), CheckinDTO.class);
				if (retModel.getResult() == ErrorCode.OK) {
					SystemUtil.sendLocalBroadcast(CoreService.this, new Intent(Broadcast.CORE_CHECKIN_SUCC));
				} else {
					onServiceFailure(-1);
				}
			}
		});
	}

	public void checkout(String cardid) {
		RequestParams params = new RequestParams();
		addCommonParams(params, Action.CHECKOUT);
		params.put("cardid", cardid);
		HttpUtil.post(URL.CLIENT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				onServiceFailure(arg0);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.d(TAG, new String(arg2));
				CheckoutDTO retModel = JSON.parseObject(new String(arg2), CheckoutDTO.class);
				switch (retModel.getResult()) {
				case ErrorCode.OK:
					SystemUtil.sendLocalBroadcast(CoreService.this, new Intent(Broadcast.CORE_CHECKOUT_SUCC));
					break;
				case ErrorCode.CHECKOUT_NEED_PAY:
					SystemUtil.sendLocalBroadcast(CoreService.this, new Intent(Broadcast.CORE_CHECKOUT_NEED_PAY));
					break;
				case ErrorCode.CHECKOUT_NO_CHECKIN:
					SystemUtil.sendLocalBroadcast(CoreService.this, new Intent(Broadcast.CORE_CHECKOUT_NO_CHECKIN));
					break;
				case ErrorCode.CHECKOUT_NO_SUCH_CARD:
					SystemUtil.sendLocalBroadcast(CoreService.this, new Intent(Broadcast.CORE_CHECKOUT_NO_SUCH_CARD));
					break;
				default:
					onServiceFailure(-1);
					break;
				}
			}
		});
	}

	public class CoreServiceBinder extends Binder {
		public void init() {
			CoreService.this.init();
		}

		public void heartbeat() {
			CoreService.this.heartbeat();
		}
	}

}
