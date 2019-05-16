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

    public static int[] parseTotalDistance(JSONObject jObject) {
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

    public static int[] parseTotalDuration(JSONObject jObject) {
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

    public static List<HashMap<HashMap<String, String>, HashMap<String, String>>> parseJObjectRouteData(JSONObject jObject) {
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
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return routeData;
    }

    public static List<List<HashMap<HashMap<String, String>, HashMap<String, String>>>> parseJObjectStepsData(JSONObject jObject) {
        List stepsData  = new ArrayList<>();
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
                        HashMap<HashMap, HashMap> data = new HashMap<>();
                        HashMap<String, String> instructions = new HashMap<>();
                        instructions.put("html_instructions", jSteps.getJSONObject(k).optString("html_instructions"));
                        instructions.put("maneuver", jSteps.getJSONObject(k).optString("maneuver"));
                        HashMap<String, String> distanceDuration = new HashMap<>();
                        distanceDuration.put("distance", Integer.toString(jSteps.getJSONObject(k).getJSONObject("distance").optInt("value")));
                        distanceDuration.put("duration", jSteps.getJSONObject(k).getJSONObject("duration").optString("text"));
                        data.put(instructions, distanceDuration);
                        path.add(data);
                    }
                }
                stepsData.add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stepsData;
    }
}