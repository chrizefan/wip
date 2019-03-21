package com.codineasy.wip.directionhelpers;

import android.util.Log;

import com.codineasy.wip.R;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import org.json.JSONArray;
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

/**
 * Created by Vishal on 10/20/2018.
 */

public class DataParser {

    public double[] parseTotalDistance(JSONObject jObject) {
        JSONArray jRoutes;
        JSONArray jLegs;
        try {
            jRoutes = jObject.getJSONArray("routes");
            double[] distances = new double[jRoutes.length()];
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                double distance = 0;
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    distance += jLegs.getJSONObject(j).getJSONObject("distance").getDouble("value");
                }
                distances[i] = distance;
            }
            return distances;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {}
        return null;
    }

    public int[] parseTotalDuration(JSONObject jObject) {
        JSONArray jRoutes;
        JSONArray jLegs;
        try {
            jRoutes = jObject.getJSONArray("routes");
            int[] durations = new int[jRoutes.length()];
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                int duration = 0;
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    duration += jLegs.getJSONObject(j).getJSONObject("duration").getInt("value");
                }
                durations[i] = duration;
            }
            return durations;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {}
        return null;
    }

    public List<List<HashMap<String, String>>> parseJObjectDistanceDuration(JSONObject jObject) throws JSONException {
        List routes = new ArrayList();
        JSONArray jRoutes;
        JSONArray jLegs;
        try {
            jRoutes = jObject.getJSONArray("routes");
            double[] distances = new double[jRoutes.length()];
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List distanceDuration = new ArrayList();
                double distanceBetweenPoints = 0;
                double distance = 0;
                int duration = 0;
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    distance += jLegs.getJSONObject(j).getJSONObject("distance").getDouble("value");
                    duration += jLegs.getJSONObject(j).getJSONObject("duration").getDouble("value");
                    if (distanceBetweenPoints < 5000) {
                        distanceBetweenPoints += jLegs.getJSONObject(j).getJSONObject("distance").getDouble("value");
                    }
                    else {
                        distanceBetweenPoints = 0;
                        HashMap<String, String> data = new HashMap<>();
                        data.put("distance", Double.toString(distance));
                        data.put("duration", Integer.toString(duration));
                        distanceDuration.add(data);
                    }
                }
                routes.add(distanceDuration);
            }
            return routes;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {}
        return null;
    }


    public JSONArray getJObjectRoutes(JSONObject jObject) throws IOException{
        List<List<HashMap<String, String>>> routes = parseJObjectLatLng(jObject);
        JSONArray jObjectRoutes = new JSONArray();
        for (int i = 0; i < routes.size(); i++) {
            List<HashMap<String, String>> route;
            route = routes.get(i);
            HashMap<String, String> hashMap;
            List<LatLng> latLng = new ArrayList<>();
            for (int j = 1; j < route.size() - 1; i++) {
                hashMap = route.get(j);
                latLng.add(new LatLng(Double.valueOf(hashMap.get("lat")), Double.valueOf(hashMap.get("lng"))));
            }
            jObjectRoutes.put(downloadUrl(getUrl(latLng, "driving")));
        }
        return jObjectRoutes;
    }

    public List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> getRouteData(JSONArray jArray) throws JSONException, IOException {
        List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> routesData = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            List<List<HashMap<String, String>>> latLng = parseJObjectLatLng(jArray.getJSONObject(i));
            List<List<HashMap<String, String>>> distanceDuration = parseJObjectDistanceDuration(jArray.getJSONObject(i));
            List<HashMap<String, String>> pointsLatLng = latLng.get(i);
            List<HashMap<String, String>> pointsDistanceDuration = distanceDuration.get(i);
            List points = new ArrayList();
            for (int j = 0; j < pointsLatLng.size() && i < pointsDistanceDuration.get(i).size(); j++) {
                HashMap<HashMap<String, String>, HashMap<String, String>> pointData = new HashMap<>();
                pointData.put(pointsLatLng.get(j), pointsDistanceDuration.get(j));
                points.add(pointData);
            }
            routesData.add(points);
        }
        Log.d("routesData:", routesData.toString());
        return routesData;
    }

    public List<List<HashMap<String, String>>> parseJObjectLatLng(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = new EncodedPolyline(polyline).decodePath();

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).lat));
                            hm.put("lng", Double.toString((list.get(l)).lng));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
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
            Log.d("mylog", "Downloaded URL: " + data);
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getUrl(List<LatLng> latLngs, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + latLngs.get(0).lat + "," + latLngs.get(0).lng;
        // Destination of route
        String str_dest = "destination=" + latLngs.get(latLngs.size()).lat + "," + latLngs.get(latLngs.size()).lng;
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