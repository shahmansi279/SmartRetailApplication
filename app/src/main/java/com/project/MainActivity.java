package com.project;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);


		new CountDownTimer(5000,1000){
			@Override
			public void onTick(long millisUntilFinished){}

			@Override
			public void onFinish(){
				//set the new Content of your activity
				MainActivity.this.setContentView(R.layout.activity_main);
			}
		}.start();


	}

	public void login(View v)   {
		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(),LoginActivity.class);
		startActivity(intent_name);
	}
	
	public void register(View v)   {
		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(),RegisterActivity.class);
		startActivity(intent_name);
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}