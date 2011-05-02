package com.mmt.snups;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SnupsError extends Activity {
      Button okButton;
      
      public void showForgottenPW(View view) {
      	Uri uriUrl = Uri.parse("http://franzonrails.multimediatechnology.at/users/password/new");
      	Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl); 
      	startActivity(launchBrowser);          
          finish();
      }
      
      @Override
      public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            System.gc();
            setContentView(R.layout.snups_error);
            TextView textview = (TextView) findViewById(R.id.error_msg);
            String errorMessage = getIntent().getStringExtra("ErrorMessage");
            textview.setText(errorMessage);
            okButton = (Button) findViewById(R.id.btn_ok);
            okButton.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View v) {
                        finish();
                  }
            });
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