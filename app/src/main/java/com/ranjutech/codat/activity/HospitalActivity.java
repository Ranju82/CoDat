package com.ranjutech.codat.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ranjutech.codat.R;
import com.ranjutech.codat.controller.BackgroundTask;
import com.ranjutech.codat.utils.BackgroundResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class HospitalActivity extends AppCompatActivity implements BackgroundResponse {

    TextView totalTextView,bedTextView,urbanTextView,urbanBedTextView,ruralTextView,ruralBedTextView,hospitalRegion;
    String dataIntent, totalHospitals,totalBeds,ruralHospitals,ruralBeds,urbanHospitals,urbanBeds,region;
    BackgroundTask task;

    AdView hospitalAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        setTitle("Hospital");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        totalTextView=findViewById(R.id.totalHospitalTextView);
        bedTextView=findViewById(R.id.totalBedTextView);
        urbanTextView=findViewById(R.id.urbanHospitalTextView);
        urbanBedTextView=findViewById(R.id.urbanBedTextView);
        ruralTextView=findViewById(R.id.ruralHospitalTextView);
        ruralBedTextView=findViewById(R.id.ruralBedTextView);
        hospitalRegion=findViewById(R.id.hospitalRegionTexView);

        hospitalAdView=findViewById(R.id.hospitalAdView);


        dataIntent=getIntent().getStringExtra("data");

        task=new BackgroundTask(this);
        task.response=this;
        task.execute("https://api.rootnet.in/covid19-in/hospitals/beds");

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        hospitalAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu) {
            Intent intent=new Intent(this,NotificationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponseReceived(String result) {
        if(result!=null){
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                JSONObject dataObject=new JSONObject(data);
                if(dataIntent.contains("India")){
                    String summary=dataObject.getString("summary");
                    JSONObject summaryObject=new JSONObject(summary);

                    region="India";
                    totalHospitals =summaryObject.getString("totalHospitals");
                    totalBeds=summaryObject.getString("totalBeds");
                    ruralHospitals=summaryObject.getString("ruralHospitals");
                    ruralBeds=summaryObject.getString("ruralBeds");
                    urbanHospitals=summaryObject.getString("urbanHospitals");
                    urbanBeds=summaryObject.getString("urbanBeds");

                    setTexts();
                }else{
                    JSONArray jsonArray=dataObject.getJSONArray("regional");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        if(dataIntent.equals(jsonObject1.getString("state"))){
                            region=dataIntent;
                            totalHospitals =jsonObject1.getString("totalHospitals");
                            totalBeds=jsonObject1.getString("totalBeds");
                            ruralHospitals=jsonObject1.getString("ruralHospitals");
                            ruralBeds=jsonObject1.getString("ruralBeds");
                            urbanHospitals=jsonObject1.getString("urbanHospitals");
                            urbanBeds=jsonObject1.getString("urbanBeds");
                            break;
                        }
                    }
                    setTexts();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void setTexts(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hospitalRegion.setText(region);
                totalTextView.setText(totalHospitals);
                bedTextView.setText(totalBeds);
                ruralTextView.setText(ruralHospitals);
                ruralBedTextView.setText(ruralBeds);
                urbanTextView.setText(urbanHospitals);
                urbanBedTextView.setText(urbanBeds);
            }
        });
    }
}
