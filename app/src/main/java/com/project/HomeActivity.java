package com.project;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.os.Handler;
import android.os.ResultReceiver;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.project.util.Constants;


public class HomeActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private String username = null;
    private String password = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    protected boolean mAddressRequested;

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mResultReceiver = new AddressResultReceiver(new Handler());
        mAddressOutput = "";
        mAddressOutput="95054";

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");

        startGimbalMonitoring();
        buildGoogleApiClient();
    }



    public void startGimbalMonitoring(){


        PlaceManager.getInstance().startMonitoring();
        CommunicationManager.getInstance().startReceivingCommunications();

        // Setup Push Communication
        String gcmSenderId = "486190651958"; // <--- SET THIS STRING TO YOUR
        // PUSH SENDER ID HERE (Google
        // API project #) ##
        registerForPush(gcmSenderId);

    }

    private void registerForPush(String gcmSenderId) {
        if (gcmSenderId != null) {
            Gimbal.registerForPush(gcmSenderId);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart() {
        super.onStart();
        mAddressRequested = true;

        Log.v("Conn", "Connecting to Google Client");
     //   showToast("Connecting to Google Client");
        mGoogleApiClient.connect();




    }


    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

       // showToast("Connected to Google Client");

       mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        showToast("mLastLocation"+ mLastLocation);
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {

               // showToast("Starting Intent Service");
                startIntentService();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

       // showToast("On Connection Failed");
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("Tag", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {

       // showToast("On Connection Suspended");
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Tag", "Connection suspended");
        mGoogleApiClient.connect();
    }


    public void stores(View v) {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(), StoreListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        intent_name.putExtra("loc_zip_code",mAddressOutput);
        startActivity(intent_name);
    }

    public void restaurants(View v) {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(), RestaurantListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        intent_name.putExtra("loc_zip_code",mAddressOutput);

        startActivity(intent_name);
    }

    public void offers(View v) {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(), OfferListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        intent_name.putExtra("loc_zip_code",mAddressOutput);
        startActivity(intent_name);
    }

    public void events(View v) {
        Intent intent_name = new Intent();
        intent_name.setClass(getApplicationContext(), EventsListActivity.class);
        intent_name.putExtra("username", username);
        intent_name.putExtra("password", password);
        intent_name.putExtra("loc_zip_code",mAddressOutput);

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

    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }



    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

           // showToast("ON Receive Result");

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
           // showToast("mAddressOutput "+mAddressOutput);
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(mAddressOutput);
            }

            else
            {
                showToast("Error Fetching location");
                mAddressOutput="";
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;

        }
    }
}