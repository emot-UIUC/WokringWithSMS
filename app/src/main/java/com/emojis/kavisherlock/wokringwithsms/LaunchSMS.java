package com.emojis.kavisherlock.wokringwithsms;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.serialportexample.UsbService;

import java.lang.ref.WeakReference;
import java.util.Set;

public class LaunchSMS extends AppCompatActivity {
    private UsbService usbService;
    IntentFilter intentFilter;
    String messageText;
    int prev_back = 0xffffffff;
    int the_back;
    LinearLayout Received;
    private MyHandler mHandler;

    public String extractEmoji (String message) {
        String emoji = "";
        int n = message.length();
        int u = 0xD83D;
        for(int i=0; i<n; i++) {
            char aChar = message.charAt(i);
            if (aChar == '>') {
                emoji += aChar;
                emoji += message.charAt(i + 1);
                emoji += message.charAt(i + 2);
                i+=2;
                return emoji;
            }
            else if ((aChar == ':' || aChar == ';' || aChar == '=')) {
                emoji += aChar;
                if (message.charAt(i + 1) == '-') {
                    emoji += message.charAt(i + 2);
                    return emoji;
                }
                else if (message.charAt(i + 1) == ' ')
                    emoji = "";
                else {
                    emoji += message.charAt(i + 1);
                    return emoji;
                }
            }
            else if ((int)aChar == u) {
                emoji += aChar;
                emoji += message.charAt(i + 1);
                return emoji;
            }
        }
        return emoji;
    }

    public String getEmotion (String emoji) {
        if (emoji == "")
            return "None";
        Received = (LinearLayout) findViewById(R.id.layout1);
        String emotion = "";
        int u = 0xD83D;
        if (emoji.length() == 0) {
            emotion = "No Emoji";
            Received.setBackgroundColor(Color.WHITE);
            return emotion;
        }
        float[] orange = new float[3];
        orange[0] = 48;
        orange[1] = 73;
        orange[2] = 100;
        float[] pink = new float[3];
        pink[0] = 330;
        pink[1] = 59;
        pink[2] = 100;
        char x = emoji.charAt(0);
        char ychar = emoji.charAt(1);
        if ((int)x == u) {
            int y = (int)ychar & 0x00FF;
            if ((y >= 0 && y <= 15 && y != 13) || (y > 26 && y < 30) || (y >= 56 && y <= 60))
                emotion = "Happy";
            else if (y == 49 || y == 50 || y == 64)
                emotion = "Shocked";
            else if ((y >= 23 && y <= 26) || y == 13)
                emotion = "Love";
            else if ((y >= 18 && y <= 22) || y == 31 || y == 34 || y == 35 || (y >= 37 && y <= 51))
                emotion = "Sad";
            else if (y == 32 || y == 33 || y == 62)
                emotion = "Angry";
            else if (y==75)
                emotion = "One Hand Up";
            else if (y==76 || y==70)
                emotion = "Two Hands Up";
            else if (y>=77 && y <= 79)
                emotion = "D Hands Down";
            else
                emotion = "Neutral";
        }
        else {
            if (emoji.compareTo(":)") == 0 || emoji.compareTo(":D") == 0 || emoji.compareTo("=)") == 0 || emoji.compareTo(":>") == 0 || emoji.compareTo(";)") == 0 || emoji.compareTo("B)") == 0)
                emotion = "Happy";
            else if (emoji.compareTo(":(") == 0 || emoji.compareTo(":<") == 0 || emoji.compareTo(":,(") == 0 || emoji.compareTo(":/") == 0)
                emotion = "Sad";
            else if (emoji.compareTo(">:<") == 0 || emoji.compareTo(">:(") == 0 || emoji.compareTo(">:O") == 0 || emoji.compareTo(">:o") == 0)
                emotion = "Angry";
            else if (emoji.compareTo(":*") == 0)
                emotion = "Love";
            else
                emotion = "Neutral";

        }
        return emotion;
    }

    public int emotionToColor(String emotion) {
        switch (emotion) {
            case "Happy":   return 0xff66cd00;
            case "Sad":     return 0xffa6a6a6;
            case "Angry":   return 0xffff0000;
            case "Love":    return 0xffff3399;
            case "Shocked": return 0xff0066ff;
            default:        return 0xffffffff;
        }
    }

    public String emojiToFile(String emoji) {
        String file_name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (emoji.isEmpty())
                return "f610";
        }
        char x1 = emoji.charAt(0);
        char x2 = emoji.charAt(1);
        if ((int)x1 == 0xD83D) {
            int y = (int) x2 & 0x00FF;
            if (y < 16)
                file_name = "f60" + Integer.toHexString(y);
            else
                file_name = "f6" + Integer.toHexString(y);
            return file_name;
        }
        else {
            if (emoji.compareTo(":)") == 0 || emoji.compareTo("=)") == 0 || emoji.compareTo(":>") == 0)
                return "f60a";
            else if (emoji.compareTo(":D") == 0 )
                return "f600";
            else if (emoji.compareTo(";)") == 0)
                return "f609";
            else if (emoji.compareTo("B)") == 0)
                return "f60e";
            else if (emoji.compareTo(":(") == 0 || emoji.compareTo(":/") == 0)
                return "f626";
            else if (emoji.compareTo(":<") == 0)
                return "f616";
            else if (emoji.compareTo(":,(") == 0)
                return "f622";
            else if (emoji.compareTo(">:<") == 0 || emoji.compareTo(">:(") == 0 || emoji.compareTo(">:O") == 0 || emoji.compareTo(">:o") == 0)
                return "f621";
            else if (emoji.compareTo(":*") == 0)
                return "f618";
            else
                return "f610";
        }

    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onReceive(Context context, Intent intent) {
            //---display the SMS received in the TextView---
            doThings(intent.getExtras().getString("sms"));

            //Vibrator vtr = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            //long pattern[]={0,200,200,400,100,200,200,1000,200,200,200,200};
            //vtr.vibrate(pattern,-1);
            //Toast.makeText(getApplicationContext(), "Phone is Vibrating", Toast.LENGTH_LONG).show();

        }
    };

    public void displayHappy(View v) {
        displayEmoji(":D");
    }

    public void displaySad(View v) {
        displayEmoji(":<");
    }

    public void displayAngry(View v) {
        displayEmoji(">:(");
    }

    public void displayLove(View v) {
        displayEmoji(":*");
    }

    public void displayEmoji(String s) {
        EditText message = (EditText) findViewById(R.id.editText1);
        String m = message.getText().toString();
        m += (" ");
        m += (s);
        doThings(m);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void doThings (String s) {
        TextView SMSes = (TextView) findViewById(R.id.textView1);

        SMSes.setText(s);
//        SMSes.append("\n (");

        SMSes.setTextColor(0xFF00FFFF);
        String emoji = extractEmoji(s);
        String emotion = getEmotion(emoji);
//        SMSes.append(emotion);
//        SMSes.append(")\n");
        ImageView image = (ImageView) findViewById(R.id.test_image);
        String image_file = emojiToFile(emoji);
        int resID = getResources().getIdentifier(image_file, "drawable", getPackageName());
        image.setImageResource(resID);
        if(usbService != null) // if UsbService was correctly binded, Send data
            usbService.write(emotion.getBytes());

        the_back = emotionToColor(emotion);
        ObjectAnimator colorFade = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            colorFade = ObjectAnimator.ofObject(Received, "backgroundColor", new ArgbEvaluator(), prev_back, the_back);
        }
        prev_back = the_back;

        colorFade.setDuration(700);
        colorFade.start();
    }

    //private EditText messageNumber;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_sms);
        //messageNumber=(EditText)findViewById(R.id.messageNumber);
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        mHandler = new MyHandler(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch_sm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        setFilters();  // Start listening notifications from USBService
        startService(UsbService.class, usbConnection, null); // Start USBService(if it was not started before) and Bind it
        super.onResume();
    }
    @Override
    protected void onPause() {
        //---unregister the receiver---
        unregisterReceiver(intentReceiver);
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }


    public void sendMessage(View v) {


        //String _messageNumber="2179745723";
        String _messageNumber="7326499845";

        messageText = "Hello There. Send me a Text or Tweet to Me :)";
        Toast.makeText(getApplicationContext(), "SMS sent!", Toast.LENGTH_SHORT).show();

        /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + _messageNumber));
        sendIntent.putExtra("sms_body", messageText);
        startActivity(sendIntent);
        finish();
*/

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(_messageNumber, null, messageText, null, null);

    }



    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras)
    {
        if(UsbService.SERVICE_CONNECTED == false)
        {
            Intent startService = new Intent(this, service);
            if(extras != null && !extras.isEmpty())
            {
                Set<String> keys = extras.keySet();
                for(String key: keys)
                {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to USBService. Dara received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler
    {
        private final WeakReference<LaunchSMS> mActivity;

        public MyHandler(LaunchSMS activity)
        {
            mActivity = new WeakReference<LaunchSMS>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    //mActivity.get().display.append(data);
                    break;
            }
        }
    }

    /*
     * Notifications from USBService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
            if(arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_GRANTED)) // USB PERMISSION GRANTED
            {
                Toast.makeText(arg0, "USB Ready", Toast.LENGTH_SHORT).show();
            }else if(arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)) // USB PERMISSION NOT GRANTED
            {
                Toast.makeText(arg0, "USB Permission not granted", Toast.LENGTH_SHORT).show();
            }else if(arg1.getAction().equals(UsbService.ACTION_NO_USB)) // NO USB CONNECTED
            {
                Toast.makeText(arg0, "No USB connected", Toast.LENGTH_SHORT).show();
            }else if(arg1.getAction().equals(UsbService.ACTION_USB_DISCONNECTED)) // USB DISCONNECTED
            {
                Toast.makeText(arg0, "USB disconnected", Toast.LENGTH_SHORT).show();
            }else if(arg1.getAction().equals(UsbService.ACTION_USB_NOT_SUPPORTED)) // USB NOT SUPPORTED
            {
                Toast.makeText(arg0, "USB device not supported", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1)
        {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            usbService = null;
        }
    };
}
