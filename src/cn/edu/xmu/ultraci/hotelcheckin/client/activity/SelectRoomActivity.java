package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.StatusDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.StatusDTO.Status;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.TypeDTO;
import cn.edu.xmu.ultraci.hotelcheckin.client.dto.TypeDTO.Type;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 选房间界面
 */
public class SelectRoomActivity extends BaseActivity implements OnItemClickListener {
	private static final String TAG = SelectRoomActivity.class.getSimpleName();

	private SelectRoomReceiver receiver;

	private String action;
	private Bundle extras;

	private ListView lvFilter;
	private GridView gvStatus;
	private SimpleAdapter lvAdapter;
	private SimpleAdapter gvAdapter;
	private List<Map<String, Object>> lvItemLst;
	private List<Map<String, Object>> gvItemLst;

	private int eventCounter = 0;
	private List<Type> typeLst;
	private List<Status> statusLst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		action = getIntent().getStringExtra("action");
		extras = getIntent().getBundleExtra("extras");

		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();

		registerReceiver();
		bindCoreService();
		bindThirdpartyService();
	}

	@Override
	protected void onStop() {
		super.onStop();

		SystemUtil.unregisterLocalBroadcast(this, receiver);
		unbindService();
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_SERIVCE_BOUND);
		filter.addAction(Broadcast.CORE_QUERY_TYPE_OK);
		filter.addAction(Broadcast.CORE_QUERY_STATUS_OK);
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		receiver = new SelectRoomReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	public void initView() {
		setContent(true, getTitle().toString(), true, 60, R.layout.activity_select_room, false);

		lvFilter = (ListView) findViewById(R.id.lv_filter);
		gvStatus = (GridView) findViewById(R.id.gv_status);
		lvFilter.setOnItemClickListener(this);
		gvStatus.setOnItemClickListener(this);
	}

	public boolean isQueryOk() {
		if (++eventCounter >= 2) {
			dismissProcess();
			return true;
		} else {
			return false;
		}
	}

	public void parseFloorDTO(TypeDTO retModel) {
		// 存起来
		typeLst = retModel.getTypes();
		// 添加CheckBox
		lvItemLst = new ArrayList<Map<String, Object>>();
		for (Type type : typeLst) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", type.getId());
			item.put("name", type.getName());
			item.put("price", type.getPrice());
			item.put("checked", false);
			lvItemLst.add(item);
		}
		initListViewAdapter();
	}

	public void parseStatusDTO(StatusDTO retModel) {
		// 存起来
		statusLst = retModel.getStatuses();
		// 添加Item
		gvItemLst = new ArrayList<Map<String, Object>>();
		for (Status status : statusLst) {
			Map<String, Object> item = new HashMap<String, Object>();
			if (status.getAvailable() == 0) {
				item.put("image", R.drawable.room_normal);
			} else if (status.getAvailable() == 1) {
				item.put("image", R.drawable.room_sold);
			} else {
				break;
			}
			item.put("id", status.getId());
			item.put("name", status.getName());
			item.put("price", "￥" + status.getType() * 100);
			gvItemLst.add(item);
		}
		initGridViewAdapter();
	}

	public void initGridViewAdapter() {
		if (gvAdapter == null) {
			gvAdapter = new SimpleAdapter(this, gvItemLst, R.layout.activity_select_room_gridview_item,
					new String[] { "image", "name", "price" }, new int[] { R.id.iv_room, R.id.tv_name, R.id.tv_price });
			gvStatus.setAdapter(gvAdapter);
		} else {
			gvAdapter.notifyDataSetChanged();
		}
	}

	public void initListViewAdapter() {
		if (lvAdapter == null) {
			lvAdapter = new SimpleAdapter(this, lvItemLst, R.layout.activity_select_room_listview_item,
					new String[] { "name" }, new int[] { R.id.cb_type });
			lvFilter.setAdapter(lvAdapter);
		} else {
			lvAdapter.notifyDataSetChanged();
		}
	}

	public String makeFilterStr() {
		StringBuffer sb = new StringBuffer();
		for (Map<String, Object> item : lvItemLst) {
			if ((Boolean) item.get("checked")) {
				sb.append(item.get("id"));
				sb.append("|");
			}
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.lv_filter:
			System.out.println("ListView第" + position + "项被点击。");
			((CheckBox) view).toggle();
			lvItemLst.get(position).put("checked", ((CheckBox) view).isChecked());
			makeFilterStr();
			break;
		case R.id.gv_status:
			System.out.println("GridView第" + position + "项被点击。");
			gvItemLst.get(position).put("image", R.drawable.room_selected);
			initGridViewAdapter();
			break;
		}
	}

	class SelectRoomReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.SELECT_ROOM_HINT);
				break;
			case Broadcast.CORE_SERIVCE_BOUND:
				showProcess();
				getCoreServiceBinder().type();
				getCoreServiceBinder().status("", "");
				break;
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
				break;
			case Broadcast.CORE_QUERY_TYPE_OK:
				break;
			case Broadcast.CORE_QUERY_STATUS_OK:
				break;
			}
		}
	}

}
