package com.codineasy.wip;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private Context mContext;


    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        if(!WipGlobals.isShowingDirection) {
            Weather weather = WipGlobals.details.get(WipGlobals.detailsIndex.get()).get(i).getWeather();
            if(weather.isReady()) {
                viewHolder.imageView.setImageResource(getImageResource(""));
                if(MapsActivity.mUnits == "metric") viewHolder.column1.setText(weather.temperature() + "°C");
                else if(MapsActivity.mUnits == "imperial")viewHolder.column1.setText(weather.temperature() + "°F");
                viewHolder.column2.setText(weather.summary());
                StringBuilder sb = new StringBuilder();
                long sec = WipGlobals.details.get(WipGlobals.detailsIndex.get()).get(i).getTimeToArrive();
                if (sec > 60) {
                    long min = sec / 60;
                    sec = sec - min * 60;
                    if (min > 60) {
                        long hrs = min / 60;
                        min = min - hrs * 60;
                        sb.append(hrs + "h" + min + "m");
                    } else {
                        sb.append(min + "m" + sec + "s");
                    }
                } else {
                    sb.append(sec + "s");
                }
                viewHolder.column3.setText(sb.toString());

            } else {
                viewHolder.column1.setText("");
                viewHolder.column2.setText("Waiting for weather data, please refresh");
                viewHolder.column3.setText("");
            }
        } else {
            List<HashMap<HashMap<String, String>, HashMap<String, String>>> route = MapsActivity.mStepsData.get(WipGlobals.detailsIndex.get());
            HashMap<HashMap<String, String>, HashMap<String, String>> step = route.get(i);
            HashMap<String, String> instructions = (HashMap<String, String>) step.keySet().toArray()[0];
            viewHolder.column1.setText("");
            viewHolder.imageView.setImageResource(getImageResource(instructions.get("maneuver")));
            viewHolder.column2.setText(Jsoup.parse(instructions.get("html_instructions")).text());
            double distance = Double.valueOf(step.get(instructions).get("distance"));
            String distanceString = "";
            if (MapsActivity.mUnits == "metric") {
                distance = distance / 1000.0;
                if (distance < 0.1) {
                    distanceString = ( Math.round(distance * 1000) + " m");
                }
                else if (distance < 100) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    distanceString = (df.format(distance) + " km");
                } else {
                    distance = Math.round(distance);
                    distanceString = ((((Double) distance).longValue()) + " km");
                }
            } else if (MapsActivity.mUnits == "imperial") {
                distance = distance / 5280.0;
                if (distance < 0.1) {
                    distanceString = (Math.round(distance * 5280) + " ft");
                }
                else if (distance < 100) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    distanceString = (df.format(distance) + " mi");
                } else {
                    distance = Math.round(distance);
                    distanceString = ((((Double) distance).longValue()) + " mi");
                }
            }
            viewHolder.column3.setText(distanceString + "\n"
                    + step.get(instructions).get("duration"));
        }
    }

    @Override
    public int getItemCount() {
        if(!WipGlobals.isShowingDirection) {
            if (WipGlobals.details.size() != 0)
                return WipGlobals.details.get(WipGlobals.detailsIndex.get()).size();
            else
                return 0;
        } else {
            int count = 0;
            List<HashMap<HashMap<String, String>, HashMap<String, String>>> legs = MapsActivity.mStepsData.get(WipGlobals.detailsIndex.get());
            for (HashMap<HashMap<String, String>, HashMap<String, String>> steps : legs) {
                count += steps.keySet().size();
            }
            return count;
        }
    }

    public int getImageResource(String maneuver) {
        switch (maneuver) {
            case "turn-slight-left": return R.drawable.slightly_left;
            case "turn-sharp-left": return R.drawable.hard_left;
            case "uturn-left": return R.drawable.uturn_left;
            case "turn-left": return R.drawable.turn_left;
            case "turn-slight-right": return R.drawable.slightly_right;
            case "turn-sharp-right": return R.drawable.hard_right;
            case "uturn-right": return R.drawable.uturn_right;
            case "turn-right": return R.drawable.turn_right;
            case "straight": return R.drawable.straight;
            case "ramp-left": return R.drawable.exit_left;
            case "ramp-right": return R.drawable.exit_right;
            case "fork-left": return R.drawable.left;
            case "fork-right": return R.drawable.right;
            case "roundabout-left": return R.drawable.circle_clockwise;
            case "roundabout-right": return R.drawable.circle_counterclockwise;
            case "merge": return R.drawable.merge;
            default: return R.drawable.clear;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView column1;
        TextView column2;
        TextView column3;
        ImageView imageView;

        RelativeLayout parentLayout;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            column1 = itemView.findViewById(R.id.temperature);
            column2 = itemView.findViewById(R.id.summary);
            column3 = itemView.findViewById(R.id.duration);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
