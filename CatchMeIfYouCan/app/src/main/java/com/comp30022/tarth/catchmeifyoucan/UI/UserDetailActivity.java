package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.comp30022.tarth.catchmeifyoucan.R;

public class UserDetailActivity extends Activity {

    ImageView backdropImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        backdropImg = (ImageView) findViewById(R.id.backdrop);
        backdropImg.setImageResource(R.drawable.p1);
    }

    // Disables back button -- you need to click logout to exit
    @Override
    public void onBackPressed() {
        finish();
    }


}

