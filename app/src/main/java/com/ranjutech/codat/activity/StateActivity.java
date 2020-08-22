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

public class StateActivity extends AppCompatActivity {

    TextView statePlaceTextView,stateTotalTextView,stateRecoveredTextView,stateActiveTextView,stateDeathsTextView;

    String dataString,state,total,recovered,deaths,active;

    AdView stateAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        setTitle("State");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        statePlaceTextView=findViewById(R.id.statePlaceTextView);
        stateTotalTextView=findViewById(R.id.stateTotalTextView);
        stateRecoveredTextView=findViewById(R.id.stateRecoveredTextView);
        stateActiveTextView=findViewById(R.id.stateActiveTextView);
        stateDeathsTextView=findViewById(R.id.stateDeathsTextView);

        stateAdView=findViewById(R.id.stateAdView);

        dataString=getIntent().getStringExtra("data");

        if(dataString!=null){
            try {
                JSONObject jsonObject=new JSONObject(dataString);
                state=jsonObject.getString("loc");
                total=jsonObject.getString("totalConfirmed");
                recovered=jsonObject.getString("discharged");
                deaths=jsonObject.getString("deaths");
                active=String.valueOf(Integer.parseInt(total)-Integer.parseInt(recovered));

                statePlaceTextView.setText(state);
                stateTotalTextView.setText(total);
                stateRecoveredTextView.setText(recovered);
                stateActiveTextView.setText(active);
                stateDeathsTextView.setText(deaths);
;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        stateAdView.loadAd(adRequest);
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
            case R.id.stateGraphicsLinearLayout:
                Intent intentGraph=new Intent(this,GraphActivity.class);
                intentGraph.putExtra("data",state );
                startActivity(intentGraph);
                break;
            case R.id.stateHospitalLinearLayout:
                Intent intentHospital=new Intent(this,HospitalActivity.class);
                intentHospital.putExtra("data",state);
                startActivity(intentHospital);
                break;
            case R.id.stateHelplineLinearLayout:
                Intent intentHelpline=new Intent(this,HelplineActivity.class);
                intentHelpline.putExtra("data",state );
                startActivity(intentHelpline);
                break;
            case R.id.stateAreaLinearLayout:
                Intent intentArea=new Intent(this,ListActivity.class);
                intentArea.putExtra("data","district"+state);
                startActivity(intentArea);
                break;
        }
    }
}
