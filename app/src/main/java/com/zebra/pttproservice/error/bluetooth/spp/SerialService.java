package com.zebra.pttproservice.error.bluetooth.spp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.zebra.pttproservice.error.bluetooth.BTHelper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * create notification and queue serial data while activity is not in the foreground
 * use listener chain: SerialSocket -> SerialService -> UI fragment
 */
public class SerialService implements SerialListener {
    public static final String TAG = SerialService.class.getSimpleName();
    private static final long BUTTON_UP_INTERVAL = 100;
    private final SPPHeadSetMgr btListener;
    private boolean isLongPressed = false;
    private boolean isLongEmergencyButtonPressed = false;
    private long DOUBLE_PRESS_INTERVAL = 400;
    private long LOCK_PRESS_INTERVAL = 900;
    private final Handler handler = new Handler();
    private int clicks;
    private int emergencyButtonClicks;
    private boolean isBusy = false;
    private boolean isEmergencyButtonBusy = false;

    Runnable longPressedRunnable = new Runnable() {
        public void run() {
            isLongPressed = true;
            Log.d(TAG, "BTSupport: Long clicked!");
            btListener.onLongPress();
        }
    };

    Runnable singleAndDoubleRunnable = new Runnable() {
        public void run() {
            if (clicks > 1) {  // Double tap.
                Log.d(TAG, "BTSupport: Double clicked!");
                btListener.onDoubleTap();
            }
            if (clicks == 1) {  // Single tap
                Log.d(TAG, "BTSupport: Tap!");
                btListener.onTap();
            }
            //restore clicks count
            clicks = 0;
        }
    };


    Runnable emergencyButtonLongPressedRunnable = new Runnable() {
        public void run() {
            isLongEmergencyButtonPressed = true;
            Log.d(TAG, "BTSupport:EmergencyButton  Long clicked!");
            btListener.onEmergencyButtonLongPress();
        }
    };

    Runnable emergencySingleAndDoubleButtonRunnable = new Runnable() {
        public void run() {
            if (emergencyButtonClicks > 1) {  // Double tap.
                Log.d(TAG, "BTSupport: EmergencyButton Double clicked!");
                btListener.onEmergencyButtonDoubleTap();
            }
            if (emergencyButtonClicks == 1) {  // Single tap
                Log.d(TAG, "BTSupport: EmergencyButton Tap!");
                btListener.onEmergencyButtonTap();
            }
            //restore emergencyButtonClicks count
            emergencyButtonClicks = 0;
        }
    };

    Runnable buttonUpRunnable = new Runnable() {
        public void run() {
            btListener.onButtonUp();
        }
    };

    /*public void addCallback(IBTEventListener listener) {
        callbacks.add(listener);
    }

    public void removeCallback(IBTEventListener listener) {
        callbacks.remove(listener);
    }*/

    private enum QueueType {Connect, ConnectError, Read, IoError}

    private static class QueueItem {
        QueueType type;
        byte[] data;
        Exception e;

        QueueItem(QueueType type, byte[] data, Exception e) {
            this.type = type;
            this.data = data;
            this.e = e;
        }
    }

    private final Handler mainLooper;
    private final Queue<QueueItem> queue1, queue2;

    private SerialSocket socket;
    private SerialListener listener;
    private boolean connected;

    /**
     * Lifecylce
     */
    public SerialService(SPPHeadSetMgr btListener) {
        mainLooper = new Handler(Looper.getMainLooper());
        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();
        this.btListener = btListener;
    }

    /**
     * Api
     */
    public void connect(SerialSocket socket) throws IOException {
        socket.connect(this);
        this.socket = socket;
        connected = true;
    }

    public void disconnect() {
        connected = false; // ignore data,errors while disconnecting
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    public void write(byte[] data) throws IOException {
        if (!connected)
            throw new IOException("not connected");
        socket.write(data);
    }

    public void attach(SerialListener listener) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new IllegalArgumentException("not in main thread");
        // use synchronized() to prevent new items in queue2
        // new items will not be added to queue1 because mainLooper.post and attach() run in main thread
        synchronized (this) {
            this.listener = listener;
        }
        for (QueueItem item : queue1) {
            switch (item.type) {
                case Connect:
                    listener.onSerialConnect();
                    break;
                case ConnectError:
                    listener.onSerialConnectError(item.e);
                    break;
                case Read:
                    listener.onSerialRead(item.data);
                    break;
                case IoError:
                    listener.onSerialIoError(item.e);
                    break;
            }
        }
        for (QueueItem item : queue2) {
            switch (item.type) {
                case Connect:
                    listener.onSerialConnect();
                    break;
                case ConnectError:
                    listener.onSerialConnectError(item.e);
                    break;
                case Read:
                    listener.onSerialRead(item.data);
                    break;
                case IoError:
                    listener.onSerialIoError(item.e);
                    break;
            }
        }
        queue1.clear();
        queue2.clear();
    }

    public void detach() {
        listener = null;
    }

    /**
     * SerialListener
     */
    public void onSerialConnect() {
        Log.d(TAG, "BTSupport : onSerialConnect2");
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            btListener.callOnConnect();
                        } else {
                            queue1.add(new QueueItem(QueueType.Connect, null, null));
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.Connect, null, null));
                }
            }
        }
    }

    public void onSerialConnectError(Exception e) {
        Log.i(TAG,"BTSupport: connection error: " +e.getMessage());
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            Log.i(TAG,"BTSupport: connection error: " +e.getMessage());
                            listener.onSerialConnectError(e);
                            btListener.onConnectFailure("");
                        } else {
                            queue1.add(new QueueItem(QueueType.ConnectError, null, e));
                            disconnect();
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.ConnectError, null, e));
                    disconnect();
                }
            }
        }
    }

    public void onSerialRead(byte[] data) {
        //Log.d(TAG, "BTSupport: on ptt btn press");
        Log.i(TAG,"BTSupport: onSerialRead: ");
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            listener.onSerialRead(data);
                            Log.w(TAG,"BTSupport: message: "+new String(data));
                            String msg = new String(data);
                            if (BTHelper.checkIfPress(msg)) {
                                //Button PRESS event
                                handler.postDelayed(longPressedRunnable, LOCK_PRESS_INTERVAL);
                            }
                            else
                                if (BTHelper.checkIfRelease(msg)) {
                                //Button RELEASE event
                                    handler.removeCallbacks(longPressedRunnable);
                                    if (!isLongPressed) {
                                        if (!isBusy) {
                                            // Prevent multiple click in this short time
                                            isBusy = true;
                                            clicks++;
                                            handler.postDelayed(singleAndDoubleRunnable, DOUBLE_PRESS_INTERVAL);
                                            isBusy = false;
                                        }
                                    }
                                    handler.postDelayed(buttonUpRunnable,BUTTON_UP_INTERVAL);
                                    isLongPressed = false;
                                }else
                                    if(BTHelper.checkIfEmergencyButtonPress(msg)){
                                        //Button PRESS event
                                        handler.postDelayed(emergencyButtonLongPressedRunnable, LOCK_PRESS_INTERVAL);
                                    }
                                    else
                                        if (BTHelper.checkIfEmergencyButtonRelease(msg)) {
                                        //Button RELEASE event
                                        handler.removeCallbacks(emergencyButtonLongPressedRunnable);
                                        if (!isLongEmergencyButtonPressed) {
                                            if (!isEmergencyButtonBusy) {
                                                // Prevent multiple click in this short time
                                                isEmergencyButtonBusy = true;
                                                emergencyButtonClicks++;
                                                handler.postDelayed(emergencySingleAndDoubleButtonRunnable, DOUBLE_PRESS_INTERVAL);
                                                isEmergencyButtonBusy = false;
                                            }
                                        }
                                        handler.postDelayed(buttonUpRunnable,BUTTON_UP_INTERVAL);
                                        isLongEmergencyButtonPressed = false;
                                    }

                        } else {
                            queue1.add(new QueueItem(QueueType.Read, data, null));
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.Read, data, null));
                }
            }
        }
    }

    public void onSerialIoError(Exception e) {
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            listener.onSerialIoError(e);
                            btListener.onConnectFailure(e.getMessage());
                        } else {
                            queue1.add(new QueueItem(QueueType.IoError, null, e));
                            disconnect();
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.IoError, null, e));
                    disconnect();
                }
            }
        }
    }

}
