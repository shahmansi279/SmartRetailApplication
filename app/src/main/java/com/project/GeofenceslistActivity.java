package com.project;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GeofenceslistActivity extends ActionBarActivity {

    public ListView mListView;
    public EditText editText;
    private String username = null;
    private String password = null;
    public CustomListAdapter adapter;
    ArrayList<StoreInfo> stores = new ArrayList<StoreInfo>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofenceslist);

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");

        ActionBar actionBar = getActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_view);
        EditText search = (EditText) actionBar.getCustomView().findViewById(R.id.searchfield);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                adapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        mListView = (ListView) findViewById(android.R.id.list);

        try {
            getSearchResults();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void getSearchResults() throws IOException {

        String url = "http://smartretailapp.appspot.com/smartapp/default/getstores?uemail=" + this.username
                + "&password=" + this.password;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            DownloadWebPageTask task = new DownloadWebPageTask();
            task.execute(url);
        } else {

        }

    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            String response = "";

            int code = 0;


            try {

                response = ConnectionUtil.downloadUrl(urls[0]);


                if (response != "error") {


                    JSONArray jso = new JSONArray(response);


                    for (int i = 0; i < jso.length(); i++) {
                        StoreInfo c = new StoreInfo();
                        JSONObject ob = (JSONObject) jso.get(i);
                        c.setStoreName(ob.get("name").toString());
                        c.setStoreDesc(ob.get("store_desc").toString());
                        c.setStoreUrl("http://smartretailapp.appspot.com/smartapp/default/download/"
                                + ob.get("pl_img_url").toString());

                        c.setStoreId(ob.get("id_p").toString());
                        c.setStoreAddr(ob.get("store_addr").toString());

                        stores.add(c);
                    }

                    response = "success";

                } else {

                    response = "No Stores to Display";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {

            if (result == "success") {

                adapter = new CustomListAdapter(GeofenceslistActivity.this, R.layout.activity_store_item, stores);

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ((CustomListAdapter) parent.getAdapter()).setSelectedPosition(position);
                        StoreInfo store = (StoreInfo) parent.getItemAtPosition(position);

                        // String storeUrl = Store.getStoreUrl();
                        String storeName = store.getStoreName();
                        String storeAddr = store.getStoreAddr();
                        String storeDesc = store.getStoreDesc();
                        String storeUrl = store.getStoreUrl();
                        String storeId = store.getStoreId();
                        Intent t = new Intent(getApplicationContext(), StoreDetailActivity.class);
                        t.putExtra("storeName", storeName);
                        t.putExtra("storeDesc", storeDesc);
                        t.putExtra("storeAddr", storeAddr);
                        t.putExtra("storeUrl", storeUrl);
                        t.putExtra("username", username);
                        t.putExtra("password", password);
                        t.putExtra("storeId", storeId);
                        startActivity(t);

                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            }

        }
    }

    @SuppressLint("NewApi")
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    public void addStore(View v) {
        Intent t = new Intent(getApplicationContext(), AddStoreActivity.class);

        t.putExtra("username", username);
        t.putExtra("password", password);

        startActivity(t);
    }

}