package com.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void stores(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),StoreListActivity.class);
        startActivity(intent_name);
    }

    public void restaurants(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),RestaurantListActivity.class);
        startActivity(intent_name);
    }

    public void offers(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),OfferListActivity.class);
        startActivity(intent_name);
    }

    public void events(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),EventsListActivity.class);
        startActivity(intent_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
