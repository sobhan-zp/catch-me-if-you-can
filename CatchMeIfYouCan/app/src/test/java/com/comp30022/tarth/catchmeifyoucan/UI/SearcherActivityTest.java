package com.comp30022.tarth.catchmeifyoucan.UI;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SearcherActivity.class,Marker.class})
public class SearcherActivityTest {

    SearcherActivity searcherActivity = new SearcherActivity();

    @Test
    public void onMarkerClick() throws Exception {
        Marker markerMock = PowerMockito.mock(Marker.class);

        Mockito.when(markerMock.getId()).thenReturn("");

        SearcherActivity spy = Mockito.spy(searcherActivity);
        Mockito.doNothing().when(spy).openAR();
        Mockito.doNothing().when(markerMock).remove();

        spy.theWpId = "";
        assertEquals(true, spy.onMarkerClick(markerMock));
    }

    @Test
    public void getTargetLocation() throws Exception {
        JSONObject jsonObjectMock = Mockito.mock(JSONObject.class);

        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.put(Mockito.anyString(), Mockito.anyDouble())).thenReturn(jsonObjectMock);

        SearcherActivity spy = Mockito.spy(searcherActivity);
        Mockito.doNothing().when(spy).onSend(jsonObjectMock);

        spy.getTargetLocation();
        verify(spy).onSend(jsonObjectMock);
    }

    @Test
    public void getWaypoints() throws Exception {
        JSONObject jsonObjectMock = Mockito.mock(JSONObject.class);

        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.put(Mockito.anyString(), Mockito.anyDouble())).thenReturn(jsonObjectMock);

        SearcherActivity spy = Mockito.spy(searcherActivity);
        Mockito.doNothing().when(spy).onSend(jsonObjectMock);

        spy.getWaypoints();
        verify(spy).onSend(jsonObjectMock);
    }

}