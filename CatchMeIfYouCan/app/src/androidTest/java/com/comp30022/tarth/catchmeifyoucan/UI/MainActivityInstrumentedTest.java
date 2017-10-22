package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comp30022.tarth.catchmeifyoucan.R;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class MainActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> rule  = new ActivityTestRule<MainActivity>(MainActivity.class) {
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
        MainActivity activity = rule.getActivity();
        View view;
        view = activity.findViewById(R.id.textViewTitle);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.textViewSubTitle);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
        view = activity.findViewById(R.id.buttonLogin);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.buttonRegister);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.buttonExit);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
    }

}
