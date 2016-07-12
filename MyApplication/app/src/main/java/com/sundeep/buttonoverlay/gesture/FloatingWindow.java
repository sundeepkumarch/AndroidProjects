package com.sundeep.buttonoverlay.gesture;

import android.app.Service;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;

public class FloatingWindow extends Service {

    public static String TAG = "com.sundeep.buttonoverlay.gesture.FloatingWindow";

    private WindowManager wm;
    private LinearLayout ll;
    private ImageView launchIcon;
    private boolean llVisible = false;

    WindowManager.LayoutParams llParams;

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

        launchIconParams.gravity = Gravity.CENTER;

        final LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.TRANSPARENT);
        ll.setLayoutParams(llParameters);
        ll.setVisibility(llVisible ? View.VISIBLE : View.INVISIBLE);
        ll.setBackgroundResource(R.drawable.custom_rectangle);

        llParams = new WindowManager.LayoutParams(
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
        wm.updateViewLayout(ll, llParams);

        launchIcon.setOnTouchListener(new View.OnTouchListener() {

            private WindowManager.LayoutParams updatedParams = launchIconParams;
            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {
                    // Single Tap
//                    swipeView.clear();
                    llVisible = !llVisible;
                    ll.setVisibility(llVisible ? View.VISIBLE : View.INVISIBLE);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    wm.getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;

                    llParams.height = 2 * height / 3;
                    llParams.width = width;

                    llParams.x = 0;
                    llParams.y = height / 3;

                    launchIconParams.x = (width / 2) - (launchIcon.getWidth() / 4);
                    launchIconParams.y = (-1 * (height / 6)) - (3 * launchIcon.getHeight() / 4);

                    wm.updateViewLayout(launchIcon, launchIconParams);

                    wm.updateViewLayout(ll, llParams);

                    return true;
                } else {
                    //Drag and Move
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x = updatedParams.x;
                            y = updatedParams.y;

                            touchedX = event.getRawX();
                            touchedY = event.getRawY();
                            Log.d(TAG, "ACTION_DOWN Touched X:" + touchedX + " Y:" + touchedY);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            updatedParams.x = (int) (x + (event.getRawX() - touchedX));
                            updatedParams.y = (int) (y + (event.getRawY() - touchedY));

                            Log.d(TAG, "ACTION_MOVE UpdatedParams X:" + updatedParams.x + " Y:" + updatedParams.y + " X=" + x + " Y=" + y);

                            wm.updateViewLayout(launchIcon, updatedParams);
                    }
                }

                return false;
            }
        });
    }

    private GestureOverlayView.OnGesturePerformedListener handleGestureListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureView, Gesture gesture) {
            gLib.load();
            ArrayList<Prediction> predictions = gLib.recognize(gesture);
            Log.d(TAG, "Recognized");

            // one prediction needed
            if (predictions.size() > 0) {
                Prediction prediction = predictions.get(0);
                // checking prediction
                if (prediction.score > 1.0) {
                    // and action
                    ll.setVisibility(View.INVISIBLE);
                    wm.updateViewLayout(ll, llParams);

                    Toast.makeText(FloatingWindow.this, prediction.name + ":" + Utility.gestureMap.size(), Toast.LENGTH_SHORT).show();
                    GestureIntentData intentData = new Utility(getApplicationContext()).getGesture(prediction.name);

                    Intent intent = new Intent();
                    Toast.makeText(FloatingWindow.this, "Starting intent:" + prediction.name, Toast.LENGTH_SHORT).show();

                    if (intentData.intentAction.length() != 0) {
                        intent.setAction(intentData.intentAction);
                    }
                    if (intentData.intentFlag != -1) {
                        intent.setFlags(intentData.intentFlag);
                    }
                    if (intentData.intentURI.length() != 0) {
                        intent.setData(Uri.parse(intentData.intentURI));
                    }

                    Log.d("FloatingWindow", "Action:" + intent.getAction());
                    Log.d("FloatingWindow", "Data_URI:" + intent.getData());
                    Log.d("FloatingWindow", "Flag:" + intent.getFlags());

                        /*Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        whatsappIntent.setData(Uri.parse("content://com.android.contacts/data/"));
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "I'm the body.");*/

                    if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }else{
                        Log.d(TAG,"No Activity found to handle Intent!!!");
                    }

                        /*Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SENDTO);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                        sendIntent.setType("text/plain");
                        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sendIntent.setPackage("com.whatsapp");
                        sendIntent.setData(Uri.parse("smsto:+919686643995"));
                        startActivity(sendIntent);*/


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
            Log.d(TAG, "Gesture: onDown");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "Gesture: onSingleTapUp");
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
