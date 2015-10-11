package com.project;

import java.io.IOException;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONObject;

import com.project.BeaconListActivity.DownloadWebPageTask;

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

public class BeaconListActivity extends Activity {
	public ListView mListView;
	public EditText editText;
	private String username = null;
	private String password = null;
	private String placeid = null;
	public CustomBeaconListAdapter adapterB;
	ArrayList<BeaconInfo> beacons = new ArrayList<BeaconInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beaconlist);

		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");
		this.placeid = i.getStringExtra("placeid");


		Log.v("place",this.placeid);
		mListView = (ListView) findViewById(android.R.id.list);

		try {
			getSearchResults();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getSearchResults() throws IOException {

		String url = "http://smartretailapp.appspot.com/smapp/default/getallbeacons?uemail="+ this.username
				+ "&password=" + this.password +"&placeid="+ this.placeid ;


		Log.v("urlbeacon",url);

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


			Log.v("url", urls[0]);


			try {


				response = ConnectionUtil.downloadUrl(urls[0]);


				if (response != "error") {

					JSONArray jso = new JSONArray(response);

					for (int i = 0; i < jso.length(); i++) {


						BeaconInfo c = new BeaconInfo();
						JSONObject ob = (JSONObject) jso.get(i);
						c.setBeaconName(ob.get("name").toString());
						c.setBeaconBatteryLvl(ob.get("b_battery_lvl")
								.toString());
						c.setBeaconFactId(ob.get("b_f_id").toString());
						c.setBeaconIconUrl("http://smartretailapp.appspot.com/smapp/default/download/"
								+ ob.get("beacon_img_url").toString());

						c.setBeaconStatus(ob.get("beacon_status").toString());
						Log.v("beacon_id", c.getBeaconName());

						beacons.add(c);
					}
					response = "success";
				} else {

					response = "No Sensors to Display";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		protected void onPostExecute(String result) {

			if (result == "success") {

				adapterB = new CustomBeaconListAdapter(BeaconListActivity.this,
						R.layout.activity_beacon_item, beacons);

				mListView.setAdapter(adapterB);

				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						((CustomBeaconListAdapter) parent.getAdapter())
								.setSelectedPosition(position);
						BeaconInfo beacon = (BeaconInfo) parent
								.getItemAtPosition(position);

						// String placeUrl = Place.getPlaceUrl();
						String beaconName = beacon.getBeaconName();
						String beaconStatus = beacon.getBeaconStatus();
						String beaconBattery = beacon.getBeaconBatteryLvl();
						String beaconIcon = beacon.getBeaconIconUrl();
						Intent t = new Intent(getApplicationContext(),
								BeaconInfoActivity.class);
						t.putExtra("b_f_id",beacon.getBeaconFactId());
						
						t.putExtra("bName", beaconName);
						t.putExtra("bStatus", beaconStatus);
						t.putExtra("bBattery", beaconBattery);
						t.putExtra("beaconIcon", beaconIcon);
						t.putExtra("username", username);
						t.putExtra("password", password);

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
