package com.comp30022.tarth.catchmeifyoucan.UI;

import android.Manifest;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Button;
import android.hardware.Camera;
import android.content.pm.PackageManager;
import android.app.ActionBar.LayoutParams;


import com.comp30022.tarth.catchmeifyoucan.Game.ArCamera;
import com.comp30022.tarth.catchmeifyoucan.Game.ArGraphics;
import com.comp30022.tarth.catchmeifyoucan.R;

@SuppressWarnings("deprecation")
public class ArActivity extends AppCompatActivity implements SensorEventListener{
    private Camera camera;
    private ArCamera cameraView;
    private ArGraphics arGraphics;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    //Sensor Constructors
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magneticField;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

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

        //camera check for AR to function
        if(checkCameraHardware(this)) {
            camera = getCameraInstance();
            cameraView = new ArCamera(this, camera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(cameraView);

            //Set graphics
            arGraphics = new ArGraphics(this, this);
            arGraphics.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            FrameLayout graphicsView = (FrameLayout) findViewById(R.id.graphicsFrame);
            graphicsView.addView(arGraphics);

            //Set sensors
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }


        buttonMaps = (Button) findViewById(R.id.buttonMap);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();

       //  buttonMaps.setOnClickListener(new Button.OnClickListener() {
       //     @Override
       //     public void onClick(View v) {
       //         openMaps();
       //     }
       // });
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause(){
        super.onPause();
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
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



    //Required for sensors
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor == magneticField) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
        updateOrientationAngles();
        //Log.d("debug", "mAzimuth :" + Float.toString(mOrientationAngles[0]));
        arGraphics.setOrientationAngles(mOrientationAngles);
        arGraphics.invalidate();
    }

    //Required for Sensors
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        sensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        sensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, mRotationMatrix);
        // "mRotationMatrix" now has up-to-date information.


        sensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }

    public float[] getOrientationAngles(){
        return this.mOrientationAngles;
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
