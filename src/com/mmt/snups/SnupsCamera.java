package com.mmt.snups;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class SnupsCamera extends Activity {
	Uri outputFileUri;

	public void onCreate(Bundle icicle) {
		Log.e("SnupsCamera", "onCreate");
		
		super.onCreate(icicle);
		
		//kick off the camera application
		try {
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            
            File imageFile = new File(Environment.getExternalStorageDirectory(), "snups.jpg");
            outputFileUri = Uri.fromFile(imageFile);
            
//            outputFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
			
            Log.v("external filepath:", Environment.getExternalStorageDirectory().toString());
			Log.v("uri-pfad:", outputFileUri.getPath());
            
			//we have to tell the camera-app where to store the image
			//otherwise we just get a preview-image in onActivityResult()
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            
			startActivityForResult(intent,1);
		} catch (Exception e) {
			Log.e(SnupsMission.class.getName(),"Error occured [" + e.getMessage() + "]");
		}

	}
	
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
    	try {
    		if (requestCode == 1 && resultCode == RESULT_OK) {
    			Uri selectedImage = outputFileUri;
                getContentResolver().notifyChange(selectedImage, null);
                ContentResolver cr = getContentResolver();
                
                Bitmap bitmap;
                try {
                     bitmap = android.provider.MediaStore.Images.Media
                     .getBitmap(cr, selectedImage);
                     
                     StoreByteImage(this, bitmap, 80);
                     
                     Intent intent=new Intent(getApplicationContext(),SnupsUpload.class);
                     startActivity(intent);
                     finish();
                     
//                     Log.v("bitmap:", bitmap.toString());

//                    Toast.makeText(this, selectedImage.toString(),
//                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
    		}
			else {
				Log.i(SnupsMission.class.getName(),"resultCode is [" + resultCode + "]");
				Intent intent=new Intent(getApplicationContext(),SnupsMission.class);
		        startActivity(intent);
		        finish();
			}
    	}
    	catch (Exception e) {
    		Log.e(SnupsMission.class.getName(),"onActivityResult Error [" + e.getMessage() + "]");
    	}
    	
    }
    
	public static boolean StoreByteImage(Context mContext, Bitmap myImage,
			int quality) {
		
		String filePath = "snupsImage.jpeg";
		
		Log.v("filePath:", filePath);

		try {			
			//create filestream
			FileOutputStream fos = mContext.openFileOutput(filePath,
														Context.MODE_WORLD_WRITEABLE);
			
			myImage.compress(CompressFormat.JPEG, quality, fos);
			
//			needed?:			
//			fos.flush();

//			needed?:
			fos.close();

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