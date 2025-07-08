package com.example.errorapplication.error.bluetooth.spp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.errorapplication.error.bluetooth.BTHeadSetMgr;


public class SPPHeadSetMgr extends BTHeadSetMgr implements SerialListener {
    public static final String TAG = SPPHeadSetMgr.class.getSimpleName();
    private static SPPHeadSetMgr instance = null;
    private static Context mContext;
    private BluetoothAdapter bluetoothAdapter;
    private String deviceAddress;
    private SerialService service;
    private Connected connected = Connected.False;

    public boolean initialized() {
        return service != null;
    }

    public void onLongPress() {

    }

    public void onDoubleTap() {

    }

    public void onTap() {

    }

    public void onEmergencyButtonLongPress() {

    }

    public void onEmergencyButtonDoubleTap() {

    }

    public void onEmergencyButtonTap() {

    }

    public void onButtonUp() {

    }

    public void callOnConnect() {

    }

    public void onConnectFailure(String s) {

    }

    private enum Connected {False, Pending, True}

    public static SPPHeadSetMgr getInstance(Context context,BluetoothDevice device) {
        mContext = context;
        instance = null;
        if (instance == null) {
            instance = new SPPHeadSetMgr(device);
        }
        return instance;
    }

    protected void clear() {
        deInit();
    }

    private SPPHeadSetMgr(BluetoothDevice device) {
        init(device);
    }

    @Override
    public void onSerialConnect() {
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        deInit();
    }

    @Override
    public void onSerialRead(byte[] data) {
    }

    @Override
    public void onSerialIoError(Exception e) {
        deInit();
    }

    public void init(BluetoothDevice device) {
        initBT(device); //find bounded BT devices
        initSerialService(); //init serial service & connect
    }

    private void initBT(BluetoothDevice device) {
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Log.d(TAG, "BTSupport : FEATURE_BLUETOOTH supported");
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            Log.d(TAG, "BTSupport : FEATURE_BLUETOOTH not supported");
        }

        if (bluetoothAdapter == null)
            Log.d(TAG, "BTSupport : bluetooth not supported");
        else if (!bluetoothAdapter.isEnabled())
            Log.d(TAG, "BTSupport : bluetooth is disabled");
        else
            Log.d(TAG, "BTSupport : no bluetooth devices found");

        if (bluetoothAdapter != null) {
            deviceAddress = device.getAddress();
            Log.d(TAG, "BTSupport : deviceAddress : " + deviceAddress +" device name: "+device.getName());
        } else {
            Log.d(TAG, "BTSupport : bluetoothAdapter is null");
        }

    }

    private void initSerialService() {
        service = new SerialService(this);
        service.attach(this);
        connect();
    }

    private void connect() {
        try {
            if (service != null) {
                Log.d(TAG, "BTSupport : service is not null");
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
                Log.d(TAG, "BTSupport : connecting...");
                connected = Connected.Pending;
                SerialSocket socket = new SerialSocket(mContext.getApplicationContext(), device);
                service.connect(socket);
            } else {
                Log.d(TAG, "BTSupport : service is null");
            }
        } catch (Exception e) {
            onSerialConnectError(e);
            Log.e(TAG, "BTSupport : connect - "+e.getMessage());
        }
    }

    public void deInit() {
        connected = Connected.False;
        if (service != null) {
            service.disconnect();
        }
        service = null;
    }
}
