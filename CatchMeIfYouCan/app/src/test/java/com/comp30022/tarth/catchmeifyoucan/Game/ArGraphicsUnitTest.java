// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

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

/**
 * ArGraphicsUnitTest.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ArGraphics.class)
public class ArGraphicsUnitTest {
    private Context context = PowerMockito.mock(Context.class);
    private ArGraphics testGraphics = new ArGraphics(context);

    @Test
    public void testCheckInCircle() throws Exception {
        testGraphics.setShapeX(0);
        testGraphics.setShapeY(0);
        assertTrue(testGraphics.checkInCircle(0f, 0f));
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