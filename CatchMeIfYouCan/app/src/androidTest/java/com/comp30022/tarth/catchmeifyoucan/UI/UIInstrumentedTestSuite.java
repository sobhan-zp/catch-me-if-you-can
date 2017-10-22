package com.comp30022.tarth.catchmeifyoucan.UI;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * UIInstrumentedTestSuite.java
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AddActivityInstrumentedTest.class,
        ChatActivityInstrumentedTest.class,
        DashboardActivityInstrumentedTest.class,
        EndActivityInstrumentedTest.class,
        FriendlistActivityInstrumentedTest.class,
        GamelistActivityInstrumentedTest.class,
        LoginActivityInstrumentedTest.class,
        MainActivityInstrumentedTest.class,
        RegisterActivityInstrumentedTest.class,
        SearcherActivityInstrumentedTest.class,
        SettingsActivityInstrumentedTest.class,
        TargetActivityInstrumentedTest.class,
        UserActivityInstrumentedTest.class
})
public class UIInstrumentedTestSuite {
}
