package com.project;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

@SuppressLint("NewApi")
public class  UploadActivity extends Activity {
    // LogCat tag

    private ProgressBar progressBar;
    private String filePath = null;
    private String username = null;
    private String password = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    long totalSize = 0;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);

        // Changing action bar background color


        // Receiving the data from previous activity
        Intent i = getIntent();

        filePath = i.getStringExtra("filePath");
        username = i.getStringExtra("username");
        password = i.getStringExtra("password");
        // boolean flag to identify the media type, image or video
        boolean isImage = true;

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                new UploadFileToServer().execute();

            }
        });

    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);



            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        private String filepath;
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            progressBar.setProgress(0);

             this.filepath= filePath;
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        private String uploadFile() {
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

                URL url = new URL("http://smartretailapp.appspot.com/smapp/default/upload");
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

                //write more parameters other than the file
             /*   outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                //outputStream.writeBytes(twoHyphens + boundary + lineEnd); //less twohyphens
                outputStream.writeBytes("Content-Disposition: form-data; name=\"invnum\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                //outputStream.writeBytes("Content-Length: " + invnum.length() + lineEnd);
                outputStream.writeBytes(lineEnd);
                //outputStream.writeBytes(invnum + lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                outputStream.writeBytes("Content-Disposition: form-data; name=\"pass\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                //outputStream.writeBytes("Content-Length: " + pass.length() + lineEnd);
                outputStream.writeBytes(lineEnd);
                //outputStream.writeBytes(pass + lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);*/



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
            showAlert(result);
            Intent i = new Intent(UploadActivity.this, HomeActivity.class);
            i.putExtra("username", username);
            i.putExtra("password", password);
            startActivity(i);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
