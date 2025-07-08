package com.example.errorapplication.error.bluetooth;


import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.errorapplication.error.bluetooth.spp.SPPHeadSetMgr;

import java.util.HashMap;
import java.util.Map;

public abstract class BTHeadSetMgr {
    public static final String TAG = BTHeadSetMgr.class.getSimpleName();
    private static BTHeadSetMgr instance = null;

    public static BTHeadSetMgr getInstance(Context context, BluetoothDevice device) {
        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            Log.i(TAG, "BTSupport : no location permission instance not initializing");
            Toast.makeText(context,"Please provide Location Permission",Toast.LENGTH_SHORT);
            return null;
        }
        Log.i(TAG, "BTSupport : all necessary permission given");
        if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
            instance = SPPHeadSetMgr.getInstance(context, device);
        }
        return instance;
    }




    public static void clearInstance() {
        if (instance == null) {
            return;
        }
        instance = null;
    }

    public static void checkAndReInit(Context context,BluetoothDevice currentActiveDevice) {
        if(currentActiveDevice == null)return;
        Log.d(TAG,"BTSupport : checkAndReInit");
        if(instance != null){
            Log.d(TAG,"BTSupport : instance: "+instance);
            clearInstance();
            Log.d(TAG,"BTSupport : getting instance of : "+currentActiveDevice.getName());
            getInstance(context,currentActiveDevice);
        }else {
            Log.d(TAG,"BTSupport : initial instance was null  getting instance of : "+currentActiveDevice.getName());
            getInstance(context,currentActiveDevice);
        }
    }

    public static boolean isInitialised() {
        return instance != null;
    }


}
