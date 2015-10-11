package com.project;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.project.util.Constants;

import java.util.Date;

public class ManageOfferListActivity extends ActionBarActivity {


    //List of types for upload photo

    private static final int ACTION_TAKE_PHOTO_B = 1;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";

    private Bitmap mImageBitmap;

    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;



    public ListView mListView;
    public EditText editText;
    private String username = null;
    private String password = null;
    private String placeid= null;

    public CustomOffersListAdapter adapter;
    ArrayList<OfferInfo> offers = new ArrayList<OfferInfo>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminofferlist);

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");
        this.placeid = i.getStringExtra("placeid");
        Log.v("place",this.placeid);


        Button picBtn = (Button) findViewById(R.id.btnIntend);
        setBtnListenerOrDisable(picBtn, mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE);

        mAlbumStorageDirFactory = new BaseAlbumDirFactory();

        mListView = (ListView) findViewById(android.R.id.list);

        try {
            getSearchResults();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void getSearchResults() throws IOException {

        String url = Constants.SMARTAPP_CONTEXT+ "getplaceoffers?uemail=" + this.username
                + "&password=" + this.password +"&aplaceid="+ this.placeid ;

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
                        OfferInfo c = new OfferInfo();
                        JSONObject ob = (JSONObject) jso.get(i);
                        c.setOfferTitle(ob.get("offer_title").toString());
                        c.setOfferDesc(ob.get("offer_desc").toString());
                        c.setOfferValidity(ob.get("offer_validity").toString());
                        c.setOfferIconUrl(Constants.SMARTAPP_CONTEXT+"download/"
                                + ob.get("offer_img_url").toString());

                        c.setOfferId(ob.get("id").toString());
                        c.setOfferZipcode(ob.get("offer_zipcode").toString());
                        c.setOfferPlaceId(ob.get("offer_place_id").toString());

                        Log.v("offer_title",c.getOfferTitle());

                        offers.add(c);


                    }

                    response = "success";

                } else {

                    response = "No Places to Display";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {

            if (result == "success") {

                adapter = new CustomOffersListAdapter(ManageOfferListActivity.this, R.layout.activity_offer_item, offers);

                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    public void addOffer(View v) {
        Intent t = new Intent(getApplicationContext(), AddStoreActivity.class);

        t.putExtra("username", username);
        t.putExtra("password", password);

        startActivity(t);
    }



    //Adding the Content to fetch camera intent.


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory
                    .getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name),
                    "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
                albumF);

        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

        int targetW = 200;

        int targetH = 200;

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(
                "android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    fileUri = Uri.fromFile(f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            uploadPic();
            mCurrentPhotoPath = null;
        }

    }

    private void uploadPic() {

    }

    Button.OnClickListener mTakePicOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
        }
    };

    /** Called when the activity is first created. */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {

                    launchUploadActivity(true);
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

        } // switch
    }

    private void launchUploadActivity(boolean b) {
        // TODO Auto-generated method stub

        Intent i = new Intent(ManageOfferListActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("username", this.username);
        i.putExtra("password", this.password);
        startActivity(i);

    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);

        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
                (mImageBitmap != null));
        outState.putParcelable("file_uri", fileUri);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        fileUri = savedInstanceState.getParcelable("file_uri");

    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(Button btn,
                                         Button.OnClickListener onClickListener, String intentName) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(getText(R.string.cannot).toString() + " "
                    + btn.getText());
            btn.setClickable(false);
        }
    }





}