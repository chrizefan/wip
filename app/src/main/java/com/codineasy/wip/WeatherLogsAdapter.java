package com.codineasy.wip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;

public class WeatherLogsAdapter extends RecyclerView.Adapter<WeatherLogsAdapter.WeatherLogViewHolder> {
    private final LinkedList<String> weatherLogs;
    private LayoutInflater layoutInflater;

    public WeatherLogsAdapter(Context context, LinkedList<String> weatherLogs) {
        this.layoutInflater = LayoutInflater.from(context);
        this.weatherLogs = weatherLogs;
    }

    @NonNull
    @Override
    public WeatherLogsAdapter.WeatherLogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = this.layoutInflater.inflate(R.layout.recycler_view_item, viewGroup, false);

        return new WeatherLogViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherLogsAdapter.WeatherLogViewHolder weatherLogViewHolder, int i) {
        String current = this.weatherLogs.get(i);
        weatherLogViewHolder.weatherItemView.setText(current);
    }

    @Override
    public int getItemCount() {
        return weatherLogs.size();
    }

    public class WeatherLogViewHolder extends RecyclerView.ViewHolder {
        public final TextView weatherItemView;
        final WeatherLogsAdapter adapter;

        public WeatherLogViewHolder(View itemView, WeatherLogsAdapter adapter) {
            super(itemView);
            this.weatherItemView = itemView.findViewById(R.id.textView);
            this.adapter = adapter;
        }
    }
}
