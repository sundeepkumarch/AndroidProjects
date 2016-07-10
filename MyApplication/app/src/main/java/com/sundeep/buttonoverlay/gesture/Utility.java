package com.sundeep.buttonoverlay.gesture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utility {

    private static String TAG = "Utility";
    public static Map<String, Intent> gestureMap = new HashMap<>();

    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor editor;

    public static final String PREFS_NAME = "com.sundeep.appname.PREFS";
    public static final String KEY_PREFIX = "com.sundeep.appname.KEY";
    private static final String KEY_INTENTACTION = "com.sundeep.appname.INTENT_ACTION";
    private static final String KEY_INTENTFLAG = "com.our.package.INTENT_FLAG";
    private static final String KEY_INTENTURI = "com.our.package.INTENT_URI";

    public enum ACTION {
        CALL_NUMBER,LAUNCH_APP,OPEN_URL,PLAY_MUSIC,SEND_MSG,OPEN_SETTINGS
    }

    public Utility(Context context) {
        if(sharedPrefs == null){
            sharedPrefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        }
    }

    public static ArrayList<ActionModel> getActionsData() {
        ArrayList<ActionModel> listData = new ArrayList<>();

        listData.add(new ActionModel(R.drawable.ic_account, "Direct dial", "CALL"));
        listData.add(new ActionModel(R.drawable.ic_android, "Open Application", "LAUNCH_APP"));
        listData.add(new ActionModel(R.drawable.ic_public, "Open URL", "OPEN_URL"));
        listData.add(new ActionModel(R.drawable.ic_music, "Play Music", "PLAY_MUSIC"));
        listData.add(new ActionModel(R.drawable.ic_message, "WhatsApp Message", "WHATSAPP"));
        listData.add(new ActionModel(R.drawable.ic_settings, "Settings Shortcut", "OPEN_SETTINGS"));

        return listData;
    }

    private String getFieldKey(String intentId,String intentField){
        return KEY_PREFIX + intentId + "_" + intentField;
    }

    public void saveGesture(GestureIntentData data){
       editor = sharedPrefs.edit();
        String id = data.id;
        editor.putString(getFieldKey(id,KEY_INTENTACTION),data.intentAction);
        editor.putInt(getFieldKey(id,KEY_INTENTFLAG),data.intentFlag);
        editor.putString(getFieldKey(id,KEY_INTENTURI),data.intentURI);
        editor.commit();
    }

    public Intent getGesture(String id){
        Intent intent = new Intent();
        String action = sharedPrefs.getString(getFieldKey(id,KEY_INTENTACTION),"");
        int flag = sharedPrefs.getInt(getFieldKey(id,KEY_INTENTFLAG),-1);
        String uri = sharedPrefs.getString(getFieldKey(id,KEY_INTENTURI),"");
        if(action.length() != 0) {
            Log.d(TAG,"ACTION:"+action);
            intent.setAction(action);
        }else{
            return null;
        }
        if(flag != -1) {
            Log.d(TAG,"FLAG:"+flag);
            intent.setFlags(flag);
        }else{
            return null;
        }
        if(uri.length() != 0) {
            Log.d(TAG,"URI:"+uri);
            intent.setData(Uri.parse(uri));
        }else{
            return null;
        }
        return intent;
    }
    public static void addGesture(String gestureName, Intent gesture){
        gestureMap.put(gestureName,gesture);
    }
}
