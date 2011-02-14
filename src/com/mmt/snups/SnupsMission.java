package com.mmt.snups;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SnupsMission extends Activity {
    /** Called when the activity is first created. */
	private static final String TAG = "Mission";
	private SharedPreferences mPreferences;
    ProgressDialog dialog;
    Button getMission;
    Thread t;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snups_mission);
        
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        
        showDialog(0);
        t=new Thread() {
              public void run() {
                    tryGetMission();
              }
        };
        t.start();
        
        getMission = (Button) findViewById(R.id.btn_new_mission);
        getMission.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                    showDialog(0);
                    t=new Thread() {
                          public void run() {
                                tryGetMission();
                          }
                    };
                    t.start();
              }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
          switch (id) {
                case 0: {
                      dialog = new ProgressDialog(this);
                      dialog.setMessage("Getting new Mission...");
                      dialog.setIndeterminate(true);
                      dialog.setCancelable(true);
                      return dialog;
                }
          }
          return null;
    }
    private Handler getMissionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
              String loginmsg=(String)msg.obj;
              
              if(loginmsg.equals("SUCCESS")) {
                    removeDialog(0);
                    loadSavedMission();
              } else {
	                Intent intent = new Intent(getApplicationContext(), SnupsError.class);
	                intent.putExtra("ErrorMessage", "Something went wrong");
	                startActivity(intent);
              	  	removeDialog(0);
              }
        }
  };
	private void tryGetMission() {
        Log.v(TAG, "Trying to get Mission");
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://snups.multimediatechnology.at/mobile/mission");

        try {
            HttpResponse response = client.execute(httpget);
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
                  SharedPreferences.Editor editor=mPreferences.edit();
                  editor.putString("MissionName", json.get("title").toString());
                  editor.putString("MissionId", json.get("id").toString());
                  editor.commit();
	              Message myMessage=new Message();
	              myMessage.obj="SUCCESS";
	              getMissionHandler.sendMessage(myMessage);
	          } else {
	        	  Message myMessage=new Message();
	              myMessage.obj="ERROR";
	              getMissionHandler.sendMessage(myMessage);
	          }
          
            }
		} catch (IOException e) {
			Log.e("ouch", "!!! IOException " + e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    
    private void loadSavedMission() {
  	  if(mPreferences.contains("MissionName") == true) {
  	  	String savedMissionName = mPreferences.getString("MissionName", "");
  	  	TextView txt_current_mission = (TextView) findViewById(R.id.txt_current_mission);
  	  	txt_current_mission.setText(savedMissionName);
  	  }
    }
    
    public void showCamera(View view) {
        Intent intent=new Intent(getApplicationContext(),SnupsCamera.class);
        startActivity(intent);
        finish();
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
                // your code here 
                return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    } 
}