package com.codineasy.wip;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class Weather extends BaseObservable {
    private DarkSkyJSONHandler handler;
    private LocationDetail detail;
    private JSONObject json;
    //private Integer time;

    public Weather(DarkSkyJSONHandler handler, LocationDetail detail) {
        this.handler = handler;

        this.handler.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                update();
            }
        });

        this.detail = detail;

        update();
    }

//    public Weather(DarkSkyJSONHandler handler, JSONObject json) throws JSONException, NullPointerException {
//        this.handler = handler;
//
//        this.handler.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                update();
//            }
//        });
//
//        this.json = json;
//        this.time = json.getInt("time");
//    }

    public void update() {
        try {
            JSONObject json = handler.getJson();
            if(json != null) {
                long time = /*this.time == null ?*/ WipGlobals.startTime + detail.getTimeToArrive() /*: this.time*/;
                JSONArray array = json.getJSONObject("hourly").getJSONArray("data");
                this.json = (JSONObject) array.get(findWeatherClosestToTime(time, array));
                this.notifyChange();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearWeather() {
        this.json = null;
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String icon()  {
        try {
            return json.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
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

        return 0;
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

//    public Weather getNextHourWeather() throws JSONException {
//        if(this.json == null)
//            update();
//
//        JSONArray array = handler.getJson().getJSONObject("hourly").getJSONArray("data");
//        int index = 0;
//        while(array.getJSONObject(index).getInt("time") != time())
//            ++index;
//
//        return new Weather(handler, array.getJSONObject(index+1));
//    }

    @Override
    public String toString() {
        return "Weather{" +
                handler.getLocation() + ", " +
                "duration=" + detail.getTimeToArrive() + ", " +
                "distance=" + detail.getDistanceToArrive() + ", " +
                "json=" + json +
                '}';
    }

    public JSONObject getJson() {
        return json;
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
