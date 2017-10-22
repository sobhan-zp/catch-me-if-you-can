package com.comp30022.tarth.catchmeifyoucan;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * ApplicationInstrumentedTest.java
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.comp30022.tarth.catchmeifyoucan", appContext.getPackageName());
    }

}
