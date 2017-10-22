// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * MapDataParserUnitTest.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MapDataParser.class)
public class MapDataParserUnitTest {

    private MapDataParser mapDataParser = new MapDataParser();

    @Test
    public void testParsingDirections() throws Exception {
        String jsonData = "test string";
        JSONArray jsonArrayMock = Mockito.mock(JSONArray.class);
        JSONObject jsonObjectMock = Mockito.mock(JSONObject.class);

        // the expected string[]
        String[] t = new String[3];
        for(int i=0; i<3; i++){
            t[i] = "a";
        }

        PowerMockito.whenNew(JSONObject.class).withArguments(jsonData).thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.getJSONArray(Mockito.anyString())).thenReturn(jsonArrayMock);
        Mockito.when(jsonArrayMock.getJSONObject(Mockito.anyInt())).thenReturn(jsonObjectMock);

        MapDataParser spy = Mockito.spy(mapDataParser);
        Mockito.doReturn(t).when(spy).getPaths(jsonArrayMock);

        String[] directions = spy.parseDirections(jsonData);
        assertArrayEquals(t, directions);
    }

    @Test
    public void testGetingtPaths() throws Exception {
        JSONArray googleStepsJsonMock = Mockito.mock(JSONArray.class);
        JSONObject googlePathJsonMock = Mockito.mock(JSONObject.class);
        // the expected string[]
        String[] t = new String[3];
        for(int i=0; i<3; i++){
            t[i] = "a";
        }
        Mockito.when(googleStepsJsonMock.length()).thenReturn(3);
        Mockito.when(googleStepsJsonMock.getJSONObject(Mockito.anyInt())).thenReturn(googlePathJsonMock);

        MapDataParser spy = Mockito.spy(mapDataParser);
        Mockito.doReturn("a").when(spy).getPath(googlePathJsonMock);

        String[] polylines;
        polylines = spy.getPaths(googleStepsJsonMock);
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