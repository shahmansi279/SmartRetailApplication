package com.project;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.project.AppActivity.GimbalEventReceiver;

import android.annotation.SuppressLint;
import android.app.ActionBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

@SuppressWarnings("deprecation")
public class OfferListActivity extends Activity {
	private GimbalEventReceiver gimbalEventReceiver;
	private GimbalEventListAdapter adapter1;
	public ListView mListView;
	public EditText editText;
	private String username = null;
	private String password = null;
	public CustomOffersListAdapter adapter;
	ArrayList<OfferInfo> offers = new ArrayList<OfferInfo>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offer_list);

		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");

		ActionBar actionBar = getActionBar();
		// add the custom view to the action bar
/*	actionBar.setCustomView(R.layout.actionbar_view);

	//	EditText search = (EditText) actionBar.getCustomView().findViewById(
				R.id.searchfield);
	//	search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {

				adapter.getFilter().filter(cs.toString());
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
				| ActionBar.DISPLAY_SHOW_HOME);
				*/

		mListView = (ListView) findViewById(android.R.id.list);

		try {
			getSearchResults();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//code to start monitoring

		//startService(new Intent(this, AppService.class));

		PlaceManager.getInstance().startMonitoring();
		CommunicationManager.getInstance().startReceivingCommunications();

		// Setup Push Communication
		String gcmSenderId = "596028915255"; // <--- SET THIS STRING TO YOUR
		// PUSH SENDER ID HERE (Google
		// API project #) ##
		registerForPush(gcmSenderId);

		//testing code
    /*	String placeid="634226EF4C414B2DA65EE36E9D9B2C17";
		Intent t = new Intent(getApplicationContext(),
				OfferDetailActivity.class);
		t.putExtra("placeid", placeid);
		startActivity(t);*/

	}

	public void getSearchResults() throws IOException {

		String url = "http://smartretailapp.appspot.com/smapp/default/getoffers?uemail="
				+ this.username + "&password=" + this.password;

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
				Log.v("res",response);

				if (response != "error") {


					JSONArray jso = new JSONArray(response);

					for (int i = 0; i < jso.length(); i++) {
						OfferInfo c = new OfferInfo();
						JSONObject ob = (JSONObject) jso.get(i);
						c.setOfferTitle(ob.get("offer_title").toString());
						c.setOfferDesc(ob.get("offer_desc").toString());
						c.setOfferValidity(ob.get("offer_validity").toString());
						c.setOfferIconUrl("http://smartretailapp.appspot.com/smapp/default/download/"
								+ ob.get("offer_img_url").toString());

						c.setOfferId(ob.get("id").toString());
						c.setOfferZipcode(ob.get("offer_zipcode").toString());
						c.setOfferPlaceId(ob.get("offer_place_id").toString());

						offers.add(c);
					}
					response = "success";
				} else {

					response = "No Offers to Display";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		protected void onPostExecute(String result) {

			if (result == "success") {

			adapter = new CustomOffersListAdapter(OfferListActivity.this,
						R.layout.activity_offer_item, offers);

				mListView.setAdapter(adapter);

				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {

						((CustomOffersListAdapter) parent.getAdapter())
								.setSelectedPosition(position);
						OfferInfo place = (OfferInfo) parent
								.getItemAtPosition(position);

						// String placeUrl = Place.getPlaceUrl();
						String OfferTitle = place.getOfferTitle();
						String OfferZipcode = place.getOfferZipcode();
						String OfferDesc = place.getOfferDesc();
						String OfferUrl = place.getOfferIconUrl();
						String OfferId = place.getOfferId();
						String OfferT = place.getOfferValidity();
						Intent t = new Intent(getApplicationContext(),
								OfferDetailActivity.class);
						t.putExtra("OfferTitle", OfferTitle);
						t.putExtra("OfferDesc", OfferDesc);
						t.putExtra("OfferZipcode", OfferZipcode);
						t.putExtra("OfferUrl", OfferUrl);
						t.putExtra("username", username);
						t.putExtra("password", password);
						t.putExtra("OfferId", OfferId);
						t.putExtra("OfferT", OfferT);
						startActivity(t);

					}
				});

			} else {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);

	}

	private void registerForPush(String gcmSenderId) {
		if (gcmSenderId != null) {
			Gimbal.registerForPush(gcmSenderId);
		}
	}

	public void onNotNowClicked(View view) {
		GimbalDAO.setOptInShown(getApplicationContext());
		PlaceManager.getInstance().stopMonitoring();
		finish();
	}

	public void onEnableClicked(View view) {
		// GimbalDAO.setOptInShown(getApplicationContext());

		Toast.makeText(getApplicationContext(), "In Enable Clicked",
				Toast.LENGTH_SHORT).show();

		PlaceManager.getInstance().startMonitoring();
		CommunicationManager.getInstance().startReceivingCommunications();

		// Setup Push Communication
		String gcmSenderId = "596028915255"; // <--- SET THIS STRING TO YOUR
		// PUSH SENDER ID HERE (Google
		// API project #) ##
		registerForPush(gcmSenderId);

	}
}