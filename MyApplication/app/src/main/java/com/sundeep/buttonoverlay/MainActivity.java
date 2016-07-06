package com.sundeep.buttonoverlay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sundeep.buttonoverlay.gesture.GestureListActivity;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button capture;
    private Button gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.start);

        capture = (Button) findViewById(R.id.capture);

        gesture = (Button) findViewById(R.id.gesture);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FloatingWindow.class);
                startService(intent);
                finish();
            }
        });

        capture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureSwipeActivity.class);
                startActivity(intent);
            }
        });

        gesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GestureListActivity.class);
                startActivity(intent);
            }
        });
    }
}
