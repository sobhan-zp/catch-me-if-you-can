// COMP30022 IT Project - Semester 2 2017
// House Tarth - William Voor Thursday 16.15
// | Ivan Ken Weng Chee         eyeonechi  ichee@student.unimelb.edu.au
// | Jussi Eemeli Silventoinen  JussiSil   jsilventoine@student.unimelb.edu.au
// | Minghao Wang               minghaooo  minghaow1@student.unimelb.edu.au
// | Vikram Gopalan-Krishnan    vikramgk   vgopalan@student.unimelb.edu.au
// | Ziren Xiao                 zirenxiao  zirenx@student.unimelb.edu.au

package com.comp30022.tarth.catchmeifyoucan.Game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.view.View;

import static java.lang.Math.toRadians;

/**
 * ArGraphics.java
 * Draws objects on top of ArCamera
 */
public class ArGraphics extends View{

    //UI Update timing handler
    public static long GRAPHIC_UPDATE_RATE = 50;
    public static long LOCATION_UPDATE_RATE = 250;

    //View width and height
    private int width;
    private int height;

    //Shape variables
    private float shapeX;
    private float shapeY;
    private float velocityX;
    private float velocityY;
    private float shapeRad;
    private static int NUM_UPDATES = (int)(LOCATION_UPDATE_RATE/GRAPHIC_UPDATE_RATE);
    private int timesMoved = 0;

    //Shape Constructors
    private Paint paint;
    private ShapeDrawable shape;

    //Graphic positioning constructors
    private final double xViewingAngle = 0.70;
    private final double yViewingAngle = 1.2;

    //Location tests
    private Location mCurrentLocation = new Location("MyLocation");
    private Location mCurrLocationMarker = new Location ("WaypointLocation");

    //Phone orientation information
    private float[] preOrientationAngles = new float[3];
    private float[] mOrientationAngles = new float[3];

    public ArGraphics(Context context){
        super(context);

        //Example Locations
        mCurrentLocation.setLatitude(-37.8136);
        mCurrentLocation.setLongitude(144.9631);

        mCurrLocationMarker.setLatitude(-37.81365);
        mCurrLocationMarker.setLongitude(144.9631);

        //Set paint style
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAlpha(200);
    }

    /**
     * Implement this to do your drawing
     * @param canvas : The canvas on which the background will be drawn
     */
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

    /**
     * Called when the size of this view has changed
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        width = w;
        height = h;
    }

    /**
     * Checks if selection is in circle radius
     * @param x
     * @param y
     * @return
     */
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

    /**
     * Calculates x-coordinate of the AR circle
     * @param orientationAngles
     * @return
     */
    private float calculateGraphicXPos(float[] orientationAngles){
        float x;
        float bearingAngle = (float)toRadians(mCurrentLocation.bearingTo(mCurrLocationMarker));
        //Log.d("debug", "bearingAngle1 :" + Float.toString(bearingAngle));
        if(bearingAngle>Math.PI){
            bearingAngle = -1* (float)((2*Math.PI) - bearingAngle);
        }

        if(orientationAngles[0] < 0  && bearingAngle < 0 ||
                orientationAngles[0] > 0  && bearingAngle > 0){
            x = width / 2 - (float) ((orientationAngles[0] - bearingAngle) / xViewingAngle * width);
        } else {
            x = width / 2 - (float) ((orientationAngles[0] + bearingAngle) / xViewingAngle * width);
        }

        return x;
    }

    /**
     * Calculates y-coordinate of the AR circle
     * @param orientationAngles
     * @return
     */
    private float calculateGraphicYPos(float[] orientationAngles){
        float y;
        y = height/2 - (int) ((orientationAngles[1])/yViewingAngle * height);
        //Log.d("debug", "height :" + Integer.toString(height));
        return y;
    }

    /**
     * Calculates the size of the graphic to be drawn
     * @return
     */
    private float calculateGraphicSize(){
        float s;
        s = (0.6F*width)/(mCurrentLocation.distanceTo(mCurrLocationMarker));
        if(s < 0.08F*width){
            s = 0.08F*width;
        }
        //Log.d("debug", "distance :" + Float.toString(mCurrentLocation.distanceTo(mCurrLocationMarker)));
        return s;
    }

    /**
     * Used to set the orientationAngles array from ArFragment
     * @param orientationAngles
     */
    public void setOrientationAngles(float[] orientationAngles){
        //Log.d("debug", "setAngles");
        timesMoved = 0;
        preOrientationAngles = mOrientationAngles.clone();
        mOrientationAngles = orientationAngles.clone();
        velocityX = (calculateGraphicXPos(mOrientationAngles)
                - calculateGraphicXPos(preOrientationAngles))/NUM_UPDATES;
        velocityY = (calculateGraphicYPos(mOrientationAngles)
                - calculateGraphicYPos(preOrientationAngles))/NUM_UPDATES;
    }

    /**
     * Sets destination location
     * @param x
     * @param y
     */
    public void setWaypointLocation(double x, double y){
        mCurrLocationMarker.setLatitude(y);
        mCurrLocationMarker.setLongitude(x);
    }

    /**
     * Sets current location
     * @param x
     * @param y
     */
    public void setCurrentLocation(double x, double y){
        mCurrentLocation.setLatitude(y);
        mCurrentLocation.setLongitude(x);
    }

    public void setShapeX(float x){
        this.shapeX = x;
    }

    public void setShapeY(float y){
        this.shapeY = y;
    }

    public float[] getOrientationAngles(){
        return mOrientationAngles;
    }

}
