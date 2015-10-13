package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OfferDetailActivity extends Activity {

    private String username = null;
    private String password = null;
    private TextView text = null;
    private TextView desc = null;
    private TextView valid = null;
    private ImageView icon = null;

    //ArrayList<OfferInfo> offers = new ArrayList<OfferInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        text = (TextView) findViewById(R.id.offertext);
        desc = (TextView) findViewById(R.id.offerdetail);
        icon = (ImageView) findViewById(R.id.icon);
        valid = (TextView) findViewById(R.id.offervalid);

        Intent t = getIntent();
        this.username = t.getStringExtra("username");
        this.password = t.getStringExtra("password");
        String offerTitle = t.getStringExtra("OfferTitle");
        String offerDesc = t.getStringExtra("OfferDesc");
        String timing = "Valid till : " + t.getStringExtra("OfferT");
        String url = t.getStringExtra("OfferUrl");
        text.setText(offerTitle);
        desc.setText(offerDesc);
        valid.setText(timing);

        icon = (ImageView) findViewById(R.id.icon);

        if (icon != null) {
            // new ImageDownloaderTask(mCardImageView).execute(cardUrl);
            Picasso.with(getBaseContext()).load(url).into(icon);

        }
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