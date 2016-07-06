package com.sundeep.buttonoverlay.gesture;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.Set;

public class GestureListActivity extends AppCompatActivity {

    private static final String TAG = "GestureListActivity";
    private String mCurrentGestureName;
    private ListView mGestureListView;
    private ArrayList<GestureHolder> mGestureList;
    private GestureAdapter mGestureAdapter;
    private GestureLibrary gLib;
    private String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);

        openOptionsMenu();

        mGestureListView = (ListView) findViewById(R.id.gestures_list);
        mGestureList = new ArrayList<>();
        prepareList();
        mGestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        mGestureListView.setLongClickable(true);
        mGestureListView.setAdapter(mGestureAdapter);

        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(mGestureListView);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG,"onResume");
        setContentView(R.layout.activity_gesture_list);

        openOptionsMenu();

        mGestureListView = (ListView) findViewById(R.id.gestures_list);
        mGestureList = new ArrayList<>();
        prepareList();
        mGestureAdapter = new GestureAdapter(mGestureList, GestureListActivity.this);
        mGestureListView.setLongClickable(true);
        mGestureListView.setAdapter(mGestureAdapter);

        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(mGestureListView);
    }

    private void prepareList() {
        try {
            mGestureList = new ArrayList<GestureHolder>();
            gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
            gLib.load();
            Set<String> gestureSet = gLib.getGestureEntries();
            for(String gestureName: gestureSet){
                ArrayList<Gesture> list = gLib.getGestures(gestureName);
                for(Gesture g : list) {
                    mGestureList.add(new GestureHolder(g, gestureName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateMenu(View view){
        LinearLayout vwParentRow = (LinearLayout)view.getParent().getParent();
        TextView tv = (TextView)vwParentRow.findViewById(R.id.gestureNameRef);
        mCurrentGestureName = tv.getText().toString();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.gesture_item_options, popup.getMenu());
        popup.show();
    }

    public void addButtonClick(View view) {
        Intent saveGesture = new Intent(GestureListActivity.this, SaveGestureActivity.class);
        startActivity(saveGesture);
    }

    public void testButtonClick(View view) {
        Intent testGesture = new Intent(GestureListActivity.this, GestureActivity.class);
        startActivity(testGesture);
    }

    public void deleteButtonClick(MenuItem item){
        gLib.removeEntry(mCurrentGestureName);
        gLib.save();
        mCurrentGestureName = "";
        onResume();
    }

    public void renameButtonClick(MenuItem item){

        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle(getString(R.string.enterNewName));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);

        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().matches("")) {
                    newName = nameField.getText().toString();
                    saveGesture();
                } else {
                    renameButtonClick(null);  //TODO : validation
//                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newName = "";
                mCurrentGestureName = "";
                return;
            }
        });

        namePopup.show();
    }

    private void saveGesture() {
        ArrayList<Gesture> list = gLib.getGestures(mCurrentGestureName);
        if (list.size() > 0) {
            gLib.removeEntry(mCurrentGestureName);
            gLib.addGesture(newName, list.get(0));
            if (gLib.save()) {
                Log.e(TAG, "gesture renamed!");
                onResume();
            }
        }
        newName = "";

    }
}
