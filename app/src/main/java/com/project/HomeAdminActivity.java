package com.project;

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
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeAdminActivity extends Activity {
    private String username = null;
    private String password = null;

    private String placeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");

        getAdminDetails();


    }


    public void getAdminDetails() {


        String url = "https://smartretailapp.appspot.com/smapp/default/getAdminDetails"

                + "?uemail="
                + username.toString()
                + "&password="
                + password.toString();

        Log.v("Status", "ul " + url);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            DownloadWebPageTask task = new DownloadWebPageTask();
            task.execute(url);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Network Connection not available", Toast.LENGTH_SHORT)
                    .show();
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

    public void viewBeacons(View v) {

        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(), BeaconListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        intent_name.putExtra("placeid", placeId);

        startActivity(intent_name);

    }

    public void viewOffers(View v) {

        Toast.makeText(getApplicationContext(), placeId,
                Toast.LENGTH_SHORT).show();

        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(), ManageOfferListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        intent_name.putExtra("placeid", placeId);


        startActivity(intent_name);

    }


    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        // public String username;

        protected String doInBackground(String... urls) {

            String response = "";


            int code = 0;


            try {

                response = ConnectionUtil.downloadUrl(urls[0]);
                Log.v("res", response);

                if (response != "error") {


                    JSONArray jso = new JSONArray(response);

                    for (int i = 0; i < jso.length(); i++) {
                        StoreInfo c = new StoreInfo();
                        JSONObject ob = (JSONObject) jso.get(i);
                        placeId = ob.get("storeid").toString();


                    }
                    response = "success";
                } else {

                    response = "No Store for the Admin";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;


        }

        @Override
        protected void onPostExecute(String result) {

            if (result != "error") {
                super.onPostExecute(result);

            } else {

                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }







}
