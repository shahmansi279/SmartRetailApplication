package com.project;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mansi on 9/26/15.
 */
public class ConnectionUtil {


    public static String downloadUrl(String url) {


        int code = 0, len =2000;
        InputStream is = null;

        String contentAsString = "";
        try {
            Log.v("Status", "Before execute");
            URL conn_url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) conn_url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            code = conn.getResponseCode();
            if (code != 200)

            {

                return "error";
            }

            is = conn.getInputStream();

            // Convert the InputStream into a string

            contentAsString = readIt(is, len);
            is.close();

        } catch (IOException e) {


            e.printStackTrace();
        } finally {
            if (is != null) {

            }
        }
        return contentAsString;

    }

    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
