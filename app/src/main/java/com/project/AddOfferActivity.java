package com.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.util.Constants;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mansi on 10/14/15.
 */
public class AddOfferActivity extends Activity {

    private ProgressBar progressBar;
    private EditText offerTitle = null;
    private EditText offerDesc = null;
    private EditText offerStartDate = null;
    private EditText offerEndDate = null;
    private ImageView offerImg = null;

    private Button addOffer;
    private String username;
    private String password;
    private String selectedImagePath;
    private String filemanagerstring;


    String logo, imagePath, Logo;
    Cursor cursor;
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        Intent i = getIntent();
        this.username = i.getStringExtra("username");
        this.password = i.getStringExtra("password");
        offerTitle = (EditText) findViewById(R.id.editText1);
        offerDesc = (EditText) findViewById(R.id.editText2);
        offerEndDate = (EditText) findViewById(R.id.editText3);
        offerImg = (ImageView) findViewById(R.id.imageView2);
        addOffer = (Button) findViewById(R.id.button1);


    }

    public void chooseImage(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();


                //OI FILE Manager


                filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);


                offerImg.setImageURI(selectedImageUri);




              //  Bitmap bm = BitmapFactory.decodeFile(imagePath);

             //   offerImg.setImageBitmap(bm);


            }

        }

    }

    public String getPath(Uri uri) {

        if (uri == null) {
            return null;
        }

        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }


    public void addOffer(View v) {


//        Read params here


        if (username != null
                && password != null
                && offerTitle.getText().toString() != null
                && offerDesc.getText().toString() != null
                && offerEndDate.getText().toString() != null && offerImg.getDrawable() != null) {


            String url = "http://127.0.0.1:8000/smapp/default/upload" ;
                    /*"addoffer?uemail="
                    + this.username
                    + "&password="
                    + this.password
                    + "&offer_title=" + this.offerTitle.getText().toString()
                    + "&offer_desc" + this.offerTitle.getText().toString();*/


            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                UploadFileToServer task = new UploadFileToServer();
                task.execute(url);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Network Connection not available", Toast.LENGTH_SHORT)
                        .show();
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


    //Code to create offer

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<String, Void, String> {

        private String filepath;
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            progressBar.setProgress(0);

            this.filepath= selectedImagePath;
            super.onPreExecute();
        }

       /* @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }
*/

        @Override
        protected String doInBackground(String... url) {
            return uploadFile(url[0]);
        }

        private String uploadFile(String urll) {
            String responseString = null;


            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";


            try {



                File file = new File(this.filepath);
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStream.read(bytes);
                fileInputStream.close();

                String fileName = file.getName();

                URL url = new URL(urll);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.setChunkedStreamingMode(1024);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)");
                connection.setRequestProperty("image", fileName);
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                //send multipart form data (required) for file
                outputStream.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + fileName + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + lineEnd);
                //outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                //outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                outputStream.writeBytes("Content-Length: " + file.length() + lineEnd);
                outputStream.writeBytes(lineEnd);

                int bufferLength = 1024;
                for (int i = 0; i < bytes.length; i += bufferLength) {
                    // publishing the progress....
                    Bundle resultData = new Bundle();
                    resultData.putInt("progress" ,(int)((i / (float) bytes.length) * 100));
                    // receiver.send(UPDATE_PROGRESS, resultData);

                    if (bytes.length - i >= bufferLength) {
                        outputStream.write(bytes, i, bufferLength);
                    } else {
                        outputStream.write(bytes, i, bytes.length - i);
                    }
                }

                //end output
                outputStream.writeBytes(lineEnd);


                // publishing the progress....
                Bundle resultData = new Bundle();
                resultData.putInt("progress", 100);
                // receiver.send(UPDATE_PROGRESS, resultData);

                outputStream.flush();
                outputStream.close();
                //input ignored for now


            } catch (Exception e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("temp", "Response from server: " + result);
            //showAlert(result);
          //  Intent i = new Intent(UploadActivity.this, HomeActivity.class);
            //i.putExtra("username", username);
            //i.putExtra("password", password);
            //startActivity(i);

            super.onPostExecute(result);
        }

    }



}
