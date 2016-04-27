package cn.edu.xmu.ultraci.hotelcheckin.client.constant;

import java.util.UUID;

/**
 * 蓝牙打印机协议&常量
 * 
 * @author LuoXin
 *
 */
public class Bluetooth {
	public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final byte[][] PRINTER_CMD = { { 0x1b, 0x40 }, // 0.复位打印机
			{ 0x1b, 0x4d, 0x00 }, // 1.标准ASCII字体
			{ 0x1b, 0x4d, 0x01 }, // 2.压缩ASCII字体
			{ 0x1d, 0x21, 0x00 }, // 3.字体不放大
			{ 0x1d, 0x21, 0x11 }, // 4.宽高加倍
			{ 0x1b, 0x45, 0x00 }, // 5.取消加粗模式
			{ 0x1b, 0x45, 0x01 }, // 6.选择加粗模式
			{ 0x1b, 0x61, 0x00 }, // 7.左对齐
			{ 0x1b, 0x61, 0x01 }, // 8.居中
			{ 0x1b, 0x61, 0x02 }, // 9.右对齐
	};

	// 压缩ASCII字体、字体不放大
	public static final int FONT_1 = 1;
	// 标准ASCII字体、字体不放大
	public static final int FONT_2 = 2;
	// 压缩ASCII字体、宽高加倍
	public static final int FONT_3 = 3;
	// 标准ASCII字体、宽高加倍
	public static final int FONT_4 = 4;

	// 不加粗
	public static final int FONT_REGULAR = 1;
	// 加粗
	public static final int FONT_BOLD = 2;

	// 左对齐
	public static final int ALIGN_LEFT = 1;
	// 居中对齐
	public static final int ALIGN_CENTER = 2;
	// 右对齐
	public static final int ALIGN_RIGHT = 3;

}
