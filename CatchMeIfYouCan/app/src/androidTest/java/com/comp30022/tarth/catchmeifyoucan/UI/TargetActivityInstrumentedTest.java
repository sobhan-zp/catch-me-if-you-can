package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class TargetActivityInstrumentedTest {

    public TargetActivityInstrumentedTest() {
        TargetActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
    }

    @Rule
    public ActivityTestRule<TargetActivity> rule  = new ActivityTestRule<TargetActivity>(TargetActivity.class) {
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
        TargetActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
        View view;
        view = activity.findViewById(R.id.fragment_container);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(FrameLayout.class));
        view = activity.findViewById(R.id.navigation);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(BottomNavigationView.class));
        view = activity.findViewById(R.id.B_addWaypoints);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.B_finishAddWP);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
    }

}
