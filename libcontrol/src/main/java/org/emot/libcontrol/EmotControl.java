package org.emot.libcontrol;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Wraps UsbSerial library by felHR85 (github.com/felHR85/UsbSerial)
 * Provides convenient interface for controlling the EMOT actuators.
 * <p>
 * Call onCreate, onPause, onResume at appropriate time of the app's lifecycle.
 *
 * @author Tiangang
 */
public class EmotControl {

    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    public static final int CTS_CHANGE = 1;
    public static final int DSR_CHANGE = 2;

    private EmotControl() {
    }

    private static EmotControl instance = new EmotControl();
    private UsbService usbService;
    private WeakReference<Activity> owner;
    private Handler mHandler;
    private ThreadPoolExecutor threadExecutor;
    private BlockingQueue<Runnable> threadQueue;
    private Future runningFuture;

    public static void onCreate(Activity caller, Handler handle) {
        instance.owner = new WeakReference<>(caller);
        instance.startService(UsbService.class, instance.usbConnection, null);
        instance.mHandler = handle;
        instance.threadQueue = new LinkedBlockingQueue<>();
        instance.threadExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, instance.threadQueue);
    }

    public static void onPause() {
        instance.owner.get().unregisterReceiver(mUsbReceiver);
        instance.owner.get().unbindService(instance.usbConnection);
    }

    public static void onResume() {
        instance.setFilters();
        instance.startService(UsbService.class, instance.usbConnection, null);
    }

    public static void setArm(Arms which, ArmActions action) {
        byte arm_byte = 0;
        byte action_byte = 0;

        switch (which) {
            case LEFT:
                arm_byte = 'l';
                break;
            case RIGHT:
                arm_byte = 'r';
                break;
        }

        switch (action) {
            case UP:
                action_byte = 'u';
                break;
            case STABLE:
                action_byte = 's';
                break;
            case DOWN:
                action_byte = 'd';
                break;
        }

        byte[] message = new byte[4];
        message[0] = 0;
        message[1] = arm_byte;
        message[2] = action_byte;
        message[3] = 0;

        sendBytes(message);
    }

    public static void setLed(Leds which, int r, int g, int b) {
        byte led_byte = 0;
        switch (which) {
            case LEFT:
                led_byte = 1;
                break;
            case RIGHT:
                led_byte = 2;
                break;
        }

        byte[] message = new byte[4];
        message[0] = led_byte;
        message[1] = (byte) r;
        message[2] = (byte) g;
        message[3] = (byte) b;

        sendBytes(message);
    }

    public static void setLed(Leds which, LedColors presetColor) {
        setLed(which, presetColor.r, presetColor.g, presetColor.b);
    }

    public static void setEmotion(Emotions which) {
        if (instance.runningFuture != null && !instance.runningFuture.isDone()) {
            instance.runningFuture.cancel(true);
        }
        instance.runningFuture = instance.threadExecutor.submit(AnimationFactory.getRunnable(which));
    }

    private static void setNewHandler(Handler customHandler) {
        instance.usbService.setHandler(customHandler);
    }

    private static void sendBytes(byte[] data) {
        instance.usbService.write(data);
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        owner.get().registerReceiver(mUsbReceiver, filter);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(owner.get(), service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            owner.get().startService(startService);
        }
        Intent bindingIntent = new Intent(owner.get(), service);
        owner.get().bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private static final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED:
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED:
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB:
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED:
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED:
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
