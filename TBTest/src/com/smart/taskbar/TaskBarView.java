package com.smart.taskbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TaskBarView extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent svc = new Intent(this, HUD.class);
        startService(svc);
        finish();
    }
}


