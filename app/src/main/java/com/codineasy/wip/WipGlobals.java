package com.codineasy.wip;

import android.database.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WipGlobals {
    public static long startTime = 0;
    public static long weatherUpdateTime = 1000*60*60;
    public static long maxTimeout = 1000;
    public static ObservableList<ObservableArrayList<LocationDetail>> details = new ObservableArrayList<>();
//     public static ObservableList<Weather> currentWeathers = new ObservableArrayList<>();
    public static ObservableInt detailsIndex = new ObservableInt(-1);
//     public static class PointDistanceComparator implements Comparator<LocationDetail> {
//         @Override
//         public int compare(LocationDetail o1, LocationDetail o2) {
//             return o1.getDistanceToArrive() - o2.getDistanceToArrive();
//         }
//     }
    public static boolean isShowingDirection = false;
}
