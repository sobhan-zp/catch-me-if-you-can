// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

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
