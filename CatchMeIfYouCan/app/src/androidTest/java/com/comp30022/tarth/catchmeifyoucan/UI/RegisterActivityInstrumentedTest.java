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

public class RegisterActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<RegisterActivity> rule  = new ActivityTestRule<RegisterActivity>(RegisterActivity.class) {
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
        RegisterActivity activity = rule.getActivity();
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
        view = activity.findViewById(R.id.editTextName);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(EditText.class));
        view = activity.findViewById(R.id.editTextEmail);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(EditText.class));
        view = activity.findViewById(R.id.buttonRegister);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(Button.class));
        view = activity.findViewById(R.id.signedup);
        assertThat(view, notNullValue());
        assertThat(view, instanceOf(TextView.class));
    }

}
