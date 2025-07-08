package com.example.errorapplication.error.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BTHelper {
    public static final String TAG = "BTHelper";
    public static Set<String> defaultBPHeadsetKeysArr = new HashSet<>();
    public static Set<String> pressKeys = new HashSet<>();
    public static Set<String> pressEmergencyKeys = new HashSet<>();
    public static Set<String> releaseKeys = new HashSet<>();
    public static Set<String> releaseEmergencyKeys = new HashSet<>();
    private static Set<String> newBPHeadsetKeysArr = new HashSet<>();

    public static boolean hasBlueParrotDeviceConnection(Context context, BluetoothDevice device) {
        boolean bBTHSAvailable = isBlueToothHeadSetAvailableUsing(context, false);

        Log.d(TAG, "BTSupport : ++ hasBlueParrotDeviceConnection bBTHSAvailable:" + bBTHSAvailable + " bluetoothAdapter:" + device.getName());
        boolean hasBlueParrotDevice = isBlueparrotHeadset(device.getName());

        Log.d(TAG, "BTSupport : -- hasBlueParrotDeviceConnection hasBlueParrotDevice:" + hasBlueParrotDevice);
        return hasBlueParrotDevice;
    }

    public static boolean isBlueparrotHeadset(String connectedHeadsetName) {
        for (String headsetName : defaultBPHeadsetKeysArr) {
            Log.d(TAG, "BTSupport : bthdevice name:" + headsetName + " connectedHeadsetName:" + connectedHeadsetName);
            if (connectedHeadsetName.contains(headsetName)) {
                Log.d(TAG, "BTSupport : found connectedHeadsetName:" + connectedHeadsetName);
                return true;
            }
        }

        return false;
    }

    public static boolean isFoundInNewBlueparrotHeadsetList(String connectedHeadsetName) {
        for (String headsetName : newBPHeadsetKeysArr) {
            Log.d(TAG, "BTSupport : New List bthdevice name:" + headsetName + " connectedHeadsetName:" + connectedHeadsetName);
            if (connectedHeadsetName.contains(headsetName)) {
                Log.d(TAG, "BTSupport : New List found connectedHeadsetName:" + connectedHeadsetName);
                return true;
            }
        }

        return false;
    }

    public static boolean isBlueToothHeadSetAvailableUsing(final Context context, boolean bCheckBthAudio) {
        if (context == null) return false;

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean bluetoothEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
            boolean bBluetoothAudioFlag = !bCheckBthAudio || getBluetoothAllowAudio(context);
            Log.d(TAG, "BTH: bluetoothEnabled:" + bluetoothEnabled +
                    " bBluetoothAudioFlag:" + bBluetoothAudioFlag + " bCheckBthAudio:" + bCheckBthAudio);
            return bluetoothEnabled && bBluetoothAudioFlag;

        } catch (Exception exp) {
            Log.e(TAG, "BTH: exception occured!!!!" + exp.getMessage());
        }
        return false;
    }

    private static boolean getBluetoothAllowAudio(final Context context) {
        boolean bBluetoothAudio = false;

        if (context == null) return bBluetoothAudio;
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs != null) {
            bBluetoothAudio = prefs.getBoolean("allowBluetooth", false);
        }
        //Debugger.i(TAG,"getBluetoothAllowAudio bBluetoothAudio:"+bBluetoothAudio);
        return bBluetoothAudio;
    }

    public static void initKeys(Set<String> pressEmergencyButtonKeys, Set<String> releaseEmergencyButtonKeys,
                                Set<String> tempPressKeys, Set<String> tempReleaseKeys,
                                Set<String> blueParrotHeadsetKeysArrayFromApi) {
        initDefaultKeys();
        pressKeys.addAll(tempPressKeys);
        releaseKeys.addAll(tempReleaseKeys);
        pressEmergencyKeys.addAll(pressEmergencyButtonKeys);
        releaseEmergencyKeys.addAll(releaseEmergencyButtonKeys);
        if (blueParrotHeadsetKeysArrayFromApi != null) {
            defaultBPHeadsetKeysArr.addAll(blueParrotHeadsetKeysArrayFromApi);
            newBPHeadsetKeysArr = blueParrotHeadsetKeysArrayFromApi;
        }
    }

    private static void updateDefaultBTList() {
        defaultBPHeadsetKeysArr.clear();
        java.util.List<String> mBPSupportedHeadsets = Arrays.asList
                ("Perform 45", "M300", "B550", "B450", "C300", "B350", "C400", "Reveal", "S450", "VXi Parrott", "APTT300", "PB", "B650", "S650");
        defaultBPHeadsetKeysArr.addAll(mBPSupportedHeadsets);
    }

    private static boolean checkIfExist(String msg, Set<String> keys) {
        for (String item : keys) {
            if (msg.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfPress(String msg) {
        return checkIfExist(msg, pressKeys);
    }

    public static boolean checkIfRelease(String msg) {
        return checkIfExist(msg, releaseKeys);
    }

    public static boolean checkIfEmergencyButtonPress(String msg) {
        return checkIfExist(msg, pressEmergencyKeys);
    }

    public static boolean checkIfEmergencyButtonRelease(String msg) {
        return checkIfExist(msg, releaseEmergencyKeys);
    }

    public static void initDefaultKeys() {
        pressKeys.clear();
        releaseKeys.clear();

        pressEmergencyKeys.clear();
        releaseEmergencyKeys.clear();

        pressEmergencyKeys.add("PTTE=P");
        releaseEmergencyKeys.add("PTTE=R");

        pressKeys.add("PTT=P");

        releaseKeys.add("PTT=R");

        updateDefaultBTList();
    }

}
