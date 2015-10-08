package com.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends Activity {

    private String username = null;
    private String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");
    }

    public void stores(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),StoreListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        startActivity(intent_name);
    }

    public void restaurants(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),RestaurantListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        startActivity(intent_name);
    }

    public void offers(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),OfferListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        startActivity(intent_name);
    }

    public void events(View v)   {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(),EventsListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
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
