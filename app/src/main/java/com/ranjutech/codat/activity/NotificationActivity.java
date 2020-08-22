package com.ranjutech.codat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.ranjutech.codat.R;
import com.ranjutech.codat.controller.NotificationAdapter;
import com.ranjutech.codat.controller.BackgroundTask;
import com.ranjutech.codat.utils.BackgroundResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class NotificationActivity extends AppCompatActivity implements BackgroundResponse {

    ListView notificationListView;
    LinkedHashMap<String,String> map=new LinkedHashMap<>();
    NotificationAdapter notificationAdapter;

    private InterstitialAd mInterstitialAd;

    BackgroundTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setTitle("Notification");

        notificationListView=findViewById(R.id.notificationListView);

        task=new BackgroundTask(this);
        task.response=this;

        task.execute("https://api.rootnet.in/covid19-in/notifications");

        MobileAds.initialize(this, initializationStatus -> {});
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2958598946856656/1197512583");
        mInterstitialAd.loadAd(adRequest);

        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView=view.findViewById(R.id.notificationTextView);
                String link=textView.getTag().toString();
                Intent intentWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intentWebsite);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }

    @Override
    public void onResponseReceived(String result) {
        if(result!=null){
            try {
                Log.i("stata",result);
                JSONObject jsonObject = new JSONObject(result);
                String data = jsonObject.getString("data");
                JSONObject dataObject = new JSONObject(data);
                JSONArray jsonArray=dataObject.getJSONArray("notifications");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject notificationObject=jsonArray.getJSONObject(i);
                    String title=notificationObject.getString("title");
                    String link=notificationObject.getString("link");

                    map.put(title,link );
                }

                notificationAdapter=new NotificationAdapter(getApplicationContext(), map);
                notificationListView.setAdapter(notificationAdapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
