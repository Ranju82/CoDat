package com.ranjutech.codat.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

public class HelplineActivity extends AppCompatActivity implements BackgroundResponse {

    LinearLayout tollfreeLinearLayout,tollfree2LinearLayout,facebookLinearLayout,twitterLinearLayout,
                    emailLinearLayout,wesiteLinearLayout;

    TextView tollfreeTextView;

    BackgroundTask task;

    AdView helplineAdView;

    String dataIntent, tollfree1,tollfree2,facebook,twitter,email,website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);

        setTitle("Helpline");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tollfreeLinearLayout=findViewById(R.id.tollfreeLinearLayout);
        tollfree2LinearLayout=findViewById(R.id.tollfree2LinearLayout);
        facebookLinearLayout=findViewById(R.id.facebookLinearLayout);
        twitterLinearLayout=findViewById(R.id.twitterLinearLayout);
        emailLinearLayout=findViewById(R.id.emailLinearLayout);
        wesiteLinearLayout=findViewById(R.id.websiteLinearLayout);

        helplineAdView=findViewById(R.id.helplineAdView);
        tollfreeTextView=findViewById(R.id.tollfreeTextView);

        dataIntent=getIntent().getStringExtra("data");

        task=new BackgroundTask(this);
        task.response=this;
        task.execute("https://api.rootnet.in/covid19-in/contacts");

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        helplineAdView.loadAd(adRequest);
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
        if (result!=null){
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                JSONObject dataObject=new JSONObject(data);
                String contacts=dataObject.getString("contacts");
                JSONObject contactsObject=new JSONObject(contacts);
                String primary=contactsObject.getString("primary");
                JSONObject primaryObject=new JSONObject(primary);
                JSONArray jsonArray=primaryObject.getJSONArray("media");

                tollfree2=primaryObject.getString("number-tollfree");
                facebook=primaryObject.getString("facebook");
                twitter=primaryObject.getString("twitter");
                email=primaryObject.getString("email");
                website=jsonArray.get(0).toString();
                if(dataIntent.contains("India")){
                    tollfree1 =primaryObject.getString("number").replace("-","" );
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tollfreeTextView.setText("Tollfree");
                        }
                    });
                }else{
                    JSONArray regionalArray=contactsObject.getJSONArray("regional");
                    for(int i=0;i<regionalArray.length();i++){
                        JSONObject regionalObject=regionalArray.getJSONObject(i);
                        if(dataIntent.contains(regionalObject.getString("loc"))){
                            tollfree1 =regionalObject.getString("number").replace("-","" );
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tollfreeTextView.setText(dataIntent+" Tollfree");
                                }
                            });
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getHelp(View view){
        int id=view.getId();
        switch (id){
            case R.id.tollfreeLinearLayout:
                Intent intentTollfree = new Intent(Intent.ACTION_DIAL);
                intentTollfree.setData(Uri.parse("tel:"+ tollfree1));
                startActivity(intentTollfree);
                break;
            case R.id.tollfree2LinearLayout:
                Intent intentTollfree2 = new Intent(Intent.ACTION_DIAL);
                intentTollfree2.setData(Uri.parse("tel:"+tollfree2));
                startActivity(intentTollfree2);
                break;
            case R.id.facebookLinearLayout:
                Intent intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse(getFacebookPageURL() ));
                startActivity(intentFacebook);
                break;
            case R.id.twitterLinearLayout:
                Intent intentTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                startActivity(intentTwitter);
                break;
            case R.id.emailLinearLayout:
                Intent intentEmail = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:"+email+"?subject=" + Uri.encode("Enquiry of Corona Virus") + "&body=" + Uri.encode("Your Question or Doubts...."));
                intentEmail.setData(data);
                startActivity(intentEmail);
                break;
            case R.id.websiteLinearLayout:
                Intent intentWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(intentWebsite);
                break;
        }
    }

    public String getFacebookPageURL() {
        PackageManager packageManager = this.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + facebook;
            } else { //older versions of fb app
                return "fb://page/" + facebook.replace("https://www.facebook.com/","" );
            }
        } catch (PackageManager.NameNotFoundException e) {
            return facebook; //normal web url
        }
    }
}
