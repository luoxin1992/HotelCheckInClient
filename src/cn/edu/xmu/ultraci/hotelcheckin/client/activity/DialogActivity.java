package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;

public class DialogActivity extends Activity {
	private static final String TAG = DialogActivity.class.getSimpleName();

	private ImageView ivType;
	private TextView tvMsg1;
	private TextView tvMsg2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		setContentView(R.layout.activity_dialog);

		ivType = (ImageView) findViewById(R.id.iv_type);
		tvMsg1 = (TextView) findViewById(R.id.tv_msg1);
		tvMsg2 = (TextView) findViewById(R.id.tv_msg2);

		ivType.setImageResource(getIntent().getIntExtra("resId", -1));
		tvMsg1.setText(getIntent().getStringExtra("msg1"));
		tvMsg2.setText(getIntent().getStringExtra("msg2"));
	}

	public void onDismiss(View v) {
		setResult(RESULT_OK);
		finish();
	}
}
