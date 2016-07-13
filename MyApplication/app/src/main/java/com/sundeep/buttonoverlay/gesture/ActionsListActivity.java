package com.sundeep.buttonoverlay.gesture;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionsListActivity extends AppCompatActivity {

    private ListView actionsListView;
    private ActionsListAdapter adapter;
    private static ArrayList<ActionModel> listData = new ArrayList<>();

    private static String TAG = "ActionsListActivity";
    static final int REQUEST_SELECT_CONTACT = 1;
    static final int REQUEST_LAUNCH_APP = 2;
    static final int REQUEST_WHATSAPP_CONTACT = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_list);

        actionsListView = (ListView) findViewById(R.id.actionsList);

        listData = Utility.getActionsData();

        adapter = new ActionsListAdapter(this, 0, listData);
        actionsListView.setAdapter(adapter);

        actionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActionModel action = listData.get(position);
                Toast.makeText(getApplicationContext(), action.getActionTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                startSelectedActivity(action);
            }
        });
        registerForContextMenu(actionsListView);
    }

    private void startSelectedActivity(ActionModel action) {
        switch (action.getActionType()) {
            case "CALL":
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_SELECT_CONTACT);
                }
                break;
            case "LAUNCH_APP":
                Log.d(TAG, "In LAUNCH_APP case:");
                showAppsListPopup();



                /*List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
                for(int i=0;i<packs.size();i++) {
                    PackageInfo p = packs.get(i);
                    if((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        AppListItem app = new AppListItem();
                        app.setAppname(p.applicationInfo.loadLabel(getPackageManager()).toString());
                        Log.d(TAG, "AppName:" + p.applicationInfo.loadLabel(getPackageManager()).toString());
                        app.setPname(p.packageName);
                        app.setVersionName(p.versionName);
                        app.setVersionCode(p.versionCode);
                        app.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
                        appsList.add(app);
                    }
                }*/

                break;
            case "PLAY_MUSIC":
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if(mAudioManager.isMusicActive()) {
                    Intent i = new Intent("com.android.music.musicservicecommand");
                    i.putExtra("command" , "togglepause" );
                    sendBroadcast(i);
                    
                }
                break;
            case "WHATSAPP":

                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setPackage("com.whatsapp");
                startActivityForResult(pickIntent, REQUEST_WHATSAPP_CONTACT);
                /*ContentResolver cr = getApplicationContext().getContentResolver();

                //RowContacts for filter Account Types
                Cursor contactCursor = cr.query(
                        ContactsContract.RawContacts.CONTENT_URI,
                        new String[]{ContactsContract.RawContacts._ID,
                                ContactsContract.RawContacts.CONTACT_ID},
                        ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                        new String[]{"com.whatsapp"},
                        null);

                //ArrayList for Store Whatsapp Contact
                ArrayList<String> myWhatsappContacts = new ArrayList<>();

                if (contactCursor != null) {
                    if (contactCursor.getCount() > 0) {
                        if (contactCursor.moveToFirst()) {
                            do {
                                //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                                String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));

                                if (whatsappContactId != null) {
                                    //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                                    Cursor whatsAppContactCursor = cr.query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                            new String[]{whatsappContactId}, null);

                                    if (whatsAppContactCursor != null) {
                                        whatsAppContactCursor.moveToFirst();
                                        String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                                        String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                        String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                        whatsAppContactCursor.close();

                                        //Add Number to ArrayList
                                        myWhatsappContacts.add(name + "~" + number);
                                    }
                                }
                            } while (contactCursor.moveToNext());
                            contactCursor.close();
                        }
                    }
                }
                Collections.sort(myWhatsappContacts);
                for (String a : myWhatsappContacts) {
                    Log.d(TAG, "---------------------------------------->" + a);
                }
                Log.d(TAG, " WhatsApp contact size :  " + myWhatsappContacts.size());*/
                break;
            case "OPEN_SETTINGS":
                //startActivity(new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS));
                //https://developer.android.com/reference/android/provider/Settings.html
                break;

        }
    }

    private void showAppsListPopup(){
        AlertDialog.Builder appsListDialog = new AlertDialog.Builder(ActionsListActivity.this);
        appsListDialog.setIcon(R.mipmap.ic_launcher);
        appsListDialog.setTitle("Choose an App");

        PackageManager pm = getPackageManager();
        final ArrayList<AppListItem> appsList = new ArrayList<>();
        List<ApplicationInfo> applications = pm.getInstalledApplications(PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                AppListItem app = new AppListItem();
                app.setIcon(pm.getApplicationIcon(appInfo));
                app.setPname(appInfo.packageName);
                Log.d(TAG, appInfo.packageName);
                app.setAppname(String.valueOf(pm.getApplicationLabel(appInfo)));
                appsList.add(app);
            }
        }

        final AppsListAdapter adapter  = new AppsListAdapter(this, 0 , appsList);

        appsListDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        appsListDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppListItem app = appsList.get(which);
                Log.d(TAG,"Selected App:"+app.getAppname()+"~"+app.getPname());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action_key", Utility.ACTION.LAUNCH_APP);
                returnIntent.putExtra("action_value", app.getAppname()+"~"+app.getPname());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        appsListDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_CONTACT:
                if (resultCode == RESULT_OK) {
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    Toast.makeText(getApplicationContext(), "Calling " + number, Toast.LENGTH_LONG).show();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("action_key", Utility.ACTION.CALL_NUMBER);
                    returnIntent.putExtra("action_value", number);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                break;
            case REQUEST_WHATSAPP_CONTACT:
                if (resultCode == RESULT_OK) {
                    if(data.hasExtra("contact")){
                        String address = data.getStringExtra("contact");
                        Log.d(TAG, "The selected Whatsapp address is: "+address);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("action_key", Utility.ACTION.OPEN_WHATSAPP_CONTACT);
                        returnIntent.putExtra("action_value", address.split("@")[0]);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }

        }
    }

}
