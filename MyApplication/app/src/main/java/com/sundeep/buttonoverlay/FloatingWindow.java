package com.sundeep.buttonoverlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatingWindow extends Service {

    public static String TAG = "com.sundeep.buttonoverlay.FloatingWindow";

    private WindowManager wm;
    private LinearLayout ll;
    private Button button;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);

        button = new Button(this);
        button.setText("Overlay button");

        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.BLUE);
        ll.setLayoutParams(llParameters);
        ll.setVisibility(View.INVISIBLE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.TYPE_PHONE,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                        PixelFormat.TRANSLUCENT);

        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.CENTER ;

        final WindowManager.LayoutParams llParams = new WindowManager.LayoutParams(
                400,
                400,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        llParams.x = 0;
        llParams.y = 100;
        llParams.gravity = Gravity.TOP | Gravity.LEFT ;

        wm.addView(button, params);
        wm.addView(ll, llParams);
        wm.updateViewLayout(ll,llParams);

        button.setOnTouchListener(new View.OnTouchListener() {

            private WindowManager.LayoutParams updatedParams = params;
            int x , y;
            float touchedX, touchedY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                        Log.d(TAG,"ACTION_DOWN UpdatedParams X:"+updatedParams.x+" Y:"+updatedParams.y);
                        wm.updateViewLayout(button,updatedParams);
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindow.this,"Overlay button Clicked", Toast.LENGTH_SHORT).show();
                ll.setVisibility(View.VISIBLE);
//                wm.removeViewImmediate(button);
                wm.updateViewLayout(ll,llParams);

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

