package com.zebra.pttproservice;

import android.Manifest;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zebra.pttproservice.R;
import com.zebra.pttproservice.error.bluetooth.BTHeadSetMgr;
import com.test.error.ErrorLibTest;
import com.test.error.MemoryEater;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i(TAG, "BTSupport handler.removeMessages");
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    int connectionState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_CONNECTION_STATE);
                    Log.i(TAG, "BTSupport Bt ACTION_CONNECTION_STATE_CHANGED "+connectionState);
                    if (connectionState == BluetoothAdapter.STATE_CONNECTED) {
                        Log.i(TAG, "BTSupport Bt BluetoothAdapter.STATE_CONNECTED bluetoothConnected ");
                        BTHeadSetMgr.getInstance(MainActivity.this,null);
                    }
                    break;
            }
        }
    };
    private static final String TAG = "MainActivity";
    Switch debugSwitch;

    private OnFragmentInteractionListener mListener;
    private String currentState = "default";

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        updateIntent(intent);
//        ImageView imageView = findViewById(R.id.imageView);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_large_image);
//        imageView.setImageBitmap(bitmap);
        initializeButtons();
        checkAndStartService(true);
    }
    @Override
    public void onNewIntent(Intent intent){
        updateIntent(intent);
        super.onNewIntent(intent);
    }
//    adb push WFCPTTProDefault.json /data/local/tmp/
//adb shell am start -n com.zebra.pttproservice/.MainActivity -a com.zebra.pttproservice.ACTION_DEFAULT_CONFIG --es configpath "/data/local/tmp/WFCPTTProDefault.json"
    private void updateIntent(Intent intent) {
        if (intent != null) {
            String intentAction = intent.getAction();
            if (!TextUtils.isEmpty(intentAction)
                    && intentAction.equalsIgnoreCase("com.zebra.pttproservice.ACTION_DEFAULT_CONFIG")) {
                String path = intent.getStringExtra("configpath");
                File configFile = new File(path);
                if(configFile.exists()){
                    try (final FileInputStream fis = new FileInputStream(configFile);
                         final InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                         final BufferedReader bufferedReader = new BufferedReader(isr)
                    ) {

                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        String jsonStr = sb.toString();
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        String isDebugMode = jsonObject.getString("is_debug_mode");
                        debugSwitch.setChecked(Boolean.parseBoolean(isDebugMode));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void checkAndStartService(boolean isForceCheckBluetooth) {
        if(isNecessaryPermissionGiven() ){
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            iFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            iFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(mReceiver, iFilter,RECEIVER_EXPORTED);
            }
            Log.d(TAG,"BTSupport : registerBtEvent");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123){
            checkAndStartService(true);
        }
    }

    public boolean isNecessaryPermissionGiven() {

        boolean isA12PermissionGiven = true;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED){
                isA12PermissionGiven = false;
            }
        }

        return   isA12PermissionGiven;
    }



    private void initializeButtons() {

        Button buttonNumberFormat = findViewById(getResources().getIdentifier("button1", "id", getPackageName()));
        buttonNumberFormat.setText(R.string.number_format_exception);
        buttonNumberFormat.setOnClickListener(v -> simulateNumberFormatException());

        Button buttonClassCast = findViewById(getResources().getIdentifier("button2", "id", getPackageName()));
        buttonClassCast.setText(R.string.class_cast_exception);
        buttonClassCast.setOnClickListener(v -> simulateClassCastException());

        Button buttonArrayIndex = findViewById(getResources().getIdentifier("button3", "id", getPackageName()));
        buttonArrayIndex.setText(R.string.array_index_out_of_bounds_exception);
        buttonArrayIndex.setOnClickListener(v -> simulateArrayIndexOutOfBoundsException());

        Button buttonArithmetic = findViewById(getResources().getIdentifier("button4", "id", getPackageName()));
        buttonArithmetic.setText(R.string.arithmetic_exception);
        buttonArithmetic.setOnClickListener(v -> simulateArithmeticException());

        Button buttonIllegalArgument = findViewById(getResources().getIdentifier("button5", "id", getPackageName()));
        buttonIllegalArgument.setText(R.string.illegal_argument_exception);
        buttonIllegalArgument.setOnClickListener(v -> simulateIllegalArgumentException());

//        Button buttonFileNotFound = findViewById(getResources().getIdentifier("button6", "id", getPackageName()));
//        buttonFileNotFound.setText(R.string.file_not_found_exception);
//        buttonFileNotFound.setOnClickListener(v -> simulateFileNotFoundException());

        Button buttonNullPointer = findViewById(getResources().getIdentifier("button7", "id", getPackageName()));
        buttonNullPointer.setText(R.string.null_pointer_exception);
        buttonNullPointer.setOnClickListener(v -> simulateNullPointerException());

        Button buttonIndexOutOfBounds = findViewById(getResources().getIdentifier("button8", "id", getPackageName()));
        buttonIndexOutOfBounds.setText(R.string.index_out_of_bounds_exception);
        buttonIndexOutOfBounds.setOnClickListener(v -> simulateIndexOutOfBoundsException());


        findViewById(getResources().getIdentifier("button9", "id", getPackageName()))
                .setOnClickListener(v -> {
                    Intent intent  = new Intent(MainActivity.this, MainActivity2.class);
                  startActivity(intent);
                });

        findViewById(getResources().getIdentifier("button10", "id", getPackageName()))
                .setOnClickListener(v -> {
                    Intent intent  = new Intent(MainActivity.this, com.zebra.pttproservice.ui.MainActivity.class);
                    startActivity(intent);
                });

        findViewById(getResources().getIdentifier("button11", "id", getPackageName()))
                .setOnClickListener(v -> {
                    Intent intent  = new Intent(MainActivity.this, com.zebra.pttproservice.task.TaskTrackerActivity.class);
                    startActivity(intent);
                });

        Button buttonOOM = findViewById(R.id.button12);
        buttonOOM.setText(R.string.oom_exception);
        buttonOOM.setOnClickListener(v -> simulateOOMException());

    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return sdf.format(now);
    }

    private void simulateNullPointerException() {
//        ErrorApplication.postStackTraceToApi("Test");
        ErrorLibTest.nullPointer();
    }
    private void simulateOOMException() {
//        ErrorApplication.postStackTraceToApi("Test");
        MemoryEater.simulateOOM();
    }
    private void simulateArrayIndexOutOfBoundsException() {
        int[] array = new int[5];
        int number = array[10];
            String serverUrl = getServerUrl();
            String[] serverUrlAr = serverUrl.split("\\.");
            String domain = serverUrlAr[10];
    }

    private void simulateClassCastException() {
        mListener = (OnFragmentInteractionListener) getApplicationContext();
    }

    private void simulateArithmeticException() {
            int result = getStores().size() / getAssignedStores().size();
    }

    private void simulateIllegalArgumentException() {
            int newSupportedHeadset = getSupportedHeadset().size() - getTotalDeprecatedHeadset() ;
            int[] invalidArray = new int[newSupportedHeadset];
            if (newSupportedHeadset < 0) {
                throw new IllegalArgumentException("Array size must be non-negative");
            }
    }

    private void simulateFileNotFoundException() {
        try {
            FileInputStream fis = new FileInputStream("non_existent_file.txt");
        } catch (FileNotFoundException e) {
            Log.e(TAG, getString(R.string.file_not_found_exception), e);
            writeErrorToFile(getString(R.string.file_not_found_exception), e);
        }
    }

    private void simulateNumberFormatException() {
        String currentDate = getCurrentDate();
        if (currentDate == null) {
            Log.e("MainActivity", "Current date string is null");
            return;
        }
        try {
            int num = Integer.parseInt(currentDate);
        } catch (NumberFormatException e) {
            Log.e("MainActivity", "Failed to parse integer from currentDate: " + currentDate, e);
        }
    }

    private void simulateIndexOutOfBoundsException() {
            String contactInfo = getContactInfo();
            char userId = contactInfo.charAt(30);
    }

    private void writeErrorToFile(String errorType, Exception e) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, "error_log.txt");
            if (e != null) {
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.append(getString(R.string.timestamp)).append(getCurrentTimestamp()).append("\n");
                    writer.append(getString(R.string.error_occurred)).append(errorType).append("\n");
                    writer.append(getString(R.string.exception_message)).append(e.getMessage()).append("\n");
                    writer.append(getString(R.string.stack_trace)).append(Log.getStackTraceString(e)).append("\n\n");
                    Toast.makeText(this, getString(R.string.error_logged) + errorType, Toast.LENGTH_SHORT).show();
                } catch (IOException ioException) {
                    Log.e(TAG, getString(R.string.failed_to_write), ioException);
                }
            } else {
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.append(getString(R.string.timestamp)).append(getCurrentTimestamp()).append("\n");
                    writer.append(getString(R.string.error_occurred)).append(errorType).append("\n\n\n");
                    Toast.makeText(this, getString(R.string.error_logged) + errorType, Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Log.e(TAG, getString(R.string.failed_to_write), ex);
                }
            }
        }
        else {
            Log.e(TAG, getString(R.string.directory_not_available));
            Toast.makeText(this, getString(R.string.failed_to_access_storage), Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getStores(){
        return List.of("Store1", "Store2");
    }

    private List<String> getAssignedStores(){
        return List.of();
    }

    private String getServerUrl(){
        return "com.zebra.pttproservice";
    }

    private String getContactInfo(){
        return "com.zebra.pttproservice@";
    }

    private String getCurrentDate(){
        return new Date().toString();
    }

    private List<String> getSupportedHeadset(){
        return List.of();
    }

    private int getTotalDeprecatedHeadset(){
        return 1;
    }

    private List<String> getApplicationScreen(){
        return null;
    }

    private void updateApplicationState(final String currentState){
        currentState.equals("default");
    }

}

