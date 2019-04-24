package com.codineasy.wip;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.Format;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DarkSkyJSONHandler extends BaseObservable {
    private LocationDetail detail;
    private Weather weather;

    private static final String requestFormat = "https://api.darksky.net/forecast/7b077f2e5773e91b61bf9cce9c4c759f/%f,%f,%d?exclude=currently,minutely&units=auto";
    private JSONObject json;
    private final Response.ErrorListener errorListener = (VolleyError error) -> {};
    private final Response.Listener<JSONObject> listener = (JSONObject response) -> {
            json = response;
            notifyChange();
        };
    private static RequestQueue queue;


    public DarkSkyJSONHandler(LocationDetail detail) {
        this.json = null;

        this.detail = detail;

        weather = new Weather(this, this.detail);
    }

    public Weather getWeather() {
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
                String.format(requestFormat, getLocation().latitude, getLocation().longitude, Calendar.getInstance().getTimeInMillis() / 1000 + detail.getTimeToArrive()),
                null,
                this.listener,
                this.errorListener
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
