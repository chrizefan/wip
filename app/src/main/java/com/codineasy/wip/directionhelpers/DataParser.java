package com.codineasy.wip.directionhelpers;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private static final String TAG = "DataParser";

    public int[] parseTotalDistance(JSONObject jObject) {
        JSONArray jRoutes;
        JSONArray jLegs;
        try {
            jRoutes = jObject.getJSONArray("routes");
            int[] distances = new int[jRoutes.length()];
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                int distance = 0;
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    distance += jLegs.getJSONObject(j).getJSONObject("distance").getInt("value");
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

    public List<HashMap<HashMap<String, String>, HashMap<String, String>>> parseJObjectData(JSONObject jObject) {
        List routeData  = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");
            jLegs = ((JSONObject) jRoutes.get(0)).getJSONArray("legs");
            int distance = 0;
            int duration = 0;
            /** Traversing all legs */
            for (int j = 0; j < jLegs.length(); j++) {
                HashMap<HashMap, HashMap> data = new HashMap<>();
                HashMap<String, String> latLng = new HashMap<>();
                latLng.put("lat", jLegs.getJSONObject(j).getJSONObject("end_location").getString("lat"));
                latLng.put("lng", jLegs.getJSONObject(j).getJSONObject("end_location").getString("lng"));
                HashMap<String, String> distanceDuration = new HashMap<>();
                distance += jLegs.getJSONObject(j).getJSONObject("distance").getInt("value");
                duration += jLegs.getJSONObject(j).getJSONObject("duration").getInt("value");
                distanceDuration.put("distance", Integer.toString(distance));
                distanceDuration.put("duration", Integer.toString(duration));
                data.put(latLng, distanceDuration);
                routeData.add(data);
                /** Traversing all steps */
                jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                for (int k = 0; k < jSteps.length(); k++) {

                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return routeData;
    }

    public static List<List<HashMap<String, String>>> parseJObjectLatLng(JSONObject jObject) {
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
}