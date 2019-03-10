package com.codineasy.wip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;

public class WeatherLogs extends AppCompatActivity {
    LinkedList<String> weatherLogs;
    private RecyclerView recyclerView;
    private WeatherLogsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_logs);

        this.weatherLogs = new LinkedList();
        for (int i = 0; i < 10; ++i) {
            weatherLogs.add("Weather #" + i);
        }

        // https://codelabs.developers.google.com/codelabs/android-training-create-recycler-view/index.html#3
        // Get a handle to the RecyclerView.
        this.recyclerView = findViewById(R.id.recyclerview);
        // Create an adapter and supply the data to be displayed.
        this.adapter = new WeatherLogsAdapter(this, this.weatherLogs);
        // Connect the adapter with the RecyclerView.
        this.recyclerView.setAdapter(this.adapter);
        // Give the RecyclerView a default layout manager.
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
