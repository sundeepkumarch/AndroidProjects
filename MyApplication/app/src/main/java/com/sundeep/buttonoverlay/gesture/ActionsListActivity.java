package com.sundeep.buttonoverlay.gesture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.List;

public class ActionsListActivity extends AppCompatActivity {

    private RecyclerView actionsListView;
    private ActionsListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static List<ActionModel> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_list);

        actionsListView = (RecyclerView) findViewById(R.id.actionsList);
        actionsListView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        actionsListView.setLayoutManager(mLayoutManager);

        actionsListView.setAdapter(adapter);
    }


    private static void populateActionsData(){
//        listData.add(new ActionModel());
    }

}
