package com.sundeep.buttonoverlay.gesture;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.Set;

public class GestureListActivity extends AppCompatActivity {

    private static final String TAG = "GestureListActivity";
    private String mCurrentGestureName;
    private ArrayList<GestureHolder> mGestureList;
    private GestureLibrary gLib;
    private String newName;

    private RecyclerView gestureRecyclerView;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);

        openOptionsMenu();

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setContentView(R.layout.activity_gesture_list);

        openOptionsMenu();

        init();
    }

    public void init(){
        prepareList();

        gridLayoutManager = new GridLayoutManager(GestureListActivity.this, 2);
        gestureRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        gestureRecyclerView.setHasFixedSize(true);
        gestureRecyclerView.setLayoutManager(gridLayoutManager);

        GestureListAdapter rcAdapter = new GestureListAdapter(mGestureList, GestureListActivity.this);
        gestureRecyclerView.setAdapter(rcAdapter);

        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(gestureRecyclerView);
    }

    private void prepareList() {
        try {
            mGestureList = new ArrayList<>();
            gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
            gLib.load();
            Set<String> gestureSet = gLib.getGestureEntries();
            for (String gestureName : gestureSet) {
                ArrayList<Gesture> list = gLib.getGestures(gestureName);
                for (Gesture g : list) {
                    mGestureList.add(new GestureHolder(g, gestureName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateMenu(View view) {
        RelativeLayout vwParentRow = (RelativeLayout) view.getParent().getParent();
        TextView tv = (TextView) vwParentRow.findViewById(R.id.gesture_name_ref);
        mCurrentGestureName = tv.getText().toString();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.gesture_item_options, popup.getMenu());
        popup.show();
    }

    public void addButtonClick(View view) {
        Intent saveGesture = new Intent(GestureListActivity.this, GestureSaveActivity.class);
        startActivity(saveGesture);
    }


    public void deleteButtonClick(MenuItem item) {
        gLib.removeEntry(mCurrentGestureName);
        gLib.save();
        mCurrentGestureName = "";
        onResume();
    }

    public void renameButtonClick(MenuItem item) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play:
                Intent intent = new Intent(GestureListActivity.this, FloatingWindow.class);
                startService(intent);
                item.setIcon(R.drawable.ic_pause);
//                finish();
                break;
        }
        return true;
    }
}
