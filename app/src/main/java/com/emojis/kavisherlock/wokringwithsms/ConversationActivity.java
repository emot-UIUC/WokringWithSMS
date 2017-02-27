package com.emojis.kavisherlock.wokringwithsms;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import org.emot.libcontrol.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversationActivity extends AppCompatActivity {

    RecyclerView conversation;
    private ArrayList<Text> personalMessages = new ArrayList<>();
    ConversationAdapter adapter;
    String number;
    SmsManager manager = SmsManager.getDefault();
    EditText input;
    IntentFilter intentFilter;
    LaunchSMS launch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        number = getIntent().getStringExtra("number");

        input = (EditText) findViewById(R.id.text_input);
        conversation = (RecyclerView) findViewById(R.id.conversation_texts);
        adapter = new ConversationAdapter(getApplicationContext(), personalMessages);
        conversation.setAdapter(adapter);
        conversation.setLayoutManager(new LinearLayoutManager(this));
        refreshConversation();
        conversation.scrollToPosition(personalMessages.size()-1);

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        launch = new LaunchSMS();
        Handler handle = new Handler();
        EmotControl.onCreate(this, handle);

    }

    public void refreshConversation(){
        ContentResolver contentResolver = getContentResolver();
        Cursor inboxCursor = contentResolver.query(Uri.parse("content://sms"), null, null, null, null);

        int indexBody = inboxCursor.getColumnIndex("body");
        int indexAddress = inboxCursor.getColumnIndex("address");
        int indexType = inboxCursor.getColumnIndex("type");

        if (indexBody < 0 || !inboxCursor.moveToFirst()) return;

        do{
            String str = inboxCursor.getString(indexBody);
            if (inboxCursor.getString(indexAddress).equals(number)){
                if (inboxCursor.getString(indexType).equals(String.valueOf(Telephony.Sms.MESSAGE_TYPE_INBOX))) {
                    personalMessages.add(new Text(str, number));
                }else{
                    personalMessages.add(new Text(str, "self"));
                }
            }
            inboxCursor.toString();
        }while(inboxCursor.moveToNext());
        Collections.reverse(personalMessages);
    }

    public void sendMessage(View v){
        String message = input.getText().toString();
        manager.sendTextMessage(number, null, message, null, null);
        personalMessages.add(new Text(message, "self"));
        adapter.notifyItemChanged(personalMessages.size()-1);
        input.setText("");
        String emoji = launch.extractEmoji(message);
        String emotion = launch.getEmotion(emoji);
        int color = launch.emotionToColor(emotion);
        if(emotion.equals("None")){
            message = EmojiParser.parseToAliases(message);
            System.out.println("message: "+message);
            Pattern pattern = Pattern.compile(":[a-z]+:");
            Matcher matcher = pattern.matcher(message);
            Emoji emoji1 = null;
            if(matcher.find()) {
                String alias = matcher.group(0);
                emoji1 = EmojiManager.getForAlias(alias.substring(1, alias.length()-1));
                System.out.println(emoji1.getTags());
            }
        }
        LinearLayout convo = (LinearLayout) findViewById(R.id.activity_conversation);
        convo.setBackgroundColor(color);
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("sms");
            String author = intent.getStringExtra("author");
            System.out.println("author: " +author);
            if(author.equals(number)){
                String emoji = launch.extractEmoji(message);
                String emotion = launch.getEmotion(emoji);
                int color = launch.emotionToColor(emotion);
                if(emotion.equals("None")){
                    message = EmojiParser.parseToAliases(message);
                    Pattern pattern = Pattern.compile(":[a-z]+:");
                    Matcher matcher = pattern.matcher(message);
                    Emoji emoji1 = null;
                    if(matcher.find()) {
                        String alias = matcher.group(0);
                        emoji1 = EmojiManager.getForAlias(alias.substring(1, alias.length()-1));
                        System.out.println(emoji1.getTags());
                    }
                }
                LinearLayout convo = (LinearLayout) findViewById(R.id.activity_conversation);
                convo.setBackgroundColor(color);
                switch(emotion){
                    case("Happy"):
                        EmotControl.setEmotion(Emotions.HAPPY);
                        break;
                    case("Sad"):
                        EmotControl.setEmotion(Emotions.SAD);
                        break;
                    case("Angry"):
                        EmotControl.setEmotion(Emotions.ANGRY);
                        break;
                    case("Shocked"):
                        EmotControl.setEmotion(Emotions.SURPRISE);
                        break;
                }
            }
        }
    };


    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(intentReceiver, intentFilter);
        EmotControl.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(intentReceiver);
        EmotControl.onPause();
    }
}
