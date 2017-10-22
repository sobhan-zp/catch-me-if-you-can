package com.comp30022.tarth.catchmeifyoucan.UI;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * UIUnitTestSuite.java
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MapDataParserUnitTest.class,
        MapDirectionsDataUnitTest.class,
        MapDownloadURLUnitTest.class,
        SearcherActivityUnitTest.class,
        TargetActivityUnitTest.class
})
public class UIUnitTestSuite {
}
