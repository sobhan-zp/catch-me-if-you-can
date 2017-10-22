// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * GamelistActivityInstrumentedTest.java
 */
public class GamelistActivityInstrumentedTest {

    public GamelistActivityInstrumentedTest() {
        GamelistActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
    }

    @Rule
    public ActivityTestRule<GamelistActivity> rule = new ActivityTestRule<GamelistActivity>(GamelistActivity.class);

    @Test
    public void testActivitySetUp() {
        GamelistActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
        View view;
        ListAdapter adapter;
        view = activity.findViewById(R.id.listViewGames);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(ListView.class));
        adapter = ((ListView) view).getAdapter();
        assertThat(adapter, instanceOf(ArrayAdapter.class));
        view = activity.findViewById(R.id.textViewGames);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
    }

}
