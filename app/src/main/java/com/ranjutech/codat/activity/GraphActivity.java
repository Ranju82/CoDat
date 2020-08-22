 package com.ranjutech.codat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.ranjutech.codat.R;
import com.ranjutech.codat.controller.BackgroundTask;
import com.ranjutech.codat.utils.BackgroundResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

 public class GraphActivity extends AppCompatActivity implements BackgroundResponse {

    GraphView graphViewTotal,graphViewRecovered,graphViewActive,graphViewDeath;
    String data;

    BackgroundTask task;

    DataPoint[] dataPointsTotal,dataPointsRecovered,dataPointsActive,dataPointsDeath;

     private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        setTitle("India");

        data=getIntent().getStringExtra("data");

        graphViewTotal=findViewById(R.id.graphTotal);
        graphViewRecovered=findViewById(R.id.graphRecovered);
        graphViewDeath=findViewById(R.id.graphDeath);
        graphViewActive=findViewById(R.id.graphActive);

        task=new BackgroundTask(this);
        task.response=this;
        task.execute("https://api.rootnet.in/covid19-in/stats/history");

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
         if(result!=null){
             try {
                 JSONObject jsonObject=new JSONObject(result);
                 String success=jsonObject.getString("success");
                 if(success.equals("true")){

                     JSONArray historyArray=jsonObject.getJSONArray("data");

                     ArrayList<String> totalArrayList=new ArrayList<>();
                     ArrayList<String> recoveredArrayList=new ArrayList<>();
                     ArrayList<String> deathArrayList=new ArrayList<>();
                     ArrayList<Integer> activeArrayList=new ArrayList<>();

                     String total,recovered,death="";
                     int active=0;

                     for(int i=30;i<historyArray.length();i=i+30){
                         JSONObject dayObject=historyArray.getJSONObject(i);
                         if(data.contains("India")) {
                             setTitle("India");
                             String summary = dayObject.getString("summary");
                             JSONObject summaryObject = new JSONObject(summary);
                             total = summaryObject.getString("total");
                             recovered = summaryObject.getString("discharged");
                             death = summaryObject.getString("deaths");
                             active = Integer.parseInt(total) - Integer.parseInt(recovered);

                             totalArrayList.add(total);
                             recoveredArrayList.add(recovered);
                             deathArrayList.add(death);
                             activeArrayList.add(active);
                         }else{
                             setTitle(data);
                             JSONArray regionArray=dayObject.getJSONArray("regional");
                             for(int k=0;k<regionArray.length();k++){
                                 JSONObject jsonObject1=regionArray.getJSONObject(k);
                                 if(data.contains(jsonObject1.getString("loc"))){
                                     total = jsonObject1.getString("totalConfirmed");
                                     recovered = jsonObject1.getString("discharged");
                                     death = jsonObject1.getString("deaths");
                                     active = Integer.parseInt(total) - Integer.parseInt(recovered);

                                     totalArrayList.add(total);
                                     recoveredArrayList.add(recovered);
                                     deathArrayList.add(death);
                                     activeArrayList.add(active);
                                     break;
                                 }
                             }
                         }
                     }

                     dataPointsTotal =new DataPoint[totalArrayList.size()];
                     dataPointsRecovered=new DataPoint[recoveredArrayList.size()];
                     dataPointsDeath=new DataPoint[deathArrayList.size()];
                     dataPointsActive=new DataPoint[activeArrayList.size()];

                     for(int j=0;j<totalArrayList.size();j++){
                         dataPointsTotal[j]=new DataPoint(j,Double.valueOf(totalArrayList.get(j))/100000 );
                     }

                     for(int j=0;j<recoveredArrayList.size();j++){
                         dataPointsRecovered[j]=new DataPoint(j,Double.valueOf(recoveredArrayList.get(j))/100000 );
                     }

                     for(int j=0;j<deathArrayList.size();j++){
                         dataPointsDeath[j]=new DataPoint(j,Double.valueOf(deathArrayList.get(j))/1000 );
                     }

                     for(int j=0;j<activeArrayList.size();j++){
                         dataPointsActive[j]=new DataPoint(j,Double.valueOf(activeArrayList.get(j))/100000 );
                     }
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }


         LineGraphSeries<DataPoint> totalSeries = new LineGraphSeries<>(dataPointsTotal);
         LineGraphSeries<DataPoint> recoveredSeries = new LineGraphSeries<>(dataPointsRecovered);
         LineGraphSeries<DataPoint> deathSeries = new LineGraphSeries<>(dataPointsDeath);
         LineGraphSeries<DataPoint> activeSeries = new LineGraphSeries<>(dataPointsActive);

         totalSeries.setTitle("In Lakhs");
         recoveredSeries.setTitle("In Lakhs");
         deathSeries.setTitle("In Thousands");
         activeSeries.setTitle("In Lakhs");

         graphViewTotal.addSeries(totalSeries);
         graphViewRecovered.addSeries(recoveredSeries);
         graphViewDeath.addSeries(deathSeries);
         graphViewActive.addSeries(activeSeries);

         graphViewTotal.setTitle("Confirmed Case");
         graphViewActive.setTitle("Active Case");
         graphViewDeath.setTitle("Death Case");
         graphViewRecovered.setTitle("Recovered Case");

         graphViewTotal.getLegendRenderer().setVisible(true);
         graphViewActive.getLegendRenderer().setVisible(true);
         graphViewDeath.getLegendRenderer().setVisible(true);
         graphViewRecovered.getLegendRenderer().setVisible(true);

         graphViewTotal.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
         graphViewActive.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
         graphViewDeath.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
         graphViewRecovered.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

         StaticLabelsFormatter totalStaticLabelsFormatter = new StaticLabelsFormatter(graphViewTotal);
         StaticLabelsFormatter activeStaticLabelsFormatter = new StaticLabelsFormatter(graphViewActive);
         StaticLabelsFormatter deathStaticLabelsFormatter = new StaticLabelsFormatter(graphViewDeath);
         StaticLabelsFormatter recoveredStaticLabelsFormatter = new StaticLabelsFormatter(graphViewRecovered);

         totalStaticLabelsFormatter.setHorizontalLabels(new String[] {"March", " --- ", "till date"});
         activeStaticLabelsFormatter.setHorizontalLabels(new String[] {"March", " --- ", "till date"});
         deathStaticLabelsFormatter.setHorizontalLabels(new String[] {"March", " --- ", "till date"});
         recoveredStaticLabelsFormatter.setHorizontalLabels(new String[] {"March", " --- ", "till date"});

         graphViewTotal.getGridLabelRenderer().setLabelFormatter(totalStaticLabelsFormatter);
         graphViewActive.getGridLabelRenderer().setLabelFormatter(activeStaticLabelsFormatter);
         graphViewDeath.getGridLabelRenderer().setLabelFormatter(deathStaticLabelsFormatter);
         graphViewRecovered.getGridLabelRenderer().setLabelFormatter(recoveredStaticLabelsFormatter);
     }

 }
