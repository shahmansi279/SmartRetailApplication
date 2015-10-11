package com.project;

import java.io.IOException;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONObject;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EventsListActivity extends Activity {
    public ListView mListView;
    public EditText editText;
    public String username = null;
    public String password = null;
    public CustomEventsListAdapter adapterB;
    private String placeId = null;
    private String zipcode = null;

    ArrayList<EventInfo> events = new ArrayList<EventInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");
        this.placeId = i.getStringExtra("placeId");
        this.zipcode = i.getStringExtra("loc_zip_code");

      /*  ActionBar actionBar = getActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_view);
        EditText search = (EditText) actionBar.getCustomView().findViewById(
                R.id.searchfield);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

                adapterB.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);*/

        mListView = (ListView) findViewById(android.R.id.list);

        try {
            getSearchResults();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getSearchResults() throws IOException {
        String url;

        if (zipcode == "")

            url = "http://smartretailapp.appspot.com/smapp/default/getevents?uemail="
                    + this.username + "&password=" + this.password;

        else

            url = "http://smartretailapp.appspot.com/smapp/default/geteventsnearby?uemail="
                    + this.username + "&password=" + this.password + "&zipcode=" + this.zipcode;


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            DownloadWebPageTask task = new DownloadWebPageTask();
            task.execute(url);
        } else {

        }

    }

    public class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            String response = "";


            int code = 0;


            try {
                response = ConnectionUtil.downloadUrl(urls[0]);


                if (response != "error") {


                    JSONArray jso = new JSONArray(response);

                    for (int i = 0; i < jso.length(); i++) {
                        EventInfo c = new EventInfo();
                        JSONObject ob = (JSONObject) jso.get(i);
                        c.setEventTitle(ob.get("event_title").toString());
                        c.setEventAddr(ob.get("event_addr").toString());
                        c.setEventDesc(ob.get("event_desc").toString());
                        c.setEventIconUrl("http://smartapp-service.appspot.com/smapp/default/download/"
                                + ob.get("ev_img_url").toString());
                        c.setEventTiming(ob.get("event_timing").toString());

                        events.add(c);
                    }
                    response = "success";
                } else {

                    response = "No Upcoming  Events to Display";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {

            if (result == "success") {

                adapterB = new CustomEventsListAdapter(EventsListActivity.this,
                        R.layout.activity_event_item, events);

                mListView.setAdapter(adapterB);

                mListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        ((CustomEventsListAdapter) parent.getAdapter())
                                .setSelectedPosition(position);
                        EventInfo event = (EventInfo) parent
                                .getItemAtPosition(position);

                        // String placeUrl = Place.getPlaceUrl();
                        String eventTitle = event.getEventTitle();
                        String eventAddr = event.getEventAddr();
                        String eventTiming = event.getEventTiming();
                        String eventDesc = event.getEventDesc();
                        String eventIcon = event.getEventIconUrl();
                        Intent t = new Intent(getApplicationContext(),
                                EventDetailActivity.class);
                        t.putExtra("username", username);
                        t.putExtra("password", password);
                        t.putExtra("eTitle", eventTitle);
                        t.putExtra("eAddr", eventAddr);
                        t.putExtra("eDesc", eventDesc);
                        t.putExtra("eIcon", eventIcon);
                        t.putExtra("eTiming", eventTiming);

                        startActivity(t);

                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_SHORT).show();

            }

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
