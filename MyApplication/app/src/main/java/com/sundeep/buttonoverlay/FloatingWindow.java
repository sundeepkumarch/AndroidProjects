package com.sundeep.buttonoverlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by sundeep on 6/30/2016.
 */
public class FloatingWindow extends Service {

    Button mButton;
    private WindowManager wm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mButton = new Button(this);
        mButton.setText("Overlay button");


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.setTitle("Load Average");
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mButton, params);

        mButton.setOnTouchListener(new View.OnTouchListener() {
            int x, y;
            float touchedX,touchedY;
            private WindowManager.LayoutParams updatedParams = params;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(FloatingWindow.this,"Overlay button event", Toast.LENGTH_SHORT).show();

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParams.x;
                        y = updatedParams.y;

                        touchedX = event.getX();
                        touchedY = event.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        updatedParams.x = (int)(x + (event.getX()-touchedX));
                        updatedParams.y = (int)(y + (event.getY()-touchedY));

                        wm.updateViewLayout(mButton,updatedParams);
                }
                return false;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mButton != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mButton);
            mButton = null;
        }
    }
}
