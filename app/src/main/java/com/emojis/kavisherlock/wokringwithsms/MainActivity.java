package com.emojis.kavisherlock.wokringwithsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static private int REQUEST_PERMISSIONS = 1001;

    private ArrayList<String> texts = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messages = (ListView) findViewById(R.id.text_inbox);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, texts);
        messages.setAdapter(arrayAdapter);
        messages.setOnItemClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.SEND_SMS);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_SMS);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.VIBRATE);
            }
            if (permissions.size() > 0){
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
            }else{
                refreshTextInbox();
            }
        }

        //TODO: Make fab send create new text conversation. 
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /*texts.add(new Text("Hello"));
        texts.add(new Text("Bob"));
        texts.add(new Text("Text"));*/

//        InboxAdapter iAdapter = new InboxAdapter(this, R.layout.text_item);
//        ListView inbox_list = (ListView) findViewById(R.id.text_inbox);
//        inbox_list.setAdapter(iAdapter);
    }

//    public Text getItem(int position){
//        return texts.get(position);
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSIONS){
            refreshTextInbox();
        }
    }

    public void refreshTextInbox(){
        ContentResolver contentResolver = getContentResolver();
        Cursor inboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = inboxCursor.getColumnIndex("body");
        int indexAddress = inboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !inboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do{
            String str = inboxCursor.getString(indexAddress) +
                    "\n" + inboxCursor.getString(indexBody) + "\n";
            texts.add(str);
            System.out.println(str);
        }while(inboxCursor.moveToNext());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String text = texts.get(i);
        String number = text.substring(0,10);
        System.out.println(number);
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("number", number);
        startActivity(intent);
    }
}
