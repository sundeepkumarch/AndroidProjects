package com.sundeep.buttonoverlay.gesture;

import android.content.Intent;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utility {

    public static Map<String, Intent> gestureMap = new HashMap<>();

    public static ArrayList<ActionModel> getActionsData() {
        ArrayList<ActionModel> listData = new ArrayList<>();

        listData.add(new ActionModel(R.drawable.ic_account, "Direct dial", "CALL"));
        listData.add(new ActionModel(R.drawable.ic_android, "Open Application", ""));
        listData.add(new ActionModel(R.drawable.ic_public, "Open URL", ""));
        listData.add(new ActionModel(R.drawable.ic_music, "Play Music", ""));
        listData.add(new ActionModel(R.drawable.ic_message, "Direct Message", ""));
        listData.add(new ActionModel(R.drawable.ic_settings, "Settings Shortcut", ""));

        return listData;
    }

    public void addGesture(String gestureName, Intent gesture){
        gestureMap.put(gestureName,gesture);
    }
}
