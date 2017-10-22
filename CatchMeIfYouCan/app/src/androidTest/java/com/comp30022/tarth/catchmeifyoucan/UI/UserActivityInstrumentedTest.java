// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * UserActivityInstrumentedTest.java
 */
public class UserActivityInstrumentedTest {

    public UserActivityInstrumentedTest() {
        UserActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
    }

    @Rule
    public ActivityTestRule<UserActivity> rule  = new ActivityTestRule<UserActivity>(UserActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            InstrumentationRegistry.getTargetContext();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.putExtra("username", "somebody");
            return intent;
        }
    };

    @Test
    public void testActivitySetUp() throws Exception {
        UserActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
        View view;
        view = activity.findViewById(R.id.ProfilePicture);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(ImageView.class));
        view = activity.findViewById(R.id.Name);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.Username);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.Online);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.Location);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.StatusTitle);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.Status);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.floatingChat);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(FloatingActionButton.class));
    }

}
