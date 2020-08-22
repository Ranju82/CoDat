package com.ranjutech.codat.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.ViewGroup;

import com.ranjutech.codat.utils.BackgroundResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundTask extends AsyncTask<String,Void,String> {

    public BackgroundResponse response=null;
    public Context mcontext;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder;

    public  BackgroundTask(Context context){
        this.mcontext=context;
    }

    @Override
    protected void onPreExecute() {
        alertDialogBuilder=new AlertDialog.Builder(mcontext);
        alertDialogBuilder.setMessage("Loading....Please wait!");
        alertDialogBuilder.setCancelable(true);
        alertDialog=alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        URL url;
        try {
            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }

            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        response.onResponseReceived(result);
        alertDialog.dismiss();
    }
}
