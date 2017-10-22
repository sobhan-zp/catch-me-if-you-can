// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * MapDataParser.java
 * Handles Google Maps JSON data communication
 */
public class MapDataParser {

    /**
     * Parses JSONObjects
     * @param jsonData
     * @return
     */
    public String[] parseDirections(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    /**
     * Returns paths
     * @param googleStepsJson
     * @return
     */
    public String[] getPaths(JSONArray googleStepsJson) {
        if (googleStepsJson != null) {
            int count = googleStepsJson.length();
            String[] polylines = new String[count];

            for (int i = 0; i < count; i++) {
                try {
                    polylines[i] = getPath(googleStepsJson.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return polylines;
        }
        return new String[0];
    }

    /**
     * Returns a path
     * @param googlePathJson
     * @return
     */
    public String getPath(JSONObject googlePathJson) {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polyline;
    }

}
