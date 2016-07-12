package com.sundeep.buttonoverlay.gesture;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

public class GestureSaveActivity extends AppCompatActivity {

    private GestureLibrary gLib;
    private static final String TAG = "GestureSaveActivity";
    private boolean mGestureDrawn;
    private Gesture mCurrentGesture;
    private String mGesturename;

    private TextView gestureActionView;
    private TextView gestureNameView;

    private String intent_action_value;
    private Utility.ACTION intent_action_key;

    private static int ACTION_RESULT_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_gesture);

        gestureNameView = (TextView) findViewById(R.id.newGestureName);
        gestureActionView = (TextView) findViewById(R.id.newGestureAction);

        Log.d(TAG, "path = " + Environment.getExternalStorageDirectory().getAbsolutePath());

        openOptionsMenu();

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gLib.load();

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.save_gesture);
        gestures.addOnGestureListener(mGestureListener);

        resetEverything();
    }

    public void onSave(View view){
        if (mGestureDrawn) {
            if (!gestureNameView.getText().toString().matches("")) {
                mGesturename = gestureNameView.getText().toString();
                gLib.addGesture(mGesturename, mCurrentGesture);
                if (!gLib.save()) {
                    Log.e(TAG, "Gesture not saved!!");
                } else {
                    showToast(getString(R.string.gesture_saved) + getExternalFilesDir(null) + "/gesture.txt");
                    Log.d(TAG,"Gesture:"+mGesturename + " saved.");
                    saveIntent();
                }
                finish();
            } else {
                //TODO : set name field with old name string user added
                showToast(getString(R.string.invalid_name));
            }
        }
    }

    public void saveIntent(){

        switch (intent_action_key){
            case CALL_NUMBER:
                //Phone Call Intent
                GestureIntentData data = new GestureIntentData();
                data.id = mGesturename;
                data.intentAction = Intent.ACTION_CALL;
                data.intentFlag = Intent.FLAG_ACTIVITY_NEW_TASK;
                data.intentURI = "tel:"+intent_action_value.replaceAll("\\s+","");
                Log.d("GestureSaveActivity","URI:"+data.intentURI);
                new Utility(getApplicationContext()).saveGesture(data);
                break;
            case SEND_MSG:

        }

    }

    public void onCancel(View view){
        reDrawGestureView();
        finish();
    }

    public void onNewAction(View view) {
        Intent intent = new Intent(GestureSaveActivity.this, ActionsListActivity.class);
        startActivityForResult(intent, ACTION_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_RESULT_CODE && resultCode == RESULT_OK) {
            intent_action_value = data.getStringExtra("action_value");
            intent_action_key = (Utility.ACTION)data.getExtras().get("action_key");
            switch (intent_action_key){
                case CALL_NUMBER:
                    gestureActionView.setText("Calling "+intent_action_value);
            }

        }
    }

    private void saveGestureAsImage(){
        //TODO : Save gesture as image, dont delete this code
                /*
                String pattern = "mm ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String time = formatter.format(new Date());
                String path = ("/d-codepages" + time + ".png");

                File file = new File(Environment.getExternalStorageDirectory()
                        + path);

                try {
                    //DrawBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    //new FileOutputStream(file));
                    Toast.makeText(this, "File Saved ::" + path, Toast.LENGTH_SHORT)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(this, "ERROR" + e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }   */
    }

    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mGestureDrawn = true;
            Log.d(TAG, "onGestureStarted");
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            mCurrentGesture = overlay.getGesture();
        }

        @Override
        public void onGestureEnded(GestureOverlayView gestureView, MotionEvent motion) {
            Log.d(TAG, "onGestureEnded");
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            Log.d(TAG, "onGestureCancelled");
        }
    };

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void resetEverything() {
        mGestureDrawn = false;
        mCurrentGesture = null;
        mGesturename = "";
    }

    private void reDrawGestureView() {
        setContentView(R.layout.activity_save_gesture);
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.save_gesture);
        gestures.removeAllOnGestureListeners();
        gestures.addOnGestureListener(mGestureListener);
        resetEverything();
    }
}
