// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.Game;

import android.hardware.Camera;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * ArFragmentUnitTest.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ArFragment.class)
public class ArFragmentUnitTest {
    private Camera cameraMock;
    private ArGraphics arGraphicsMock;
    private ArFragment arMock;

    @Test
    public void testStarRepeatingTask() throws Exception{
        arMock = Mockito.mock(ArFragment.class);
        ScheduledExecutorService mockHandler = Mockito.mock(ScheduledExecutorService.class);
        Runnable mockRunnable = Mockito.mock(Runnable.class);
        TimeUnit mockTimeUnit = Mockito.mock(TimeUnit.class);

        mockHandler.scheduleAtFixedRate(mockRunnable, 0, 0, mockTimeUnit);
        verify(mockHandler).scheduleAtFixedRate(mockRunnable, 0, 0, mockTimeUnit);
    }

    //Tests failure if touched recently
    @Test
    public void testOnTouch() throws Exception {
        arMock = Mockito.mock(ArFragment.class);
        View testView = PowerMockito.mock(View.class);
        MotionEvent testEvent = PowerMockito.mock(MotionEvent.class);
        arGraphicsMock = PowerMockito.mock(ArGraphics.class);

        PowerMockito.when(arGraphicsMock.checkInCircle(any(Float.TYPE),any(Float.TYPE))).thenReturn(true);
        arMock.setLastTime(5000);
        arMock.setTouchPause(0);
        assertFalse(arMock.onTouch(testView, testEvent));
    }

    @Test
    public void testArrayCombine(){
        arMock = new ArFragment();
        String a = "a";
        String[] arrayMock = {"b","c","d"};
        String[] arrayCompare = {"a","b","c","d"};
        String[] result = arMock.arrayCombine(a,arrayMock,0);
        assertArrayEquals(arrayCompare, result);
    }



}