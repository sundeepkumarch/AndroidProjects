package com.sundeep.buttonoverlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingWindow extends Service {

    public static String TAG = "com.sundeep.buttonoverlay.FloatingWindow";

    private WindowManager wm;
    private LinearLayout ll;
    private Button stopButton;
    private ImageView launchIcon;
    private boolean llVisible = false;

    private int llWidth = 400;
    private int llHeight = 400;

    private SwipeView swipeView;

    private GestureDetector gestureDetector;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);

        stopButton = new Button(this);
        stopButton.setText("Stop");

        launchIcon = new ImageView(this);
        launchIcon.setImageResource(R.drawable.overlayicon_1);

        swipeView = new SwipeView(this);

        gestureDetector = new GestureDetector(this, new SingleTap());

        final LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.TRANSPARENT);
        ll.setLayoutParams(llParameters);
        ll.setVisibility(llVisible ? View.VISIBLE: View.INVISIBLE);
        ll.setBackgroundResource(R.drawable.custom_rectangle);

        ll.addView(swipeView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        final WindowManager.LayoutParams launchIconParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        launchIconParams.gravity = Gravity.CENTER ;

        final WindowManager.LayoutParams llParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

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
                    swipeView.clear();
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

    public class SwipeView extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public SwipeView(Context context) {
            super(context);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear(){
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path,paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX,eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++)
                    {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    Log.d("CaptureSwipeActivity","Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left){
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
