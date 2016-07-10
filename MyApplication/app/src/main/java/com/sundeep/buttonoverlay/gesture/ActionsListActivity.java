package com.sundeep.buttonoverlay.gesture;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sundeep.buttonoverlay.R;

import java.util.ArrayList;
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

        adapter = new ActionsListAdapter(this,0,listData);
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

    private void startSelectedActivity(ActionModel action){
        switch(action.getActionType()){
            case "CALL":
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_SELECT_CONTACT);
                }
                break;
            case "LAUNCH_APP":
                Log.d(TAG,"In LAUNCH_APP case:");
                List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
                ArrayList<AppListItem> appsList = new ArrayList<>();
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
                }
                Intent launchIntent = new Intent(ActionsListActivity.this, AppsListActivity.class);
//                launchIntent.putParcelableArrayListExtra("dataList",appsList);
                launchIntent.putExtra("dataList",appsList);
                Log.d(TAG,"Launching apps list size:"+appsList.size());
                startActivityForResult(launchIntent, REQUEST_LAUNCH_APP);
                break;
            case "WHATSAPP":
                ContentResolver cr = getApplicationContext().getContentResolver();

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
                                        myWhatsappContacts.add(number);

                                        Log.d(TAG, " WhatsApp contact id  :  " + id);
                                        Log.d(TAG, " WhatsApp contact name :  " + name);
                                        Log.d(TAG, " WhatsApp contact number :  " + number);
                                    }
                                }
                            } while (contactCursor.moveToNext());
                            contactCursor.close();
                        }
                    }
                }

                Log.d(TAG, " WhatsApp contact size :  " + myWhatsappContacts.size());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_SELECT_CONTACT:
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
                    returnIntent.putExtra("action_key", Utility.ACTION.CALL_NUMBER);
                    returnIntent.putExtra("action_value",number);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
                break;
            case REQUEST_LAUNCH_APP:
                if(resultCode == RESULT_OK){

                }

        }
    }

}
