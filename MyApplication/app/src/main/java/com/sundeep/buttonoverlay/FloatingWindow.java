package com.sundeep.buttonoverlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.net.Uri;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class FloatingWindow extends Service {

    public static String TAG = "com.sundeep.buttonoverlay.FloatingWindow";

    private WindowManager wm;
    private LinearLayout ll;
    private ImageView launchIcon;
    private boolean llVisible = false;

    private int llWidth = 400;
    private int llHeight = 400;

    private GestureDetector gestureDetector;
    private GestureLibrary gLib;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);

        launchIcon = new ImageView(this);
        launchIcon.setImageResource(R.drawable.overlayicon_1);

        gestureDetector = new GestureDetector(this, new SingleTap());

        final WindowManager.LayoutParams launchIconParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        launchIconParams.gravity = Gravity.CENTER ;

        final LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.TRANSPARENT);
        ll.setLayoutParams(llParameters);
        ll.setVisibility(llVisible ? View.VISIBLE: View.INVISIBLE);
        ll.setBackgroundResource(R.drawable.custom_rectangle);

        final WindowManager.LayoutParams llParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gLib.load();

        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        gestureOverlayView.setFadeOffset(1000);
        gestureOverlayView.setFadeEnabled(true);
        gestureOverlayView.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        gestureOverlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        gestureOverlayView.addOnGesturePerformedListener(handleGestureListener);
        gestureOverlayView.setGestureStrokeAngleThreshold(90.0f);

        ll.addView(gestureOverlayView);

        wm.addView(launchIcon, launchIconParams);
        wm.addView(ll, llParams);
        wm.updateViewLayout(ll,llParams);

        launchIcon.setOnTouchListener(new View.OnTouchListener() {

            private WindowManager.LayoutParams updatedParams = launchIconParams;
            int x , y;
            float touchedX, touchedY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {
                    // Single Tap
                    Toast.makeText(FloatingWindow.this,"Overlay button Clicked", Toast.LENGTH_SHORT).show();
//                    swipeView.clear();
                    llVisible = !llVisible;
                    ll.setVisibility(llVisible ? View.VISIBLE: View.INVISIBLE);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;

                    llParams.height = 2*height/3;
                    llParams.width = width;

                    llParams.x = 0;
                    llParams.y = height/3;

                    launchIconParams.x = (width/2)-(launchIcon.getWidth()/4);
                    launchIconParams.y =  (-1*(height/6))-(3*launchIcon.getHeight()/4);

                    wm.updateViewLayout(launchIcon,launchIconParams);

                    wm.updateViewLayout(ll,llParams);

                    return true;
                } else {
                    //Drag and Move
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            x = updatedParams.x;
                            y = updatedParams.y;

                            touchedX = event.getRawX();
                            touchedY = event.getRawY();
                            Log.d(TAG,"ACTION_DOWN Touched X:"+touchedX+" Y:"+touchedY);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            updatedParams.x = (int)(x + (event.getRawX()-touchedX));
                            updatedParams.y = (int)(y + (event.getRawY()-touchedY));

                            Log.d(TAG,"ACTION_MOVE UpdatedParams X:"+updatedParams.x+" Y:"+updatedParams.y+" X="+x+" Y="+y);

                            wm.updateViewLayout(launchIcon,updatedParams);
                    }
                }

                return false;
            }
        });
    }

    private GestureOverlayView.OnGesturePerformedListener handleGestureListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureView,Gesture gesture) {

            ArrayList<Prediction> predictions = gLib.recognize(gesture);
            Log.d(TAG, "Recognized");

            // one prediction needed
            if (predictions.size() > 0) {
                Prediction prediction = predictions.get(0);
                // checking prediction
                if (prediction.score > 1.0) {
                    // and action
                    Toast.makeText(FloatingWindow.this, prediction.name, Toast.LENGTH_SHORT).show();
                    if(prediction.name.equalsIgnoreCase("save")){
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callIntent.setData(Uri.parse("tel:+919743960300"));
                        startActivity(callIntent);
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class SingleTap implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG,"Gesture: onDown");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG,"Gesture: onSingleTapUp");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
