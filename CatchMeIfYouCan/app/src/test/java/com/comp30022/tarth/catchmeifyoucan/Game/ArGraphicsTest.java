package com.comp30022.tarth.catchmeifyoucan.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ArGraphics.class)
public class ArGraphicsTest {
    private Context context = PowerMockito.mock(Context.class);
    private ArGraphics testGraphics = new ArGraphics(context);

    @Test
    public void testCheckInCircle() throws Exception {
        testGraphics.setShapeX(0);
        testGraphics.setShapeY(0);
        assertTrue(testGraphics.checkInCircle(any(Float.class), any(Float.class)));
    }

    @Test
    public void testSetOrientationAngles() throws Exception {
        float[] testAngles = {1.4F, 2.34F, 0.5F};
        testGraphics.setOrientationAngles(testAngles);
        assertArrayEquals(testGraphics.getOrientationAngles(), testAngles, 0);
    }

    @Test
    public void testOnDraw(){
        Canvas mockCanvas = Mockito.mock(Canvas.class);
        Paint mockPaint = Mockito.mock(Paint.class);

        mockCanvas.drawCircle(0,0,1,mockPaint);
        verify(mockCanvas).drawCircle(0,0,1,mockPaint);
    }
}