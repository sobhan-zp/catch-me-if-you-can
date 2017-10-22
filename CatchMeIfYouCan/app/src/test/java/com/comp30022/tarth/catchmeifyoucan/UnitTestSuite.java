package com.comp30022.tarth.catchmeifyoucan;

import com.comp30022.tarth.catchmeifyoucan.Game.GameUnitTestSuite;
import com.comp30022.tarth.catchmeifyoucan.UI.UIUnitTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * UnitTestSuite.java
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GameUnitTestSuite.class,
        UIUnitTestSuite.class
})
public class UnitTestSuite {
}
