package com.example.errorapplication;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private OnFragmentInteractionListener mListener;
    private String currentState = "default";

    // Define the interface
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeButtons();

        EditText editText = findViewById(R.id.editText);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String enteredText = editText.getText().toString();
            if (!enteredText.isEmpty()) {
                writeErrorToFile(enteredText, null);
            } else {
                Toast.makeText(this, "Please enter text to log.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeButtons() {
        Button buttonNullPointer = findViewById(getResources().getIdentifier("button1", "id", getPackageName()));
        if (buttonNullPointer != null) {
            buttonNullPointer.setText(R.string.null_pointer_exception);
            buttonNullPointer.setOnClickListener(v -> {
                try {
                    simulateNullPointerException();
                } catch (NullPointerException e) {
                    Log.e("MainActivity", "NullPointerException in simulateNullPointerException", e);
                    Toast.makeText(this, "NullPointerException occurred. Please check your input.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("MainActivity", "button1 not found in layout");
        }

        Button buttonArrayIndex = findViewById(getResources().getIdentifier("button2", "id", getPackageName()));
        if (buttonArrayIndex != null) {
            buttonArrayIndex.setText(R.string.array_index_out_of_bounds_exception);
            buttonArrayIndex.setOnClickListener(v -> simulateArrayIndexOutOfBoundsException());
        } else {
            Log.e("MainActivity", "button2 not found in layout");
        }

        Button buttonClassCast = findViewById(getResources().getIdentifier("button3", "id", getPackageName()));
        if (buttonClassCast != null) {
            buttonClassCast.setText(R.string.class_cast_exception);
            buttonClassCast.setOnClickListener(v -> simulateClassCastException());
        } else {
            Log.e("MainActivity", "button3 not found in layout");
        }

        Button buttonArithmetic = findViewById(getResources().getIdentifier("button4", "id", getPackageName()));
        if (buttonArithmetic != null) {
            buttonArithmetic.setText(R.string.arithmetic_exception);
            buttonArithmetic.setOnClickListener(v -> simulateArithmeticException());
        } else {
            Log.e("MainActivity", "button4 not found in layout");
        }

        Button buttonIllegalArgument = findViewById(getResources().getIdentifier("button5", "id", getPackageName()));
        if (buttonIllegalArgument != null) {
            buttonIllegalArgument.setText(R.string.illegal_argument_exception);
            buttonIllegalArgument.setOnClickListener(v -> simulateIllegalArgumentException());
        } else {
            Log.e("MainActivity", "button5 not found in layout");
        }

//        Button buttonFileNotFound = findViewById(getResources().getIdentifier("button6", "id", getPackageName()));
//        buttonFileNotFound.setText(R.string.file_not_found_exception);
//        buttonFileNotFound.setOnClickListener(v -> simulateFileNotFoundException());

        Button buttonNumberFormat = findViewById(getResources().getIdentifier("button7", "id", getPackageName()));
        if (buttonNumberFormat != null) {
            buttonNumberFormat.setText(R.string.number_format_exception);
            buttonNumberFormat.setOnClickListener(v -> simulateNumberFormatException());
        } else {
            Log.e("MainActivity", "button7 not found in layout");
        }

        Button buttonIndexOutOfBounds = findViewById(getResources().getIdentifier("button8", "id", getPackageName()));
        if (buttonIndexOutOfBounds != null) {
            buttonIndexOutOfBounds.setText(R.string.index_out_of_bounds_exception);
            buttonIndexOutOfBounds.setOnClickListener(v -> simulateIndexOutOfBoundsException());
        } else {
            Log.e("MainActivity", "button8 not found in layout");
        }
    }


    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return sdf.format(now);
    }

    private void simulateNullPointerException() {
        String applicationState = getApplicationScreen();
        String[] state = applicationState.split("\\.");
        int domain = state[5].length();
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
            String currentDate =  getCurrentDate();
            int num = Integer.parseInt(currentDate);
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
        return -1;
    }

    private String getApplicationScreen(){
        return "dashboard,contacts,history,settings";
    }

    private void updateApplicationState(final String currentState){
        currentState.equals("default");
    }

}

