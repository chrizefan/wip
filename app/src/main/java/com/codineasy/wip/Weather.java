package com.codineasy.wip;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Weather extends BaseObservable {
    private DarkSkyJSONHandler handler;
    private LocationDetail detail;
    private JSONObject json;

    public Weather(DarkSkyJSONHandler handler, LocationDetail detail) {
        this.handler = handler;

        this.handler.addOnPropertyChangedCallback(new DarkSkyJSONHandlerListener());

        this.detail = detail;

        updateJSON();
    }

    private void updateJSON() {
        try {
            JSONObject json = handler.getJson();
            if(json != null) {
                long time = WipGlobals.startTime + detail.getTimeToArrive();
                JSONArray array = json.getJSONObject("hourly").getJSONArray("data");
                this.json = (JSONObject) array.get(findWeatherClosestToTime(time, array));
                this.notifyChange();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String summary()  {
        try {
            if(json != null)
                return json.getString("summary");
        } catch (JSONException e) {

        }
        return null;
    }


    public String icon()  {
        try {
            if(json != null)
                return json.getString("icon");
        } catch (JSONException e) {

        }
        return null;
    }


    public double temperature()  {
        try {
            if(json != null)
                return json.getDouble("temperature");
        } catch (JSONException e) {

        }
        return Double.NaN;
    }

    public String getString(String key) throws JSONException {
        return json.getString(key);
    }

    @Override
    public String toString() {
        return "Weather{" +
                "json=" + json +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Weather) {
            return json.toString().equals(((Weather) o).json.toString());
        }
        return false;
    }

    public static Integer findWeatherClosestToTime(long time, JSONArray array) {
        int closestIndex = -1;
        long smallestDiff = -1;

        try {
            for(int i = 0; i < array.length(); ++i) {
                JSONObject json = (JSONObject) array.get(i);
                long currentDiff = Math.abs(json.getInt("time") - time);
                if (smallestDiff == -1 || currentDiff < smallestDiff) {
                    smallestDiff = currentDiff;
                    closestIndex = i;
                } else {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return closestIndex;
    }

    class DarkSkyJSONHandlerListener extends OnPropertyChangedCallback {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            updateJSON();
        }
    }
}
