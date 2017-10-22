// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.view.View;

import com.comp30022.tarth.catchmeifyoucan.R;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * TargetActivityUnitTest.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TargetActivity.class)
public class TargetActivityUnitTest {

    TargetActivity targetActivity = new TargetActivity();

    @Test
    public void onClick() throws Exception {
        View vMock = Mockito.mock(View.class);

        Mockito.when(vMock.getId()).thenReturn(R.id.B_addWaypoints);
        targetActivity.onClick(vMock);
        assertEquals(true, targetActivity.addWaypoints);

        Mockito.when(vMock.getId()).thenReturn(R.id.B_finishAddWP);
        targetActivity.onClick(vMock);
        assertEquals(false, targetActivity.addWaypoints);
    }

    @Test
    public void getLocation() throws Exception {
        JSONObject jsonObjectMock = Mockito.mock(JSONObject.class);

        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.put(Mockito.anyString(), Mockito.anyDouble())).thenReturn(jsonObjectMock);

        TargetActivity spy = Mockito.spy(targetActivity);
        Mockito.doNothing().when(spy).onSend(jsonObjectMock);

        spy.getLocation();
        verify(spy).onSend(jsonObjectMock);
    }


    @Test
    public void sendWaypoints() throws Exception {
        JSONObject jsonObjectMock = Mockito.mock(JSONObject.class);
        targetActivity.cWaypoints = new ArrayList<Double>();
        targetActivity.cWaypoints.add(1.1);
        targetActivity.cWaypoints.add(1.1);

        PowerMockito.whenNew(JSONObject.class).withNoArguments().thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.put(Mockito.anyString(), Mockito.anyDouble())).thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.put(Mockito.anyString(), Mockito.anyString())).thenReturn(jsonObjectMock);
        Mockito.when(jsonObjectMock.put(Mockito.anyString(), eq(jsonObjectMock))).thenReturn(jsonObjectMock);

        TargetActivity spy = Mockito.spy(targetActivity);
        Mockito.doNothing().when(spy).onSend(jsonObjectMock);

        spy.sendWaypoints();
        verify(spy).onSend(jsonObjectMock);
    }


}