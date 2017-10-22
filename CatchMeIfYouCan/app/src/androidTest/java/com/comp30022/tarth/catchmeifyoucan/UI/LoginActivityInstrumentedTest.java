package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;
import com.comp30022.tarth.catchmeifyoucan.Server.WebSocketClient;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class LoginActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<LoginActivity> rule  = new ActivityTestRule<LoginActivity>(LoginActivity.class) {
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
        LoginActivity activity = rule.getActivity();
        View view;
        view = activity.findViewById(R.id.title);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.subhead);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.editTextUsername);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(EditText.class));
        view = activity.findViewById(R.id.editTextPassword);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(EditText.class));
        view = activity.findViewById(R.id.buttonLogin);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.registered);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
    }

}
