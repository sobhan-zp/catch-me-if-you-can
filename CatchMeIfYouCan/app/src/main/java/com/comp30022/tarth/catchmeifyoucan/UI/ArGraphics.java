package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.view.View;
import android.location.Location;
import android.hardware.SensorManager;
import java.math.*;


import com.google.android.gms.maps.model.Marker;


/**
 * Created by Jussi on 12/10/2017.
 */

public class ArGraphics extends View{

    //View width and height
    private int width;
    private int height;

    //Shape Constructors
    private Paint paint;
    private ShapeDrawable shape;

    //Graphic positioning constructors
    double xViewingAngle = 0.6859;
    double yViewingAngle = 0.9919;

    //Location tests
    private Location mCurrentLocation = new Location("");

    private Location mCurrLocationMarker = new Location ("");

    private float[] mOrientationAngles = new float[3];


    //private SurfaceHolder surfaceHolder;
    //private Bitmap bitmap;
    //private Canvas canvas;

    public ArGraphics(Context context, ArActivity arActivity){
        super(context);
        //surfaceHolder = getHolder();
        //surfaceHolder.addCallback(this);

        //Set paint style
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawCircle(calculateGraphicXPos(),calculateGraphicYPos(),0.1F*width, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        width = w;
        height = h;
    }

    private int calculateGraphicXPos(){
        int x;
        x = width/2 - (int) (mOrientationAngles[0]/xViewingAngle * width);
        return x;
    }

    private int calculateGraphicYPos(){
        int y;
        y = height/2 - (int) ((mOrientationAngles[1])/yViewingAngle * height);
        return y;
    }

    private float calculateGraphicSize(){
        float s;
        s = (0.05F*width)/(mCurrentLocation.distanceTo(mCurrLocationMarker));
        return s;
    }

    public void setOrientationAngles(float[] orientationAngles){
        mOrientationAngles = orientationAngles;
    }

    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){

    }
}
