package com.mmt.snups;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class SnupsUpload extends Activity {
    /** Called when the activity is first created. */
	private static final String TAG = "UPLOAD";
    Thread t;
    private SharedPreferences mPreferences;
    ProgressDialog dialog;
    File imageFile;
    
    private Context mContext = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.snups_upload);
        
        String internalStoragePath = mContext.getFilesDir().toString();
		String filePath = internalStoragePath + "/" + "image.jpeg";
        
        imageFile = new File(filePath);
        
        Log.v("imageFile:", imageFile.toString());
        Log.v("imagePath:", filePath);
        
        if(imageFile.exists()){
        	Log.v("file: ", "exists");
        	Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.toString());
        	ImageView myImage = (ImageView) findViewById(R.id.SnupsImage);
        	myImage.setImageBitmap(myBitmap);
        }
        else
        {
        	Log.v("file:", "doesn't exist");
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
          switch (id) {
                case 0: {
                      dialog = new ProgressDialog(this);
                      dialog.setMessage("Please wait while uploading...");
                      dialog.setIndeterminate(true);
                      dialog.setCancelable(true);
                      return dialog;
                }
          }
          return null;
    }
    private Handler uploadHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
                String loginmsg=(String)msg.obj;
                if(loginmsg.equals("SUCCESS")) {
                    removeDialog(0);
                    Intent intent=new Intent(getApplicationContext(),SnupsMission.class);
                    startActivity(intent);
                    finish();
                } else {
	                Intent intent = new Intent(getApplicationContext(), SnupsError.class);
	                intent.putExtra("ErrorMessage", "Something went wrong");
	                startActivity(intent);
              	  	removeDialog(0);
                }
          }
    };
    public void tryUpload() {
          Log.v(TAG, "Trying to upload");
          
          DefaultHttpClient client = new DefaultHttpClient();
          HttpPost httppost = new HttpPost("http://franzonrails.multimediatechnology.at/images");
          
          try {
        	    MultipartEntity p_entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        	  
        	    mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        	    Charset chars = Charset.forName("UTF-8");

        	    p_entity.addPart("image[image_data]", new FileBody(imageFile));
        	    p_entity.addPart("image[username]", new StringBody(mPreferences.getString("UserName", ""), chars));
        	    p_entity.addPart("image[password]", new StringBody(mPreferences.getString("PassWord", ""), chars));
        	    p_entity.addPart("image[mission_id]", new StringBody(mPreferences.getString("MissionId", ""), chars));

        	    httppost.setEntity(p_entity);
   
                HttpResponse response = client.execute(httppost);
                Log.v(TAG, response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();
                
                if (entity != null) {

              	  InputStream instream = entity.getContent();
              	  BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
              	  StringBuilder sb = new StringBuilder();

              	  String line = null;
              	  
              	  while ((line = reader.readLine()) != null) {
              		  sb.append(line + "\n");
              	  }

              	  String result = sb.toString();

              	  Log.v(TAG,result);

              	  instream.close();

              	  JSONObject json=new JSONObject(result);

              	  String status = json.get("status").toString();

	                  if (status.equals("SUCCESS")) {
	                        Message myMessage=new Message();
	                        myMessage.obj="SUCCESS";
	                        uploadHandler.sendMessage(myMessage);
	                  } else {
	                	    Message myMessage=new Message();
	                        myMessage.obj="ERROR";
	                        uploadHandler.sendMessage(myMessage);
	                  }
                }
  		} catch (IOException e) {
  			Log.e("ouch", "!!! IOException " + e.getMessage());
  		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }


    
    
    
    
    public void takeNewImage(View view) {
        Intent intent=new Intent(getApplicationContext(),SnupsCamera.class);
        startActivity(intent);
        finish();
    }  

    public void uploadImage(View view) {
        showDialog(0);
        t=new Thread() {
              public void run() {
                    tryUpload();
              }
        };
        t.start();
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
	            Intent intent=new Intent(getApplicationContext(),SnupsCamera.class);
	            startActivity(intent);
	            finish();
                return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    }  
}