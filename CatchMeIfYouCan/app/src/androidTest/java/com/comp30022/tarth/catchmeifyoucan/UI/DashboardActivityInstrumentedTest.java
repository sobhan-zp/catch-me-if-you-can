package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class DashboardActivityInstrumentedTest {

    public DashboardActivityInstrumentedTest() {
        DashboardActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
    }

    @Rule
    public ActivityTestRule<DashboardActivity> rule  = new ActivityTestRule<DashboardActivity>(DashboardActivity.class) {
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
        DashboardActivity activity = rule.getActivity();
        WebSocketClient.getClient().connect();
        WebSocketClient.getClient().setActivity(activity);
        View view;
        view = activity.findViewById(R.id.buttonCreate);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.buttonJoin);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.buttonFriendlist);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.buttonLogout);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.buttonSettings);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.imageViewTest);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(ImageView.class));
        view = activity.findViewById(R.id.Username);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
    }

    @Test
    public void testUsernameIsDisplayed() throws Exception {
        DashboardActivity activity = rule.getActivity();
        View view = activity.findViewById(R.id.Username);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        TextView textView = (TextView) view;
        assertThat(textView.getText().toString(), is("@somebody"));
    }

}
