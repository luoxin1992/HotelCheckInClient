package cn.edu.xmu.ultraci.hotelcheckin.client.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import cn.edu.xmu.ultraci.hotelcheckin.client.R;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Broadcast;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.TimeUtil;

/**
 * 拍摄身份证界面
 */
public class CaptureIdCardActivity extends BaseActivity {
	private static final String TAG = CaptureIdCardActivity.class.getSimpleName();

	private CaptureIdCardReceiver receiver;

	// Camera object
	private Camera mCamera;
	// Preview surface handle for callback
	private SurfaceHolder surfaceHolder;
	// Note if preview windows is on.
	private boolean previewing;
	private SurfaceView activity_captureidcard_sv_photo;
	int mCurrentCamIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	protected void onPause() {
		super.onPause();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		setContent(true, getTitle().toString(), true, 60, R.layout.activity_capture_id_card, false);

		activity_captureidcard_sv_photo = (SurfaceView) findViewById(R.id.activity_captureidcard_sv_photo);
		surfaceHolder = activity_captureidcard_sv_photo.getHolder();
		surfaceHolder.addCallback(new SurfaceViewCallback());
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		activity_captureidcard_sv_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 单击屏幕拍照，并保存图片文件，并跳转activity
				if (previewing) {
					mCamera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
					// 跳转到拍摄证件照界面
					// Intent intent = new Intent(CaptureIdCardActivity.this,
					// GuestActivity.class);
					//
					// String enterTime =
					// getIntent().getStringExtra("enterTime");
					// String exitTime = getIntent().getStringExtra("exitTime");
					// String selected_room_name =
					// getIntent().getStringExtra("selected_room_name");
					// String selected_floor_name =
					// getIntent().getStringExtra("selected_floor_name");
					// Double room_price =
					// getIntent().getDoubleExtra("room_price", 0);
					// String room_type =
					// getIntent().getStringExtra("room_type");
					// String user_phone =
					// getIntent().getStringExtra("user_phone");
					//
					// intent.putExtra("FromWhere", "tourist_checkin");
					// intent.putExtra("enterTime", enterTime);
					// intent.putExtra("exitTime", exitTime);
					// intent.putExtra("selected_floor_name",
					// selected_floor_name);
					// intent.putExtra("selected_room_name",
					// selected_room_name);
					// intent.putExtra("room_price", room_price);
					// intent.putExtra("room_type", room_type);
					// intent.putExtra("user_phone", user_phone);
					// startActivity(intent);
					// finish();
				}
			}
		});
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.CORE_FILE_UPLOAD_OK);
		filter.addAction(Broadcast.CORE_FILE_NOT_FOUND);
		receiver = new CaptureIdCardReceiver();
		SystemUtil.registerLocalBroadcast(this, receiver, filter);
	}

	Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};

	Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

		}
	};

	Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			//保存照片
			// String fileName =
			// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
			// + File.separator + "PicTest_" + System.currentTimeMillis() +
			// ".jpg";
			File file = new File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
					TimeUtil.getCurrentTime() + ".jpg");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}

			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				bos.write(arg0);
				bos.flush();
				bos.close();
				scanFileToPhotoAlbum(file.getAbsolutePath());
//				Toast.makeText(CaptureIdCardActivity.this, "[Test] Photo take and store in" + file.toString(),
//						Toast.LENGTH_LONG).show();

				//上传照片
				getCoreServiceBinder().upload("idcard", file.getAbsolutePath());
			} catch (Exception e) {
				Toast.makeText(CaptureIdCardActivity.this, "Picture Failed" + e.toString(), Toast.LENGTH_LONG).show();
			}
		};
	};

	public void scanFileToPhotoAlbum(String path) {

		MediaScannerConnection.scanFile(CaptureIdCardActivity.this, new String[] { path }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					public void onScanCompleted(String path, Uri uri) {
						Log.i("TAG", "Finished scanning " + path);
					}
				});
	}

	private final class SurfaceViewCallback implements SurfaceHolder.Callback {
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
			if (previewing) {
				mCamera.stopPreview();
				previewing = false;
			}

			try {
				mCamera.setPreviewDisplay(arg0);
				mCamera.startPreview();
				previewing = true;
				setCameraDisplayOrientation(CaptureIdCardActivity.this, mCurrentCamIndex, mCamera);
			} catch (Exception e) {
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// mCamera = Camera.open();
			// change to front camera
			mCamera = openFrontFacingCameraGingerbread();
			// get Camera parameters
			Camera.Parameters params = mCamera.getParameters();

			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				// Autofocus mode is supported
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			previewing = false;
		}
	}

	private Camera openFrontFacingCameraGingerbread() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				try {
					cam = Camera.open(camIdx);
					mCurrentCamIndex = camIdx;
				} catch (RuntimeException e) {
					Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}
		return cam;
	}

	// 根据横竖屏自动调节preview方向，Starting from API level 14, this method can be called
	// when preview is active.
	private static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		// degrees the angle that the picture will be rotated clockwise. Valid
		// values are 0, 90, 180, and 270.
		// The starting position is 0 (landscape).
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else {
			// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	class CaptureIdCardReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent newIntent;
			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				// 播放提示音
				getThirdpartyServiceBinder().synthesicSpeech(TTS.CAPTURE_ID_CARD_HINT);
				break;
			case Broadcast.CORE_FILE_UPLOAD_OK:
				newIntent = new Intent(CaptureIdCardActivity.this, GuestActivity.class);
				newIntent.putExtra("filename", intent.getStringExtra("filename"));
				startActivity(newIntent);
				finish();
				break;
			case Broadcast.CORE_FILE_NOT_FOUND:
				break;
			}
		}

	}
}
