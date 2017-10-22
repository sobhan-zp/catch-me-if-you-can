// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MapDirectionsData.java
 * Handles Google Directions Data communication
 */
public class MapDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    LatLng latLng;
    List<Polyline> mPolylines = new ArrayList<Polyline>();

    /**
     * Performs a computation on a background thread
     * @param objects
     * @return
     */
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];

        MapDownloadURL downloadUrl = new MapDownloadURL();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    /**
     * Runs on the UI thread after doInBackground(Params...)
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        String[] directionsList;
        MapDataParser parser = new MapDataParser();
        directionsList = parser.parseDirections(s);
        displayDirection(directionsList);
    }

    /**
     * Displays a route
     * @param directionsList
     */
    public void displayDirection(String[] directionsList) {
        int count = directionsList.length;
        for(int i = 0;i<count;i++) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            Polyline polyline = mMap.addPolyline(options);
            mPolylines.add(polyline);
        }
    }

    /**
     * Removes route display
     */
    public void clearPolyline(){
        int count = mPolylines.size();
        for(int i = count-1;i>=0;i--) {
            mPolylines.get(i).remove();
        }
    }

}
