package com.comp30022.tarth.catchmeifyoucan.Game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.Canvas;
import android.view.View;
import android.location.Location;

import com.comp30022.tarth.catchmeifyoucan.UI.ArActivity;

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
    private final double xViewingAngle = 0.70;
    private final double yViewingAngle = 1.2;

    //Location tests
    private Location mCurrentLocation = new Location("MyLocation");
    private Location mCurrLocationMarker = new Location ("WaypointLocation");

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

        mCurrLocationMarker.setLatitude(-37.81365);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        width = w;
        height = h;
    }

    private float calculateGraphicXPos(float[] orientationAngles){
        float x;
        float bearingAngle =  -3.14F; //(float)toRadians(mCurrentLocation.bearingTo(mCurrLocationMarker));
        //Log.d("debug", "bearingAngle1 :" + Float.toString(bearingAngle));
        if(bearingAngle>Math.PI){
            bearingAngle = -1* (float)((2*Math.PI) - bearingAngle);
        }

        if(orientationAngles[0] < 0  && bearingAngle < 0 ||
                orientationAngles[0] > 0  && bearingAngle > 0){
            x = width / 2 - (float) ((orientationAngles[0] - bearingAngle) / xViewingAngle * width);
        }
        else {
            x = width / 2 - (float) ((orientationAngles[0] + bearingAngle) / xViewingAngle * width);
        }

        return x;
    }
    private float calculateGraphicYPos(float[] orientationAngles){
        float y;
        y = height/2 - (int) ((orientationAngles[1])/yViewingAngle * height);
        //Log.d("debug", "height :" + Integer.toString(height));
        return y;
    }

    private float calculateGraphicSize(){
        float s;
        s = (0.5F*width)/(mCurrentLocation.distanceTo(mCurrLocationMarker));
        //Log.d("debug", "distance :" + Float.toString(mCurrentLocation.distanceTo(mCurrLocationMarker)));
        return s;
    }

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

    public void setWaypointLocation(double x, double y){
        mCurrLocationMarker.setLatitude(y);
        mCurrLocationMarker.setLongitude(x);
    }

    public void setCurrentLocation(double x, double y){
        mCurrentLocation.setLatitude(y);
        mCurrentLocation.setLongitude(x);
    }
}
