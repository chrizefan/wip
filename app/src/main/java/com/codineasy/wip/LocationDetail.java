package com.codineasy.wip;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocationDetail extends BaseObservable {
    private LatLng location;
    private DarkSkyJSONHandler jsonHandler;

    private int timeToArrive;
    private int distanceToArrive;

    public LocationDetail(HashMap<String, String> latLng, HashMap<String, String> durDst) {
        this.updateDetails(latLng, durDst);
        this.jsonHandler.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                notifyChange();
            }
        });
    }

    public void updateDetails(HashMap<String, String> latLng, HashMap<String, String> durDst) {
        this.location = new LatLng(
                Double.parseDouble(Objects.requireNonNull(latLng.get("lat"))),
                Double.parseDouble((Objects.requireNonNull(latLng.get("lng"))))
        );

        this.timeToArrive = Integer.parseInt(Objects.requireNonNull(durDst.get("duration")));
        this.distanceToArrive = Integer.parseInt(Objects.requireNonNull(durDst.get("distance")));

        this.jsonHandler = new DarkSkyJSONHandler(this);
        notifyChange();
    }

    public void fetchDarkSky() {
        jsonHandler.update();
    }

    public LatLng getLocation() {
        return location;
    }

    public Weather getWeather() {
            return jsonHandler.getWeather();
    }

    public int getTimeToArrive() {
        return timeToArrive;
    }

    public int getDistanceToArrive() {
        return distanceToArrive;
    }

    @Override
    public String toString() {
        return "LocationDetail{" +
                "location=" + location +
                ", jsonHandler=" + jsonHandler +
                ", timeToArrive=" + timeToArrive +
                ", distanceToArrive=" + distanceToArrive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof LocationDetail) {
            LocationDetail l = (LocationDetail)o;

            boolean darkSkyHandlerEqual;
            if(l.jsonHandler == jsonHandler)
                darkSkyHandlerEqual = true;
            else
                darkSkyHandlerEqual = jsonHandler.equals(l.jsonHandler);

            return l.location.equals(location) && darkSkyHandlerEqual &&
                    l.timeToArrive == timeToArrive && l.distanceToArrive == distanceToArrive;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location.latitude, location.longitude, timeToArrive, distanceToArrive);
    }
}
