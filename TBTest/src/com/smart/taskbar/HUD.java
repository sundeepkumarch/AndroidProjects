package com.smart.taskbar;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class HUD extends Service implements OnTouchListener {
    Button mButton;
    @Override
    public IBinder
    onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //mView = new HUDView(this);
        mButton = new Button(this);
        mButton.setText("Overlay button");
        mButton.setOnTouchListener(this);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mButton, params);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(this,"Overlay button event", Toast.LENGTH_SHORT).show();
		return false;
	}
    
    
}
