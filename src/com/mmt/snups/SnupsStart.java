package com.mmt.snups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SnupsStart extends Activity {
    /** Called when the activity is first created. */
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snups_start);
    }
    
    public void showTutorial(View view) {
    	TutorialDialog tutorialDialog = new TutorialDialog(this);
    	tutorialDialog.show();
    }
    
    public void showLogin(View view) {
        Intent intent=new Intent(getApplicationContext(),SnupsLogin.class);
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