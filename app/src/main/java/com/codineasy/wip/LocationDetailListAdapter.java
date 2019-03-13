package com.codineasy.wip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.LinkedList;

public class LocationDetailListAdapter extends RecyclerView.Adapter<LocationDetailListAdapter.LocationDetailViewHolder> {
    private final LinkedList<LocationDetail> locationDetails;
    private LayoutInflater layoutInflater;

    public LocationDetailListAdapter(Context context, LinkedList<LocationDetail> locationDetails) {
        this.layoutInflater = LayoutInflater.from(context);
        this.locationDetails = locationDetails;
    }

    @NonNull
    @Override
    public LocationDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = this.layoutInflater.inflate(R.layout.recycler_view_item, viewGroup, false);

        return new LocationDetailViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationDetailViewHolder locationDetailViewHolder, int i) {
        LocationDetail current = this.locationDetails.get(i);
            locationDetailViewHolder.weatherTextView.setText(current.toString());
    }

    @Override
    public int getItemCount() {
        return locationDetails.size();
    }

    public class LocationDetailViewHolder extends RecyclerView.ViewHolder {
        public final TextView weatherTextView;
        public final ImageView weatherIconView;
        final LocationDetailListAdapter adapter;

        public LocationDetailViewHolder(View itemView, LocationDetailListAdapter adapter) {
            super(itemView);
            this.weatherTextView = itemView.findViewById(R.id.textView2);
            this.weatherIconView = itemView.findViewById(R.id.imageView2);
            this.adapter = adapter;
        }
    }
}
