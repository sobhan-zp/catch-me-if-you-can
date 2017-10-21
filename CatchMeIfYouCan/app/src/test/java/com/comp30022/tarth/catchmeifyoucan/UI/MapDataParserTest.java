package com.comp30022.tarth.catchmeifyoucan.UI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MapDataParserTest {

    private MapDataParser mapDataParser = new MapDataParser();

    @Test
    public void testGetingtPaths() throws Exception {
        JSONArray googleStepsJsonMock = Mockito.mock(JSONArray.class);
        JSONObject googlePathJsonMock = Mockito.mock(JSONObject.class);
        JSONObject googlePathJOMock = Mockito.mock(JSONObject.class);

        Mockito.when(googleStepsJsonMock.length()).thenReturn(3);
        Mockito.when(googleStepsJsonMock.getJSONObject(Mockito.anyInt())).thenReturn(googlePathJsonMock);
        Mockito.when(googlePathJsonMock.getJSONObject("polyline")).thenReturn(googlePathJOMock);
        Mockito.when(googlePathJOMock.getString("points")).thenReturn("a");

        String[] t = new String[3];
        for(int i=0; i<3; i++){
            t[i] = "a";
        }

        String[] polylines;
        polylines = mapDataParser.getPaths(googleStepsJsonMock);
        assertArrayEquals(t, polylines);
    }

    @Test
    public void testGettingPath() throws Exception {
        JSONObject googlePathJsonMock = Mockito.mock(JSONObject.class);
        JSONObject googlePathJOMock = Mockito.mock(JSONObject.class);
        Mockito.when(googlePathJsonMock.getJSONObject("polyline")).thenReturn(googlePathJOMock);
        Mockito.when(googlePathJOMock.getString("points")).thenReturn("the point");
        String polyline = "";
        polyline = mapDataParser.getPath(googlePathJsonMock);
        assertEquals("the point", polyline);
    }

}