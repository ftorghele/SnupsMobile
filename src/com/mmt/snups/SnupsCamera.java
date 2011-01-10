package com.mmt.snups;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SnupsCamera extends Activity implements SurfaceHolder.Callback {

	static final int FOTO_MODE = 0;
	private static final String TAG = "CAMERA";
	Camera mCamera;	
	boolean mPreviewRunning = false;
	private Context mContext = this;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Log.e(TAG, "onCreate");

//		Bundle extras = getIntent().getExtras();

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.snups_camera);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {

				Intent mIntent = new Intent();

				StoreByteImage(mContext, imageData, 100);
				mCamera.startPreview();

				setResult(FOTO_MODE, mIntent);

                Intent intent=new Intent(getApplicationContext(),SnupsUpload.class);
                startActivity(intent);
                finish();

			}
		}
	};

	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");	
		mCamera = Camera.open();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		 p.setPreviewSize(w, h);// here w h are reversed
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	public void takePicture(View arg0) {

		mCamera.takePicture(null, mPictureCallback, mPictureCallback);

	}
	
	public static boolean StoreByteImage(Context mContext, byte[] imageData,
			int quality) {

        File sdImageMainDirectory = new File("/sdcard");
		FileOutputStream fileOutputStream = null;
		try {

			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 1;
			
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
					imageData.length,options);

			
			fileOutputStream = new FileOutputStream(
					sdImageMainDirectory.toString() +"/image.jpg");
							
  
			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
    
    
    
	
	
	
	
	
	
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.tutorial:
      	  TutorialDialog tutorialDialog = new TutorialDialog(this);
        	  tutorialDialog.show();
            return true;
        case R.id.quit:
      	  finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
	            Intent intent=new Intent(getApplicationContext(),SnupsMission.class);
	            startActivity(intent);
	            finish();
                return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    } 
}