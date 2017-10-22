package com.comp30022.tarth.catchmeifyoucan.UI;

import android.Manifest;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.comp30022.tarth.catchmeifyoucan.Game.ArCamera;
import com.comp30022.tarth.catchmeifyoucan.Game.ArGraphics;
import com.comp30022.tarth.catchmeifyoucan.R;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class ArActivity extends Activity implements SensorEventListener, View.OnTouchListener {
    private Camera camera;
    private ArCamera cameraView;
    private ArGraphics arGraphics;

    //UI Update timing handler
    private ScheduledExecutorService graphicUpdateHandler;
    public static long GRAPHIC_UPDATE_RATE = 50;
    private ScheduledExecutorService locationUpdateHandler;
    public static long LOCATION_UPDATE_RATE = 250;

    //Ensure touches aren't registered multiple times
    private long lastTime = -1;
    private long touchPause = 1000;

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

    //Riddle Arrays
    private String[] riddlesArray;
    private String[] correctAnswers;
    private String[] wrongAnswers;
    private boolean[] usedRiddles;

    //Dialog Constructors
    private AlertDialog.Builder riddleDialog;
    private AlertDialog.Builder successDialog;
    private AlertDialog.Builder failDialog;
    private boolean correctAns = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ar);

        //Stop screen from dimming
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
            arGraphics = new ArGraphics(this);
            arGraphics.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            FrameLayout graphicsView = (FrameLayout) findViewById(R.id.graphicsFrame);
            graphicsView.addView(arGraphics);
            arGraphics.setOnTouchListener(this);

            //Set sensors
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


            //Initialize Dialog box and Riddle Arrays
            riddleDialog = new AlertDialog.Builder(this);
            successDialog = new AlertDialog.Builder(this);
            failDialog = new AlertDialog.Builder(this);
            Resources res = getResources();
            riddlesArray = res.getStringArray(R.array.riddles_array);
            usedRiddles = new boolean[riddlesArray.length];
            correctAnswers = res.getStringArray(R.array.correct_answers);
            wrongAnswers = res.getStringArray(R.array.wrong_answers);
        }
        ;
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        System.out.println(bd.get("SearcherLatitude"));
        System.out.println(bd.get("SearcherLongitude"));
        System.out.println(bd.get("TargetLatitude"));
        System.out.println(bd.get("TargetLongitude"));
    }

    // Set back button on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

        //Set update timing
        graphicUpdateHandler = Executors.newScheduledThreadPool(0);
        locationUpdateHandler = Executors.newScheduledThreadPool(0);
        startRepeatingTask();
    }

    @Override
    protected void onPause(){
        super.onPause();
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
        stopRepeatingTask();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
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
        //arGraphics.setOrientationAngles(mOrientationAngles);
    }

    //Required for Sensors
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        float[] newRotationMatrix = mRotationMatrix.clone();
        sensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        sensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, newRotationMatrix);
        // "mRotationMatrix" now has up-to-date information.

        sensorManager.getOrientation(newRotationMatrix, mOrientationAngles);
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


    private void startRepeatingTask(){
        graphicUpdateHandler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run(){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        arGraphics.invalidate();
                        //Log.d("debug", "invalidate");
                    }
                });
            }
        }, 0, GRAPHIC_UPDATE_RATE, TimeUnit.MILLISECONDS);

        locationUpdateHandler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run(){
                double x = 0;
                double y = 0;
                arGraphics.setOrientationAngles(mOrientationAngles);
                arGraphics.setWaypointLocation(x, y);
                //Log.d("debug", "set angles");
            }
        }, 0, LOCATION_UPDATE_RATE, TimeUnit.MILLISECONDS);
    }

    private void stopRepeatingTask(){
        locationUpdateHandler.shutdown();
        graphicUpdateHandler.shutdown();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        if(arGraphics.checkInCircle(event.getX(), event.getY())){
            if(lastTime < 0){
                lastTime = System.currentTimeMillis();
            }
            else{
                if(System.currentTimeMillis() - lastTime < touchPause){
                    return false; //Nothing happens
                }
                else {
                    correctAns = false;
                    lastTime = System.currentTimeMillis();
                    int i = (int) (Math.random() * (riddlesArray.length));
                    while(usedRiddles[i]){
                        i = (int) (Math.random() * (riddlesArray.length));
                    }
                    usedRiddles[i] = true;
                    final int ansPos = (int)(Math.random() * 4);
                    String[] answers = arrayCombine(correctAnswers[i], Arrays.copyOfRange(wrongAnswers, i*3, i*3 + 3), ansPos);
                    riddleDialog.setTitle(riddlesArray[i]).setSingleChoiceItems(answers, -1,
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    if(which == ansPos){
                                        correctAns = true;
                                    }
                                    else{
                                        correctAns = false;
                                    }
                                }
                    })
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(correctAns) {
                                successDialog.setMessage("Correct! Move to the next waypoint!");
                                successDialog.show();
                                //arGraphics.setMarkerLocation(somelocation);
                            }
                            else{
                                failDialog.setMessage("Wrong! Try again.");
                                riddleDialog.show();
                                failDialog.show();
                            }
                        }
                    });
                    riddleDialog.show();
                }
            }
        }
        return true;
    }

    public String[] arrayCombine(String a, String[] b, int ansPos){
        String[] result = new String[1 + b.length];
        result[ansPos] = a;
        int i = 0;
        for(int j = 0; j < 1 + b.length; j++){
            if(result[j] == null){
                result[j] = b[i];
                i++;
            }
        }
        return result;
    }

    // Resets the current activity connected to the WebSocket upon terminating child activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    // Navigates to Maps activity
    // private void openMaps() {
    //     Intent intent = new Intent(this, MapsActivity.class);
    //     startActivity(intent);
    // }
}
