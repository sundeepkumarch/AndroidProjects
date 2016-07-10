package com.sundeep.buttonoverlay.gesture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;

public class AppsListActivity extends AppCompatActivity {

    private static String TAG = "AppsListActivity";

    private ListView appsListView;
    private AppsListAdapter adapter;
    private static ArrayList<AppListItem> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        appsListView = (ListView) findViewById(R.id.appsList);

//        listData = getIntent().getParcelableArrayListExtra("dataList");
        listData = (ArrayList<AppListItem>) getIntent().getSerializableExtra("dataList");
        Log.d(TAG,"ListDataSize:"+listData.size());
        adapter = new AppsListAdapter(this, 0 , listData);
        appsListView.setAdapter(adapter);

        registerForContextMenu(appsListView);

        /*appsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppListItem app = listData.get(position);
                Toast.makeText(getApplicationContext(), app.getAppname() + " is selected!", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("app",app);
                setResult(RESULT_OK,resultIntent);
            }
        });*/
    }
}
