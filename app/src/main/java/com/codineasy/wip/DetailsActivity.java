package com.codineasy.wip;

import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
        for (int i = 1; i < 10; ++i) {
            locationDetails.add(new LocationDetail(
                    "Raining " + i,
                    null,
                    new LinkedList<String>(Arrays.asList("Condition: Rainy", "Temperature: " + i * 69))
            ));
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
