package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Bluetooth;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Config;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.LogTemplate;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;

/**
 * 杂项服务<br>
 * <ul>
 * 检查蓝牙<br>
 * 检查NFC<br>
 * 播放音效<br>
 * 打印小票<br>
 * </ul>
 * 
 * @author LuoXin
 *
 */
public class MiscService extends Service {
	private static final String TAG = MiscService.class.getSimpleName();

	// 蓝牙打印机
	private BluetoothSocket mBluetoothsocket;
	private OutputStream mOutputStream;
	// 音效池
	private SoundPool mSoundPool;
	private SparseArray<Integer> mEffects;

	@Override
	public void onCreate() {
		super.onCreate();

		initSoundPool();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MiscServiceBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 销毁SoundPool
		if (mSoundPool != null) {
			mSoundPool.release();
		}
		// 断开打印机
		try {
			disconnectPrinter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查蓝牙适配器
	 */
	public void checkBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			if (adapter.isEnabled()) {
				Log.i(TAG, LogTemplate.MISC_BLUETOOTH_OK);
				SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_BLUETOOTH_OK));
			} else {
				Log.w(TAG, LogTemplate.MISC_BLUETOOTH_DISABLE);
				SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_BLUETOOTH_DISABLE));
			}
		} else {
			Log.e(TAG, LogTemplate.MISC_BLUETOOTH_NONSUPPORT);
			SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_BLUETOOTH_NONSUPPORT));
		}
	}

	/**
	 * 检查NFC适配器
	 */
	public void checkNFC() {
		NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
		if (adapter != null) {
			if (adapter.isEnabled()) {
				Log.i(TAG, LogTemplate.MISC_NFC_OK);
				SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_NFC_OK));
			} else {
				Log.w(TAG, LogTemplate.MISC_NFC_DISABLE);
				SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_NFC_DISABLE));
			}
		} else {
			Log.w(TAG, LogTemplate.MISC_NFC_NONSUPPORT);
			SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_NFC_NONSUPPORT));
		}
	}

	/**
	 * 初始化音效池
	 */
	private void initSoundPool() {
		mSoundPool = new SoundPool.Builder().build();
		mEffects = new SparseArray<>();
		mEffects.append(R.raw.beep, mSoundPool.load(this, R.raw.beep, 1));
		mEffects.append(R.raw.ding, mSoundPool.load(this, R.raw.ding, 1));
	}

	/**
	 * 播放音效
	 * 
	 * @param resId
	 *            音效资源ID
	 */
	public void playEffect(int resId) {
		mSoundPool.play(mEffects.get(resId), 1, 1, 0, 0, 1);
	}

	/**
	 * 连接打印机<br>
	 * 如有需要，先断开打印机的前一次连接
	 * 
	 * @throws IOException
	 *             打印机通讯错误
	 */
	private void connectPrinter() throws IOException {
		if (mBluetoothsocket != null || mOutputStream != null) {
			disconnectPrinter();
		}
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(Config.BT_MAC);
		mBluetoothsocket = device.createRfcommSocketToServiceRecord(Bluetooth.SPP_UUID);
		mBluetoothsocket.connect();
		mOutputStream = mBluetoothsocket.getOutputStream();
		// 连接成功后发送复位指令
		sendData2Printer(Bluetooth.PRINTER_CMD[0]);
		Log.i(TAG, LogTemplate.MISC_PRINTER_CONN_OK);
	}

	/**
	 * 断开打印机
	 * 
	 * @throws IOException
	 *             打印机通讯错误
	 */
	private void disconnectPrinter() throws IOException {
		if (mOutputStream != null) {
			mOutputStream.close();
			mOutputStream = null;
		}
		if (mBluetoothsocket != null) {
			mBluetoothsocket.close();
			mBluetoothsocket = null;
		}
		Log.i(TAG, LogTemplate.MISC_PRINTER_DISCONN_OK);
	}

	/**
	 * 向打印机发送数据
	 * 
	 * @param data
	 *            数据
	 * @throws IOException
	 *             打印机通讯错误
	 */
	private void sendData2Printer(byte[] data) throws IOException {
		mOutputStream.write(data);
	}

	/**
	 * 设置打印格式<br>
	 * 无效的设置视为恢复默认打印格式
	 * 
	 * @param size
	 *            字号
	 * @param type
	 *            加粗
	 * @param align
	 *            对齐
	 * @throws IOException
	 *             打印机通讯错误
	 */
	public void setPrintFormat(int size, int type, int align) throws IOException {
		switch (size) {
		case Bluetooth.FONT_1:
			sendData2Printer(Bluetooth.PRINTER_CMD[2]);
			sendData2Printer(Bluetooth.PRINTER_CMD[3]);
			break;
		case Bluetooth.FONT_2:
			sendData2Printer(Bluetooth.PRINTER_CMD[1]);
			sendData2Printer(Bluetooth.PRINTER_CMD[3]);
			break;
		case Bluetooth.FONT_3:
			sendData2Printer(Bluetooth.PRINTER_CMD[2]);
			sendData2Printer(Bluetooth.PRINTER_CMD[4]);
			break;
		case Bluetooth.FONT_4:
			sendData2Printer(Bluetooth.PRINTER_CMD[1]);
			sendData2Printer(Bluetooth.PRINTER_CMD[4]);
			break;
		default:
			sendData2Printer(Bluetooth.PRINTER_CMD[0]);
			return;
		}

		switch (type) {
		case Bluetooth.FONT_REGULAR:
			sendData2Printer(Bluetooth.PRINTER_CMD[5]);
			break;
		case Bluetooth.FONT_BOLD:
			sendData2Printer(Bluetooth.PRINTER_CMD[6]);
			break;
		default:
			sendData2Printer(Bluetooth.PRINTER_CMD[0]);
			return;
		}

		switch (align) {
		case Bluetooth.ALIGN_LEFT:
			sendData2Printer(Bluetooth.PRINTER_CMD[7]);
			break;
		case Bluetooth.ALIGN_CENTER:
			sendData2Printer(Bluetooth.PRINTER_CMD[8]);
			break;
		case Bluetooth.ALIGN_RIGHT:
			sendData2Printer(Bluetooth.PRINTER_CMD[9]);
			break;
		default:
			sendData2Printer(Bluetooth.PRINTER_CMD[0]);
			return;
		}
	}

	/**
	 * 打印文本
	 * 
	 * @param text
	 *            文本
	 * @throws IOException
	 *             打印机通讯错误
	 */
	public void printText(String text) throws IOException {
		// 要打印的文本后必须有换行符
		// 打印机才能清空缓冲区
		if (!text.endsWith("\n")) {
			text += "\n";
		}
		try {
			// 打印机只支持中文以GBK编码
			sendData2Printer(text.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void printTicket(final Bundle extras) {
		new Thread() {
			@Override
			public void run() {
				// 连接打印机
				try {
					connectPrinter();
					// 酒店名称
					setPrintFormat(Bluetooth.FONT_4, Bluetooth.FONT_BOLD, Bluetooth.ALIGN_CENTER);
					printText(SystemUtil.getPreferences(MiscService.this, "name"));
					// 打印时间
					setPrintFormat(Bluetooth.FONT_2, Bluetooth.FONT_REGULAR, Bluetooth.ALIGN_CENTER);
					printText(TimeUtil.formatDateTime(System.currentTimeMillis()));
					// 分割线
					setPrintFormat(0, 0, 0);
					printText("--------------------------------");
					// 客人信息（姓名、电话）
					setPrintFormat(Bluetooth.FONT_2, Bluetooth.FONT_REGULAR, Bluetooth.ALIGN_LEFT);
					printText("姓名：" + extras.getString("name"));
					printText("电话：" + extras.getString("mobile"));
					// 空行
					setPrintFormat(0, 0, 0);
					printText("\n");
					// 房间信息（房号、房型、房价、入住、预离）
					setPrintFormat(Bluetooth.FONT_2, Bluetooth.FONT_REGULAR, Bluetooth.ALIGN_LEFT);
					printText("房号：" + extras.getString("room"));
					printText("房型：" + extras.getString("type"));
					printText("房价：" + extras.getString("price"));
					printText("入住：" + extras.getString("checkin"));
					printText("预离：" + extras.getString("checkout"));
					// 分割线
					setPrintFormat(0, 0, 0);
					printText("--------------------------------");
					// 宾客须知
					setPrintFormat(Bluetooth.FONT_1, Bluetooth.FONT_REGULAR, Bluetooth.ALIGN_LEFT);
					printText("宾客须知：" + SystemUtil.getPreferences(MiscService.this, "notice"));
					// 谢谢惠顾
					setPrintFormat(Bluetooth.FONT_3, Bluetooth.FONT_REGULAR, Bluetooth.ALIGN_CENTER);
					printText("衷心感谢阁下的光临！");
					// 酒店信息
					setPrintFormat(Bluetooth.FONT_2, Bluetooth.FONT_REGULAR, Bluetooth.ALIGN_CENTER);
					printText(SystemUtil.getPreferences(MiscService.this, "address"));
					printText(SystemUtil.getPreferences(MiscService.this, "telephone"));
					// 空行(解决打印机最后出纸长度不足)
					setPrintFormat(0, 0, 0);
					printText("\n\n");
					// 断开打印机
					disconnectPrinter();
					SystemUtil.sendLocalBroadcast(MiscService.this, new Intent(Broadcast.MISC_PRINTER_OK));
				} catch (IOException e) {
					Log.e(TAG, String.format(LogTemplate.MISC_PRINTER_FAIL, e.getMessage()));
					SystemUtil.sendLocalBroadcast(MiscService.this, new Intent(Broadcast.MISC_PRINTER_FAIL));
				}
			};
		}.start();
	}

	public class MiscServiceBinder extends Binder {
		public void checkBluetooth() {
			MiscService.this.checkBluetooth();
		}

		public void checkNFC() {
			MiscService.this.checkNFC();
		}

		public void playEffect(int resId) {
			MiscService.this.playEffect(resId);
		}

		public void printTicket(Bundle extras) {
			MiscService.this.printTicket(extras);
		}
	}
}
