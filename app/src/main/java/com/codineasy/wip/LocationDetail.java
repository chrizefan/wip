package com.codineasy.wip;

import android.location.Address;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;

public class LocationDetail {
    private JSONObject weather;
    private Address location;

    public LocationDetail(JSONObject weather, Address location) {
        this.weather = weather;
        this.location = location;
    }

    public Address getLocation() {
        return location;
    }

    public JSONObject getWeather() throws JSONException {
        return weather;
    }

    public void setWeather(JSONObject weather) {
        this.weather = weather;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public double distanceFrom(Address address) {
        return Math.random()*100;
    }

    @Override
    public String toString() {
        return "LocationDetail{" +
                "weather=" + weather +
                ", location=" + location +
                '}';
    }
}
