package com.comp30022.tarth.catchmeifyoucan.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.view.View;
import android.location.Location;

import com.google.android.gms.maps.model.Marker;

import static java.lang.Math.toRadians;


/**
 * Created by Jussi on 12/10/2017.
 */

public class ArGraphics extends View{

    //View width and height
    private int width;
    private int height;

    //Shape variables
    private float shapeX;
    private float shapeY;
    private float velocityX;
    private float velocityY;
    private float shapeRad;
    private static int NUM_UPDATES = (int)(ArActivity.LOCATION_UPDATE_RATE/ArActivity.GRAPHIC_UPDATE_RATE);
    private int timesMoved = 0;

    //Shape Constructors
    private Paint paint;
    private ShapeDrawable shape;

    //Graphic positioning constructors
    double xViewingAngle = 0.6859;
    double yViewingAngle = 0.9919;

    //Location tests
    private Location mCurrentLocation = new Location("Melbourne");
    private Location mCurrLocationMarker = new Location ("NearMelbourne");

    //Phone orientation information
    private float[] preOrientationAngles = new float[3];
    private float[] mOrientationAngles = new float[3];


    //private SurfaceHolder surfaceHolder;
    //private Bitmap bitmap;
    //private Canvas canvas;

    public ArGraphics(Context context, ArActivity arActivity){
        super(context);

        //Example Locations
        mCurrentLocation.setLatitude(-37.8136);
        mCurrentLocation.setLongitude(144.9631);

        mCurrLocationMarker.setLatitude(-37.81355);
        mCurrLocationMarker.setLongitude(144.9631);

        //Set paint style
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAlpha(200);
    }

    @Override
    protected void onDraw(Canvas canvas){

        this.shapeX = calculateGraphicXPos(preOrientationAngles);
        this.shapeY = calculateGraphicYPos(preOrientationAngles);
        this.shapeRad = calculateGraphicSize();

        canvas.drawCircle(this.shapeX+velocityX*timesMoved, this.shapeY+velocityY*timesMoved, this.shapeRad, paint);
        if(timesMoved<NUM_UPDATES){
            timesMoved++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        width = w;
        height = h;
    }

    private float calculateGraphicXPos(float[] orientationAngles){
        float x;
        x = width/2 - (int) ((orientationAngles[0]-
                toRadians(mCurrentLocation.bearingTo(mCurrLocationMarker)))/xViewingAngle * width);
        return x;
    }

    private float calculateGraphicYPos(float[] orientationAngles){
        float y;
        y = height/2 - (int) ((orientationAngles[1])/yViewingAngle * height);
        return y;
    }

    private float calculateGraphicSize(){
        float s;
        s = (0.5F*width)/(mCurrentLocation.distanceTo(mCurrLocationMarker));
        //Log.d("debug", "distance :" + Float.toString(mCurrentLocation.distanceTo(mCurrLocationMarker)));
        return s;
    }

    public void setOrientationAngles(float[] orientationAngles){
        Log.d("debug", "setAngles");
        timesMoved = 0;
        preOrientationAngles = mOrientationAngles.clone();
        mOrientationAngles = orientationAngles.clone();
        velocityX = (calculateGraphicXPos(mOrientationAngles)
                - calculateGraphicXPos(preOrientationAngles))/NUM_UPDATES;
        velocityY = (calculateGraphicYPos(mOrientationAngles)
                - calculateGraphicYPos(preOrientationAngles))/NUM_UPDATES;
    }

    public void setMarkerLocation(Location location){
        this.mCurrLocationMarker.set(location);
    }

    public boolean checkInCircle(float x, float y){
        boolean inCircle = false;
        if(x >= (this.shapeX - this.shapeRad) && x <= (this.shapeX + this.shapeRad)){
            if(y >= (this.shapeY - this.shapeRad) && y <= (this.shapeY + this.shapeRad)){
                inCircle = true;
                //Log.d("debug", "inCircle " );
            }
        }
        //else { Log.d("debug", "notInCircle ");}
        return inCircle;
    }
}
