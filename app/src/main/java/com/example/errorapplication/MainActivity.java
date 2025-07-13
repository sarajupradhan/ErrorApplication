package com.example.errorapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errorapplication.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
android.util.Log

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private OnFragmentInteractionListener mListener;
    private String currentState = "default";

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeButtons();
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
                    Intent intent  = new Intent(MainActivity.this, com.example.errorapplication.ui.MainActivity.class);
                    startActivity(intent);
                });;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return sdf.format(now);
    }

    private void simulateNullPointerException() {
        List<String> applicationStates = getApplicationScreen();
        String domain = applicationStates.get(0);
    }
    private void simulateArrayIndexOutOfBoundsException() {
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
        try {
            // Validate if currentDate is a valid integer string before parsing
            int num = Integer.parseInt(currentDate);
        } catch (NumberFormatException e) {
            Log.e("MainActivity", "Failed to parse date string as integer: " + currentDate, e);
            Toast.makeText(this, "Invalid number format: " + currentDate, Toast.LENGTH_SHORT).show();
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
        return "com.example.errorapplication";
    }

    private String getContactInfo(){
        return "com.example.errorapplication@";
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

