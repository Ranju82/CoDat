package com.ranjutech.codat.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ranjutech.codat.R;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class DistrictActivity extends AppCompatActivity {

    TextView districtPlaceTextView,districtTotalTextView,districtRecoveredTextView,districtActiveTextView,districtDeathsTextView;

    String district,total,recovered,deaths,active;

    String[] dataString;

    AdView districtAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);

        setTitle("District");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        districtPlaceTextView=findViewById(R.id.districtPlaceTextView);
        districtTotalTextView=findViewById(R.id.districtTotalTextView);
        districtRecoveredTextView=findViewById(R.id.districtRecoveredTextView);
        districtActiveTextView=findViewById(R.id.districtActiveTextView);
        districtDeathsTextView=findViewById(R.id.districtDeathsTextView);

        districtAdView=findViewById(R.id.districtAdView);

        dataString=getIntent().getStringArrayExtra("data");

        if(dataString!=null){
            try{
                JSONObject jsonObject=new JSONObject(dataString[1]);
                district=dataString[0];
                total=jsonObject.getString("confirmed");
                recovered=jsonObject.getString("recovered");
                active=jsonObject.getString("active");
                deaths=jsonObject.getString("deceased");

                districtPlaceTextView.setText(district);
                districtTotalTextView.setText(total);
                districtActiveTextView.setText(active);
                districtRecoveredTextView.setText(recovered);
                districtDeathsTextView.setText(deaths);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        districtAdView.loadAd(adRequest);
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

    public void getScreen(View view){
        int id=view.getId();
        switch (id){
            case R.id.districtGraphicsLinearLayout:
                Intent intentGraph=new Intent(this,GraphActivity.class);
                intentGraph.putExtra("data",dataString[2] );
                startActivity(intentGraph);
                break;
            case R.id.districtHospitalLinearLayout:
                Intent intentHospital=new Intent(this,HospitalActivity.class);
                intentHospital.putExtra("data",dataString[2]);
                startActivity(intentHospital);
                break;
            case R.id.districtHelplineLinearLayout:
                Intent intentHelpline=new Intent(this,HelplineActivity.class);
                intentHelpline.putExtra("data",dataString[2] );
                startActivity(intentHelpline);
                break;
        }
    }
}
