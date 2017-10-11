package com.comp30022.tarth.catchmeifyoucan.UI;

import android.Manifest;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.hardware.Camera;
import android.content.pm.PackageManager;


import com.comp30022.tarth.catchmeifyoucan.R;

@SuppressWarnings("deprecation")
public class ArActivity extends AppCompatActivity{
    private Camera camera;
    private ArCamera cameraView;
    private Bitmap bitmap;
    private Canvas canvas;

    private Button buttonMaps;
    private static final int REQUEST_CAMERA_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        //Check camera permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        }

        //camera creation
        if(checkCameraHardware(this)) {
            camera = getCameraInstance();
            cameraView = new ArCamera(this, camera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(cameraView);
        }

        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        buttonMaps = (Button) findViewById(R.id.buttonChat);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();

       //  buttonMaps.setOnClickListener(new Button.OnClickListener() {
       //     @Override
       //     public void onClick(View v) {
       //         openMaps();
       //     }
       // });
    }

    public boolean checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    // Navigates to Maps activity
    // private void openMaps() {
    //     Intent intent = new Intent(this, MapsActivity.class);
    //     startActivity(intent);
    // }
}
