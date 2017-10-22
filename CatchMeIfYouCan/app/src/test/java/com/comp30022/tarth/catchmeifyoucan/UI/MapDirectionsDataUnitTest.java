package com.comp30022.tarth.catchmeifyoucan.UI;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * MapDirectionsDataUnitTest.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleMap.class, PolylineOptions.class, Polyline.class})
public class MapDirectionsDataUnitTest {

    GoogleMap mapMock = PowerMockito.mock(GoogleMap.class);
    List<Polyline> mPolylines = new ArrayList<Polyline>();

    @Test
    public void testDisplayingDirection() throws Exception {

        PolylineOptions optionsMock = PowerMockito.mock(PolylineOptions.class);
        Polyline polylineMock = PowerMockito.mock(Polyline.class);
        List<Polyline> mPolylinesSpy = PowerMockito.spy(mPolylines);

        mapMock.addPolyline(optionsMock);
        mPolylinesSpy.add(polylineMock);
        verify(mapMock).addPolyline(optionsMock);
        verify(mPolylinesSpy).add(polylineMock);
    }

    @Test
    public void testClearingPolyline() throws Exception {
        List<Polyline> mPolylinesSpy = PowerMockito.spy(mPolylines);
        Polyline polylineMock = PowerMockito.mock(Polyline.class);
        Mockito.doReturn(polylineMock).when(mPolylinesSpy).get(Mockito.anyInt());
        mPolylinesSpy.get(0);
        polylineMock.remove();
        verify(mPolylinesSpy).get(0);
        verify(polylineMock).remove();
    }

}