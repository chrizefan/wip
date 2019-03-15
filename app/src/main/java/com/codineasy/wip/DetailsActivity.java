package com.codineasy.wip;

import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class DetailsActivity extends AppCompatActivity {
    LinkedList<LocationDetail> locationDetails;
    private RecyclerView recyclerView;
    private LocationDetailListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        this.locationDetails = new LinkedList();
        for (int i = 0; i < 1; ++i) {
            JSONObject json = null;
            try {
                json = new JSONObject(
                        "{\"currently\": {\n" +
                                "              \"time\": 1509993277,\n" +
                                "              \"summary\": \"Drizzle\",\n" +
                                "              \"icon\": \"rain\",\n" +
                                "              \"nearestStormDistance\": 0,\n" +
                                "              \"precipIntensity\": 0.0089,\n" +
                                "              \"precipIntensityError\": 0.0046,\n" +
                                "              \"precipProbability\": 0.9,\n" +
                                "              \"precipType\": \"rain\",\n" +
                                "              \"temperature\": 66.1,\n" +
                                "              \"apparentTemperature\": 66.31,\n" +
                                "              \"dewPoint\": 60.77,\n" +
                                "              \"humidity\": 0.83,\n" +
                                "              \"pressure\": 1010.34,\n" +
                                "              \"windSpeed\": 5.59,\n" +
                                "              \"windGust\": 12.03,\n" +
                                "              \"windBearing\": 246,\n" +
                                "              \"cloudCover\": 0.7,\n" +
                                "              \"uvIndex\": 1,\n" +
                                "              \"visibility\": 9.84,\n" +
                                "              \"ozone\": 267.44\n" +
                                "          }}"
                );
            } catch (JSONException e) {
                Log.e("JSON Error: ", e.getMessage());
            }
            locationDetails.add(new LocationDetail(null, json));
        }

        // https://codelabs.developers.google.com/codelabs/android-training-create-recycler-view/index.html#3
        // Get a handle to the RecyclerView.
        this.recyclerView = findViewById(R.id.recyclerview);
        // Create an adapter and supply the data to be displayed.
        this.adapter = new LocationDetailListAdapter(this, this.locationDetails);
        // Connect the adapter with the RecyclerView.
        this.recyclerView.setAdapter(this.adapter);
        // Give the RecyclerView a default layout manager.
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
