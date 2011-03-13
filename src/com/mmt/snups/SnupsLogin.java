package com.mmt.snups;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
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
import android.widget.EditText;

public class SnupsLogin extends Activity {
      /** Called when the activity is first created. */
      private static final String TAG = "Login";
      Button signin;
      String loginmessage = null;
      Thread t;
      private SharedPreferences mPreferences;
      ProgressDialog dialog;
      @Override
      public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.snups_login);
            
            mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
            insertLoginInfo();
   
                  signin = (Button) findViewById(R.id.btn_sign_in);
                  signin.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                              showDialog(0);
                              t=new Thread() {
                                    public void run() {
                                          tryLogin();
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
                        dialog.setMessage("Please wait while connecting...");
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        return dialog;
                  }
            }
            return null;
      }
      private Handler loginHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                  String loginmsg=(String)msg.obj;
                  if(loginmsg.equals("SUCCESS")) {
                        removeDialog(0);
                        Intent intent = new Intent(getApplicationContext(), SnupsMenu.class);
                        startActivity(intent);
                        finish();
                  } else {
	                    Intent intent = new Intent(getApplicationContext(), SnupsError.class);
	                    intent.putExtra("ErrorMessage", "Wrong username or password");
	                    startActivity(intent);
	                    removeDialog(0);
                  }
            }
      };
      public void tryLogin() {
            Log.v(TAG, "Trying to Login");
            EditText etxt_user = (EditText) findViewById(R.id.txt_username);
            EditText etxt_pass = (EditText) findViewById(R.id.txt_password);
            String username = etxt_user.getText().toString();
            String password = etxt_pass.getText().toString();
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://franzonrails.multimediatechnology.at/mobile/login");
            List<BasicNameValuePair> userData = new ArrayList<BasicNameValuePair>();
            userData.add(new BasicNameValuePair("username", username));
            userData.add(new BasicNameValuePair("password", password));
            try {
                  UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(userData,HTTP.UTF_8);
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
	                        // Store the username and password in SharedPreferences after the successful login
	                        SharedPreferences.Editor editor=mPreferences.edit();
	                        editor.putString("UserName", username);
	                        editor.putString("PassWord", password);
	                        editor.commit();
	                        Message myMessage=new Message();
	                        myMessage.obj="SUCCESS";
	                        loginHandler.sendMessage(myMessage);
	                  } else if(status.equals("ERROR")) {
	                	    Message myMessage=new Message();
	                        myMessage.obj="ERROR";
	                        loginHandler.sendMessage(myMessage);
	                  } else {
	                	    Message myMessage=new Message();
	                        myMessage.obj="OTHER";
	                        loginHandler.sendMessage(myMessage);
	                  }
                  }
    		} catch (IOException e) {
    			Log.e("ouch", "!!! IOException " + e.getMessage());
    		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      }
     
      private void insertLoginInfo() {
    	  if(mPreferences.contains("UserName") == true) {
    	  	String savedUsername = mPreferences.getString("UserName", "");
    	  	EditText etxt_user = (EditText) findViewById(R.id.txt_username);
    	  	etxt_user.setText(savedUsername);
    	  }
    	  if(mPreferences.contains("PassWord") == true) {
    	  	String savedUserPassword = mPreferences.getString("PassWord", "");
    	  	EditText etxt_pass = (EditText) findViewById(R.id.txt_password);
    	  	etxt_pass.setText(savedUserPassword);
    	  }
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
	            Intent intent=new Intent(getApplicationContext(),SnupsStart.class);
	            startActivity(intent);
	            finish();
                return true; 
          } 
          return super.onKeyDown(keyCode, event); 
      } 
}