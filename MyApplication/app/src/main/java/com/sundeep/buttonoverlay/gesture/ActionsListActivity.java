package com.sundeep.buttonoverlay.gesture;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.List;

public class ActionsListActivity extends AppCompatActivity {

    private RecyclerView actionsListView;
    private ActionsListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<ActionModel> listData = new ArrayList<>();

    static final int REQUEST_SELECT_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_list);

        actionsListView = (RecyclerView) findViewById(R.id.actionsList);
        actionsListView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        actionsListView.setLayoutManager(mLayoutManager);

        listData = Utility.getActionsData();

        adapter = new ActionsListAdapter(this,listData);
        actionsListView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        actionsListView.setAdapter(adapter);

        actionsListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), actionsListView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ActionModel action = listData.get(position);
                Toast.makeText(getApplicationContext(), action.getActionTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                startSelectedActivity(action);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        registerForContextMenu(actionsListView);
    }

    private void startSelectedActivity(ActionModel action){
        switch(action.getActionType()){
            case "CALL":
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_SELECT_CONTACT);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    Toast.makeText(getApplicationContext(), "Calling "+number, Toast.LENGTH_LONG).show();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("action","Calling "+number);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
                break;
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ActionsListActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ActionsListActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}
