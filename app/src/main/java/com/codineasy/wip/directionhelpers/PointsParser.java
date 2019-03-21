package com.codineasy.wip.directionhelpers;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.codineasy.wip.MapsActivity;
import com.codineasy.wip.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.codineasy.wip.GlobalApplication.getAppContext;

/**
 * Created by Vishal on 10/20/2018.
 */

public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode;

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            MapsActivity.jDirections = jObject;
            Log.d("mylog", jsonData[0]);
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());

            // Starts parsing data
            routes = parser.parseJObjectLatLng(jObject);
            Log.d("mylog", "Executing routes");
            Log.d("mylog", routes.toString());

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions[] lineOptions = new PolylineOptions[result.size()];
        // Traversing through all the routes
        for (int i = 0; i < lineOptions.length; i++) {
            points = new ArrayList<>();
            lineOptions[i] = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions[i].addAll(points);
                lineOptions[i].width(20);
                lineOptions[i].clickable(true);
                if (i == 0) {
                    lineOptions[i].color(ContextCompat.getColor(getAppContext(), R.color.colorBlue));
                    lineOptions[i].zIndex(1);
                } else {
                    lineOptions[i].color(ContextCompat.getColor(getAppContext(), R.color.colorBlueTransparent));
                    lineOptions[i].zIndex(0);
                }
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");
            // Drawing polyline in the Google Map for the i-th route
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone((Object[]) lineOptions);
        }

}
