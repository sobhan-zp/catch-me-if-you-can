// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

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
