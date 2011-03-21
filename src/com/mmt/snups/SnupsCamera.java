package com.mmt.snups;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Log;

public class SnupsCamera extends Activity {

	public void onCreate(Bundle icicle) {
		Log.e("SnupsCamera", "onCreate");
		
		super.onCreate(icicle);
		
		try {
			Intent action = new Intent("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(action,1);
		} catch (Exception e) {
			Log.e(SnupsMission.class.getName(),"Error occured [" + e.getMessage() + "]");
		}

	}
	
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
    	Bitmap b = null;
    	try {
    		if (requestCode == 1) {
    			Log.i(SnupsMission.class.getName(),"resultCode is [" + resultCode + "]");
    			if (resultCode == RESULT_OK) {
	    			if (b != null) b.recycle();
	    			b = (Bitmap) data.getExtras().get("data");
	    			if (b != null) {
	    				StoreByteImage(this, b, 80);
	    				
	    				Intent intent=new Intent(getApplicationContext(),SnupsUpload.class);
	    		        startActivity(intent);
	    		        finish();
	    			}
    			}
    			else {
    				Intent intent=new Intent(getApplicationContext(),SnupsMission.class);
    		        startActivity(intent);
    		        finish();
    			}
    		}
    	}catch (Exception e) {
    		Log.e(SnupsMission.class.getName(),"onActivityResult Error [" + e.getMessage() + "]");
    	}
    	
    }
    
	public static boolean StoreByteImage(Context mContext, Bitmap myImage,
			int quality) {
		
		String filePath = "image.jpeg";
		
		Log.v("filePath:", filePath);

		try {			
			//create filestream
			FileOutputStream fos = mContext.openFileOutput(filePath,
														Context.MODE_WORLD_WRITEABLE);
			
			myImage.compress(CompressFormat.JPEG, quality, fos);
			
			fos.flush();

//			needed?:
//			fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}