package com.ranjutech.codat.activity;

import android.content.Intent;
 import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.ranjutech.codat.R;
import com.ranjutech.codat.controller.BackgroundTask;
import com.ranjutech.codat.controller.ListAdapter;
import com.ranjutech.codat.utils.BackgroundResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity implements BackgroundResponse {

    ListView listView;
    LinkedHashMap<String,String> map=new LinkedHashMap<>();

    ListAdapter listAdapter;

    JSONArray jsonArray;
    JSONObject districtDataObject;
    Intent intentState,intentDistrict;

    private InterstitialAd mInterstitialAd;

    String state;

    BackgroundTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView=findViewById(R.id.regionListView);
        intentState=new Intent(this,StateActivity.class);
        intentDistrict=new Intent(this,DistrictActivity.class);

        task=new BackgroundTask(this);
        task.response=this;

        final String data=getIntent().getStringExtra("data");

        if(data!=null){
            if(data.contains("district")){
                setTitle("Districts");
                state=data.replace("district","" );
                task.execute("https://api.covid19india.org/state_district_wise.json");
            }else {
                setTitle("States");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    jsonArray = jsonObject.getJSONArray("regional");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String name = object.getString("loc");
                        String number = object.getString("totalConfirmed");

                        map.put(name, number);
                    }

                    listAdapter=new ListAdapter(getApplicationContext(), map);
                    listView.setAdapter(listAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(data.contains("district")){
                        TextView textView=view.findViewById(R.id.regionNameTextView);
                        String name=textView.getText().toString();
                        String data=districtDataObject.getString(name);
                        String[] value={name,data,state};
                        intentDistrict.putExtra("data", value);
                        startActivity(intentDistrict);
                    }else {
                        JSONObject jsonObject = jsonArray.getJSONObject(position);
                        intentState.putExtra("data", jsonObject.toString());
                        startActivity(intentState);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        MobileAds.initialize(this, initializationStatus -> {});
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2958598946856656/1197512583");
        mInterstitialAd.loadAd(adRequest);
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
    public void onResume() {
        // Start or resume the game.
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
        try {
            JSONObject jsonObject=new JSONObject(result);
            String stateString=jsonObject.getString(state);
            JSONObject allDistrictObject=new JSONObject(stateString);
            String allDistricts=allDistrictObject.getString("districtData");
            districtDataObject=new JSONObject(allDistricts);

            Iterator<String> names=districtDataObject.keys();

            while(names.hasNext()){
                final String name=names.next();
                if (districtDataObject.get(name) instanceof JSONObject) {
                   final String data=((JSONObject) districtDataObject.get(name)).getString("confirmed");
                    map.put(name,data );
                }
            }
            listAdapter=new ListAdapter(getApplicationContext(), map);
            listView.setAdapter(listAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
