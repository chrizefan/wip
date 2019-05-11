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

        this.handler.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                update();
            }
        });

        this.detail = detail;
    }

    public void update() {
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

    public void fetchDarkSky() {
        this.detail.fetchDarkSky();
    }

    public boolean isReady() {
        return json != null;
    }


    public String summary()  {
        try {
            return json.getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String icon()  {
        try {
            return json.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int temperature()  {
            try {
                double temp = json.getDouble("temperature");
                int a = (int) Math.round(temp);
                return a;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }
        double temp1= Double.NaN;
            int b = (int) Math.round(temp1);
            return b;
    }

    public String getString(String key) throws JSONException {
        return json.getString(key);
    }

    public long time() {
        try {
            return json.getLong("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Weather{" +
                handler.getLocation() + ", " +
                "duration=" + detail.getTimeToArrive() + ", " +
                "distance=" + detail.getDistanceToArrive() + ", " +
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
        int closestIndex = 0;
        long smallestDiff;
        try {
            smallestDiff = Math.abs(array.getJSONObject(0).getInt("time") - time);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }

        try {
            for(int i = 1; i < array.length(); ++i) {
                JSONObject json = array.getJSONObject(i);
                long currentDiff = Math.abs(json.getInt("time") - time);
                if (currentDiff < smallestDiff) {
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
}
