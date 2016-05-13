package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

public class DialogActivity extends Activity {
	private static final String TAG = DialogActivity.class.getSimpleName();

	private ImageView ivType;
	private TextView tvMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		setContentView(R.layout.activity_dialog);

		ivType = (ImageView) findViewById(R.id.iv_type);
		tvMsg = (TextView) findViewById(R.id.tv_msg);

		ivType.setImageResource(getIntent().getIntExtra("resId", -1));
		tvMsg.setText(getIntent().getStringExtra("msg"));
	}
}
