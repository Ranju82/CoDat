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
import com.ranjutech.codat.controller.BackgroundTask;
import com.ranjutech.codat.utils.BackgroundResponse;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements BackgroundResponse {

    TextView placeTextView,totalTextView,recoveredTextView,activeTextView,deathsTextView;

    BackgroundTask task;

    String dataString;

    AdView homeAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Home");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        placeTextView=findViewById(R.id.placeTextView);
        totalTextView=findViewById(R.id.totalTextView);
        recoveredTextView=findViewById(R.id.recoveredTextView);
        activeTextView=findViewById(R.id.activeTextView);
        deathsTextView=findViewById(R.id.deathsTextView);

        homeAdView=findViewById(R.id.homeAdView);

        task=new BackgroundTask(this);
        task.response=this;

        task.execute("https://api.rootnet.in/covid19-in/stats/latest");

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        homeAdView.loadAd(adRequest);
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
            case R.id.graphicsLinearLayout:
                Intent intentGraph=new Intent(this,GraphActivity.class);
                intentGraph.putExtra("data","India" );
                startActivity(intentGraph);
                break;
            case R.id.hospitalLinearLayout:
                Intent intentHospital=new Intent(this,HospitalActivity.class);
                intentHospital.putExtra("data","India" );
                startActivity(intentHospital);
                break;
            case R.id.helplineLinearLayout:
                Intent intentHelpline=new Intent(this,HelplineActivity.class);
                intentHelpline.putExtra("data","India" );
                startActivity(intentHelpline);
                break;
            case R.id.areaLinearLayout:
                Intent intentArea=new Intent(this,ListActivity.class);
                intentArea.putExtra("data",dataString );
                startActivity(intentArea);
                break;
        }
    }

    @Override
    public void onResponseReceived(String result) {
        if(result!=null){
            try {
                JSONObject jsonObject=new JSONObject(result);
                dataString=jsonObject.getString("data");
                JSONObject dataObject=new JSONObject(dataString);
                String summaryString=dataObject.getString("summary");
                JSONObject totalObject=new JSONObject(summaryString);

                final String total=totalObject.getString("total");
                final String recovered=totalObject.getString("discharged");
                final int active=Integer.parseInt(total)-Integer.parseInt(recovered);
                final String deaths=totalObject.getString("deaths");
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        totalTextView.setText(total);
                        recoveredTextView.setText(recovered);
                        activeTextView.setText(String.valueOf(active));
                        deathsTextView.setText(deaths);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
