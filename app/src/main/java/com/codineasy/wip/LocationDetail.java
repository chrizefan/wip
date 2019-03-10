package com.codineasy.wip;

import android.location.Address;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;

public class LocationDetail {
    private ImageView weatherIcon;
    private String weatherInfo;
    private Address location;
    private LinkedList<String> relevantWeatherInfos;

    public LocationDetail(String weatherInfo, Address location, LinkedList<String> relevantWeatherInfos) {
        this.weatherInfo = null;
        this.weatherInfo = weatherInfo;
        this.location = location;
        this.relevantWeatherInfos = relevantWeatherInfos;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public Address getLocation() {
        return location;
    }

    public LinkedList<String> getRelevantWeatherInfos() {
        return relevantWeatherInfos;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public void setRelevantWeatherInfos(LinkedList<String> relevantWeatherInfos) {
        this.relevantWeatherInfos = relevantWeatherInfos;
    }

    @Override
    public String toString() {
        return "LocationDetail{" +
                "weatherIcon=" + weatherIcon +
                ", weatherInfo='" + weatherInfo + '\'' +
                ", location=" + location +
                ", relevantWeatherInfos=" + relevantWeatherInfos +
                '}';
    }
}
