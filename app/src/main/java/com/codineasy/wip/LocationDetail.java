package com.codineasy.wip;

import android.location.Address;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.LinkedList;

public class LocationDetail {
    private LatLng location;
    private JSONObject weather;

    public LocationDetail(LatLng location, JSONObject weather) {
        this.location = location;
        this.weather = weather;
    }

    public LatLng getLocation() {
        return location;
    }

    public JSONObject getWeather() {
        return weather;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setWeather(JSONObject weather) {
        this.weather = weather;
    }

    public double distanceFrom(LatLng location) {
        return -1;
    }

    @Override
    public String toString() {
        return "LocationDetail{" +
                "location=" + location +
                ", weather=" + weather +
                '}';
    }
}
