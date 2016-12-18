package com.emojis.kavisherlock.wokringwithsms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {

    ListView conversation;
    private ArrayList<String> personalMessages = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    String number;
    SmsManager manager = SmsManager.getDefault();
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        number = getIntent().getStringExtra("number");

        input = (EditText) findViewById(R.id.text_input);
        conversation = (ListView) findViewById(R.id.conversation_texts);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personalMessages);
        conversation.setAdapter(arrayAdapter);
        refreshConversation();
    }

    public void refreshConversation(){
        ContentResolver contentResolver = getContentResolver();
        Cursor inboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = inboxCursor.getColumnIndex("body");
        int indexAddress = inboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !inboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do{
            String str = inboxCursor.getString(indexAddress) +
                    "\n" + inboxCursor.getString(indexBody) + "\n";
            if (inboxCursor.getString(indexAddress).equals(number)){
                personalMessages.add(str);
            }
        }while(inboxCursor.moveToNext());
    }

    public void sendMessage(View v){
        manager.sendTextMessage(number, null, input.getText().toString(), null, null);
        personalMessages.add(input.getText().toString());
        input.setText("");
    }
}
