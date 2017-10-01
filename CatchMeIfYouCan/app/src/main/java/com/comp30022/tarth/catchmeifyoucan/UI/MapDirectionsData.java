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

public class MapDirectionsData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    LatLng latLng;
    List<Polyline> mPolylines = new ArrayList<Polyline>();

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

    @Override
    protected void onPostExecute(String s) {
        String[] directionsList;
        MapDataParser parser = new MapDataParser();
        directionsList = parser.parseDirections(s);
        displayDirection(directionsList);
    }

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

    public void clearPolyline(){
        int count = mPolylines.size();
        for(int i = count-1;i>=0;i--) {
            mPolylines.get(i).remove();
        }
    }
}
