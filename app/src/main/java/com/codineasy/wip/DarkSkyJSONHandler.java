package com.codineasy.wip;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.Objects;

public class DarkSkyJSONHandler extends BaseObservable {
    private LocationDetail detail;
    private Weather weather;
    private JSONObject json;
    private final Response.ErrorListener ERROR_LISTENER = (VolleyError error) -> {
        Log.d("DarkSkyJSONHandler", "Volley request error: " + error);
    };
    private final Response.Listener<JSONObject> LISTENER = (JSONObject response) -> {
            json = response;
            notifyChange();
        };
    private static RequestQueue queue;

    private String getRequestFormat() {
        String units = "auto";
        if(MapsActivity.mUnits == "metric") units = "si";
        else if(MapsActivity.mUnits == "imperial") units = "us";
        return "https://api.darksky.net/forecast/7b077f2e5773e91b61bf9cce9c4c759f/%f,%f?exclude=minutely&units="+units;
    }

    public DarkSkyJSONHandler(LocationDetail detail) {
        this.json = null;

        this.detail = detail;

        weather = new Weather(this, this.detail);
    }

    public Weather getWeather() {
        weather.update();
        return weather;
    }

    public JSONObject getJson() {
        return json;
    }

    @Bindable
    public LatLng getLocation() {
        return detail.getLocation();
    }

    @Override
    public String toString() {
        return "DarkSkyJSONHandler{" +
                ", json=" + json +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof DarkSkyJSONHandler) {
            DarkSkyJSONHandler h = (DarkSkyJSONHandler)o;
            return detail.hashCode() == h.detail.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(detail, 1);
    }

    public void update() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                String.format(getRequestFormat(), getLocation().latitude, getLocation().longitude),
                null,
                this.LISTENER,
                this.ERROR_LISTENER
        );
        queue.add(request);
    }

    public static void allowAllUpdate(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public static void preventAllUpdate() {
        queue.stop();
    }

    public static void reallowAllUpdate() {
        queue.start();
    }
}
