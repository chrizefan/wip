package com.codineasy.wip.directionhelpers;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.codineasy.wip.DarkSkyJSONHandler;
import com.codineasy.wip.LocationDetail;
import com.codineasy.wip.R;
import com.codineasy.wip.WipGlobals;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.codineasy.wip.GlobalApplication.getAppContext;
import static com.codineasy.wip.MapsActivity.jDirections;
import static com.codineasy.wip.MapsActivity.mRoutesData;


/**
 * Created by Vishal on 10/20/2018.
 */

public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode;
    private static final String TAG = "PointsParser";

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
            jDirections = jObject;
            Log.d(TAG, jsonData[0]);
            DataParser parser = new DataParser();
            Log.d(TAG, parser.toString());

            // Starts parsing data
            routes = parser.parseJObjectLatLng(jObject);
            mRoutesData = getJObjectData(jObject);
            Log.d(TAG, "Executing routes");
            Log.d(TAG, routes.toString());

        } catch (Exception e) {
            Log.d(TAG, e.toString());
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
            Log.d(TAG, "onPostExecute lineoptions decoded");
            Log.d(TAG, "RoutesData:" + mRoutesData.toString());
            // Drawing polyline in the Google Map for the i-th route
            //mMap.addPolyline(lineOptions);

        DarkSkyJSONHandler.allowAllUpdate(getAppContext());
        for(List<HashMap<HashMap<String, String>, HashMap<String, String>>> listMapMap: mRoutesData) {
            ObservableArrayList<LocationDetail> tmpList = new ObservableArrayList<>();
            for(HashMap<HashMap<String, String>, HashMap<String, String>> mapMap : listMapMap)
                for(HashMap<String, String> latlng : mapMap.keySet()) {
                    LocationDetail detail = new LocationDetail(latlng, mapMap.get(latlng));
                    detail.fetchDarkSky();
                    tmpList.add(detail);
                }

            WipGlobals.details.add(tmpList);
        }

            taskCallback.onTaskDone((Object[]) lineOptions);
        }

    private List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> getJObjectData(JSONObject jObject){
        List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> routesData  = new ArrayList<>();
        List<List<HashMap<String, String>>> routes = DataParser.parseJObjectLatLng(jObject);
        for (int i = 0; i < routes.size(); i++) {
            com.google.maps.model.LatLng origin;
            com.google.maps.model.LatLng destination;
            List<HashMap<String, String>> route = routes.get(i);
            HashMap<String, String> hashMap;
            List<com.google.maps.model.LatLng> latLng = new ArrayList<>();
            for (int j = 0; j < route.size(); j += route.size()/10) {
                hashMap = route.get(j);
                latLng.add(new com.google.maps.model.LatLng(Double.valueOf(hashMap.get("lat")), Double.valueOf(hashMap.get("lng"))));
            }
            origin = new com.google.maps.model.LatLng(Double.valueOf(route.get(0).get("lat")), Double.valueOf(route.get(0).get("lng")));
            destination = new com.google.maps.model.LatLng(Double.valueOf(route.get(route.size()-1).get("lat")), Double.valueOf(route.get(route.size()-1).get("lng")));
            latLng = latLng.subList(1, latLng.size() - 1);
            try {
                JSONObject jRoute = new JSONObject(downloadUrl(getUrl(origin, destination, latLng, "driving")));
                routesData.add(new DataParser().parseJObjectData(jRoute));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return routesData;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = null;
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d(TAG, "Downloaded URL: " + data);
            br.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception downloading URL: " + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getUrl(com.google.maps.model.LatLng origin, com.google.maps.model.LatLng destination, List<com.google.maps.model.LatLng> latLngs, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.lat + "," + origin.lng;
        Log.d(TAG, "origin:" + origin);
        // Destination of route
        String str_dest = "destination=" + destination.lat + "," + destination.lng;
        Log.d(TAG, "destination:" + destination);
        // Mode
        String mode = "mode=" + directionMode;
        // WayPoints
        String encodedWaypoints = "waypoints=enc:" + new EncodedPolyline(latLngs).getEncodedPath() + ":";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + encodedWaypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&alternatives=false" + "&units=metric" + "&key=" + getAppContext().getString(R.string.google_maps_key2);
    }

}
