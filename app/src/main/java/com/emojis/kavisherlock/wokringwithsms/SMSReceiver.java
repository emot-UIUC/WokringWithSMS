package com.emojis.kavisherlock.wokringwithsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.List;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        String author = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            assert pdus != null;
            msgs = new SmsMessage[pdus.length];
            if(Build.VERSION.SDK_INT >= 19){
                msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                str = msgs[msgs.length-1].getMessageBody();
                author += msgs[msgs.length-1].getOriginatingAddress();
            }else{
                for (int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str += msgs[i].getMessageBody();
                    author += msgs[i].getOriginatingAddress();
                }
            }
            //---display the new SMS message---
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("sms", str);
            broadcastIntent.putExtra("author", author);
            context.sendBroadcast(broadcastIntent);
        }
    }
}