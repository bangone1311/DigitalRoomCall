package com.misentek.digitalroomcall;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.misentek.digitalroomcall.MainActivity.activity;


public class UploadFileAsync extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        try {
            String sourceFileUri = params[0];
            File sourceFile = new File(sourceFileUri);

            if (sourceFile.isFile()) {



                Log.d("fakfak","Is file : "+ sourceFile.isFile());
                try {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    String ipaddr = preferences.getString("ipaddr","192.168.43.142");
                    String url = "http://"+ipaddr+"/upload";


                } catch (Exception e) {

                    // dialog.dismiss();
                    Log.e("fak",e.getMessage());
                    e.printStackTrace();

                }
                // dialog.dismiss();

            } // End else block


        } catch (Exception ex) {
            // dialog.dismiss();

            Log.e("fak",ex.getMessage());
            ex.printStackTrace();
        }

        Log.d("fak","returned");
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}