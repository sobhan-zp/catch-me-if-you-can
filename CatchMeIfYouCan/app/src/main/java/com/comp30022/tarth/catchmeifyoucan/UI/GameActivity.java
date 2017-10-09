package com.comp30022.tarth.catchmeifyoucan.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.comp30022.tarth.catchmeifyoucan.Game.ChatFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.MapFragment;
import com.comp30022.tarth.catchmeifyoucan.Game.OptionsFragment;
import com.comp30022.tarth.catchmeifyoucan.R;

public class GameActivity extends AppCompatActivity {

    private final static int CHAT_ITEM_ID = 0;
    private final static int MAP_ITEM_ID = 1;
    private final static int OPTIONS_ITEM_ID = 1;

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(MAP_ITEM_ID).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switchFragment(item);
                return true;
            }
        });

        MenuItem item = navigation.getMenu().getItem(MAP_ITEM_ID);
        switchFragment(item);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = navigation.getMenu().getItem(MAP_ITEM_ID);
        int selectedItemId = navigation.getSelectedItemId();
        if (selectedItemId != homeItem.getItemId()) {
            navigation.getMenu().getItem(MAP_ITEM_ID).setChecked(true);
            switchFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void switchFragment(MenuItem item) {
        Fragment fragment = new Fragment();
        switch (item.getItemId()) {
            case R.id.navigationChat:
                fragment = new ChatFragment();
                break;
            case R.id.navigationMap:
                fragment = new MapFragment();
                break;
            case R.id.navigationOptions:
                fragment = new OptionsFragment();
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}
