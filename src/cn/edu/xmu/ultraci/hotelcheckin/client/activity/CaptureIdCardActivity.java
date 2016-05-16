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
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.Code;
import cn.edu.xmu.ultraci.hotelcheckin.client.constant.TTS;
import cn.edu.xmu.ultraci.hotelcheckin.client.util.SystemUtil;

/**
 * 拍摄身份证界面
 */
public class CaptureIdCardActivity extends BaseActivity {
	private static final String TAG = CaptureIdCardActivity.class.getSimpleName();

	private CaptureIdCardReceiver receiver;

	private String action;
	private Bundle extras;

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
	protected void onResume() {
		super.onResume();
		isForeground = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService();
		SystemUtil.unregisterLocalBroadcast(this, receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Code.CHANGE_UI && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
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
				}
			}
		});
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Broadcast.THIRDPARTY_SERIVCE_BOUND);
		filter.addAction(Broadcast.IFLYTEK_SYNTHESIS_OK);
		filter.addAction(Broadcast.CORE_SERVER_REQUEST_FAIL);
		filter.addAction(Broadcast.CORE_SERVER_PROCESS_FAIL);
		filter.addAction(Broadcast.CORE_FILE_NOT_FOUND);
		filter.addAction(Broadcast.CORE_FILE_UPLOAD_OK);
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
			showProcess();
			File file = new File(getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
					System.currentTimeMillis() + ".jpg");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				bos.write(arg0);
				bos.flush();
				bos.close();
				// 上传照片
				getCoreServiceBinder().upload("idcard", file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

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

			switch (intent.getAction()) {
			case Broadcast.THIRDPARTY_SERIVCE_BOUND:
				getThirdpartyServiceBinder().synthesicSpeech(TTS.CAPTURE_ID_CARD_HINT);
				break;
			case Broadcast.IFLYTEK_SYNTHESIS_OK:
				if (!isForeground) {
					dismissDialog();
				}
				if (isChangingUI) {
					isChangingUI = false;
					Intent newIntent;
					newIntent = new Intent(CaptureIdCardActivity.this, GuestActivity.class);
					newIntent.putExtra("action", action);
					newIntent.putExtra("extras", extras);
					startActivityForResult(newIntent, Code.CHANGE_UI);
				}
				break;
			case Broadcast.CORE_FILE_UPLOAD_OK:
				dismissProcess();
				isChangingUI = true;
				// 身份证照片上传成功
				extras.putString("idcard", intent.getStringExtra("filename"));
				// 弹出对话框
				getThirdpartyServiceBinder().synthesicSpeech(TTS.CAPTURE_ID_CARD_UPLOADED);
				showDialog(R.drawable.plain, TTS.CAPTURE_ID_CARD_UPLOADED);
				break;
			case Broadcast.CORE_FILE_NOT_FOUND:
			case Broadcast.CORE_SERVER_PROCESS_FAIL:
			case Broadcast.CORE_SERVER_REQUEST_FAIL:
				dismissProcess();
				// 内部错误
				getThirdpartyServiceBinder().synthesicSpeech(TTS.INTERNAL_ERROR);
				showDialog(R.drawable.warn, TTS.INTERNAL_ERROR);
				break;
			}
		}
	}
}
