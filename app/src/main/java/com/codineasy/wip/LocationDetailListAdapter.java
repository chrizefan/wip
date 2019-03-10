package com.codineasy.wip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

public class LocationDetailListAdapter extends RecyclerView.Adapter<LocationDetailListAdapter.LocationDetailViewHolder> {
    private final LinkedList<String> weatherLogs;
    private LayoutInflater layoutInflater;

    public LocationDetailListAdapter(Context context, LinkedList<String> weatherLogs) {
        this.layoutInflater = LayoutInflater.from(context);
        this.weatherLogs = weatherLogs;
    }

    @NonNull
    @Override
    public LocationDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = this.layoutInflater.inflate(R.layout.recycler_view_item, viewGroup, false);

        return new LocationDetailViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationDetailViewHolder locationDetailViewHolder, int i) {
        String current = this.weatherLogs.get(i);
        locationDetailViewHolder.weatherItemView.setText(current);
    }

    @Override
    public int getItemCount() {
        return weatherLogs.size();
    }

    public class LocationDetailViewHolder extends RecyclerView.ViewHolder {
        public final TextView weatherItemView;
        final LocationDetailListAdapter adapter;

        public LocationDetailViewHolder(View itemView, LocationDetailListAdapter adapter) {
            super(itemView);
            this.weatherItemView = itemView.findViewById(R.id.textView);
            this.adapter = adapter;
        }
    }
}
