package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.widget.Toast;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.StringUtil;

/**
 * 刷卡界面
 *
 */
public class SwipeCardActivity extends BaseActivityWithoutBottom {

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	
	//记录从哪个界面跳转过来、以及卡验证通过后去往哪个界面
	private String from;
	private String to;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView(R.layout.activity_swipe_card);
		setTimeout(15);

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) };
		mTechLists = new String[][] { new String[] { NfcA.class.getName() } };

	}

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}

	private void enterToSelectTimeFromMenber() {
//		Intent intent = new Intent(SwipeCardActivity.this, SelectTimeActivity.class);
//		intent.putExtra("FromWhere", "member_checkin");
//		startActivity(intent);
//		finish();
	}

	private void enterTOSelectTimeFromTourist() {
//		Intent intent = new Intent(SwipeCardActivity.this, SelectTimeActivity.class);
//		intent.putExtra("FromWhere", "tourist_checkin");
//		startActivity(intent);
//		finish();
	}

	private void enterToVoice(String cardid) {
//		Intent intent = new Intent(this, LoginVoice.class);
//		intent.putExtra("cardid", cardid);
//		startActivity(intent);
//		finish();
	}

	private void enterToHome() {
		finish();
		Toast.makeText(SwipeCardActivity.this, "退房成功", Toast.LENGTH_SHORT).show();
	}

	private String getIntentStringExtra() {
		return getIntent().getStringExtra("FromWhere");
	}


	public void onNewIntent(Intent intent) {
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		byte[] id = tagFromIntent.getId();

		String fromWhere = getIntentStringExtra();
		if (fromWhere.equals("staff_login_out")) {
			enterToVoice(StringUtil.byte2HexString(id));
		} else if (fromWhere.equals("member_checkin")) { // 如果是从会员入住跳转过来的
			enterToSelectTimeFromMenber();
		} else if (fromWhere.equals("tourist_checkin")) { // 如果是从散客入住跳转过来的
			enterTOSelectTimeFromTourist();
		} else if (fromWhere.equals("repeatorder")) { // 如果是从续订跳转过来的
//			enterToSelectRepeatTime();
		} else if (fromWhere.equals("exitroom")) { // 如果是从退房跳转过来的
			enterToHome();
		}

	}

}
