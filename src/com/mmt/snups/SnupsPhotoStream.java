package com.mmt.snups;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
 
public class SnupsPhotoStream extends Activity {
    private WebView webview;
    private static final String TAG = "PhotoStream";
    private ProgressDialog progressBar;  
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
 
        setContentView(R.layout.snups_photo_stream);
 
        this.webview = (WebView)findViewById(R.id.webView);
 
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(false);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
 
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
 
        progressBar = ProgressDialog.show(SnupsPhotoStream.this, "Snups", "Loading...");
 
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }
 
            public void onPageFinished(WebView view, String url) {
                Log.v(TAG, "Finished loading URL: " +url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
 
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.v(TAG, "Error: " + description);
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        webview.loadUrl("http://franzonrails.multimediatechnology.at/mobilehome");
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
	            Intent intent=new Intent(getApplicationContext(),SnupsMenu.class);
	            startActivity(intent);
	            finish();
                return true; 
        } 
        return super.onKeyDown(keyCode, event); 
    }  
}