package cn.edu.xmu.ultraci.hotelcheckin.client.service;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Bluetooth;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Config;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.LogTemplate;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 杂项服务<br>
 * <ul>
 * 检查NFC<br>
 * 检查蓝牙<br>
 * 打印小票<br>
 * 播放音效<br>
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
		initBluetooth();
		initNFC();
		connectPrinter();
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
		disconnectPrinter();
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
	 * 初始化蓝牙适配器
	 */
	private void initBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			if (adapter.isEnabled()) {
				Log.i(TAG, LogTemplate.MISC_BLUETOOTH_OK);
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
	 * 初始化NFC适配器
	 */
	private void initNFC() {
		NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
		if (adapter != null) {
			if (adapter.isEnabled()) {
				Log.i(TAG, LogTemplate.MISC_NFC_OK);
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
	 * 连接打印机
	 */
	private void connectPrinter() {
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(Config.BT_MAC);
		try {
			mBluetoothsocket = device.createRfcommSocketToServiceRecord(Bluetooth.SPP_UUID);
			mBluetoothsocket.connect();
			mOutputStream = mBluetoothsocket.getOutputStream();
			// 连接成功后发送复位指令
			sendData2Printer(Bluetooth.PRINTER_CMD[0]);
			Log.i(TAG, LogTemplate.MISC_PRINTER_CONN_OK);
		} catch (IOException e) {
			SystemUtil.sendLocalBroadcast(this, new Intent(Broadcast.MISC_PRINTER_CONN_FAIL));
			Log.e(TAG, String.format(LogTemplate.MISC_PRINTER_CONN_FAIL, e.getMessage()));
			disconnectPrinter();
		}
	}

	/**
	 * 断开打印机
	 */
	private void disconnectPrinter() {
		try {
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if (mBluetoothsocket != null) {
				mBluetoothsocket.close();
				mBluetoothsocket = null;
			}
			Log.i(TAG, LogTemplate.MISC_PRINTER_DISCONN_OK);
		} catch (IOException e) {
			Log.e(TAG, String.format(LogTemplate.MISC_PRINTER_DISCONN_FAIL, e.getMessage()));
		}
	}

	/**
	 * 向打印机发送数据
	 * 
	 * @param data
	 *            数据
	 */
	private void sendData2Printer(final byte[] data) {
		new Thread() {
			public void run() {
				try {
					mOutputStream.write(data);
				} catch (IOException e) {
					Log.e(TAG, String.format(LogTemplate.MISC_PRINTER_SEND_FAIL, e.getMessage()));
					// 与打印机通讯异常时自动重连
					disconnectPrinter();
					connectPrinter();
				}
			}
		}.start();
	}

	/**
	 * 向打印机发送数据
	 * 
	 * @param text
	 *            数据
	 */
	private void sendData2Printer(String text) {
		// 要打印的文本后必须有换行符
		// 打印机才能清空缓冲区
		if (!text.endsWith("\n")) {
			text += "\n";
		}
		sendData2Printer(text.getBytes());
	}

	/**
	 * 以格式1打印<br>
	 * 
	 * @param text
	 *            要打印的文本
	 */
	private void printInType1(String text) {

	}

	/**
	 * 以格式2打印<br>
	 * 
	 * @param text
	 *            要打印的文本
	 */
	private void printInType2(String text) {

	}

	/**
	 * 以格式3打印<br>
	 * 
	 * @param text
	 *            要打印的文本
	 */
	private void printInType3(String text) {

	}

	/**
	 * 以格式4打印<br>
	 * 
	 * @param text
	 *            要打印的文本
	 */
	private void printInType4(String text) {

	}

	/**
	 * 以格式5打印<br>
	 * 
	 * @param text
	 *            要打印的文本
	 */
	private void printInType5(String text) {

	}

	/**
	 * 以格式6打印<br>
	 * 
	 * @param text
	 *            要打印的文本
	 */
	private void printInType6(String text) {

	}

	public void playEffect(int resId) {
		mSoundPool.play(mEffects.get(resId), 1, 1, 0, 0, 1);
	}

	public void printTicket() {

	}

	public class MiscServiceBinder extends Binder {
		public void playEffect(int resId) {
			MiscService.this.playEffect(resId);
		}

		public void printTicket() {

		}
	}

}
