package com.comp30022.tarth.catchmeifyoucan.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.comp30022.tarth.catchmeifyoucan.R;

public class EndActivity extends Activity {

    Button buttonReturn;
    Button buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end);

        buttonReturn = (Button) findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutWarning();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Reveals pop up asking if user really wants to exit
    public void logoutWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.exit_app_title));
        builder.setMessage(getResources().getString(R.string.exit_app_message));
        builder.setPositiveButton(getResources().getString(R.string.exit_dialog_pos), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logout();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exit_dialog_neg), null);
        builder.show();
    }

    // Disconnects from server and returns to main menu
    public void logout() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
