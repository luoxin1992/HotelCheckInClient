package cn.edu.xmu.ultraci.hotelcheckin.client.service;

/**
 * 语音相关服务<br>
 * <ul>
 * 语音合成(讯飞API)<br>
 * 声纹验证(讯飞API)<br>
 * 音效播放(原生API)
 * </ul>
 * 
 * @author LuoXin
 *
 */
public interface IVoiceService {

	/**
	 * 获取声纹验证所需的8位数字密码
	 * 
	 * @return 获取的密码
	 */
	public String generatePassword();

	/**
	 * 验证指定用户的声纹(异步方法)
	 * 
	 * @param uid
	 *            待验证的用户ID
	 * @param pwd
	 *            待验证的密码
	 */
	public void verifyVoiceprint(String uid, String pwd);

	/**
	 * 将给定文本转换成语音(TTS)并播放(异步方法)
	 * 
	 * @param text
	 *            待合成的内容
	 */
	public void speechSynthesis(String text);

	/**
	 * 播放指定的音效
	 * 
	 * @param sound
	 *            音效值
	 */
	public void playEffect(int sound);
}
