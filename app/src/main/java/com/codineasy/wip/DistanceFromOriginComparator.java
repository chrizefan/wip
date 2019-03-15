package com.codineasy.wip;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

public class DistanceFromOriginComparator implements Comparator<LatLng> {
    private LatLng origin;

    public DistanceFromOriginComparator(LatLng origin) {
        this.origin = origin;
    }

    @Override
    public int compare(LatLng locationA, LatLng locationB) {
        return -1;
    }
}
