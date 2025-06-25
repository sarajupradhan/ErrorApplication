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
import java.util.Locale;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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
        buttonNullPointer.setText(R.string.null_pointer_exception);
        buttonNullPointer.setOnClickListener(v -> simulateNullPointerException());

        Button buttonArrayIndex = findViewById(getResources().getIdentifier("button2", "id", getPackageName()));
        buttonArrayIndex.setText(R.string.array_index_out_of_bounds_exception);
        buttonArrayIndex.setOnClickListener(v -> simulateArrayIndexOutOfBoundsException());

        Button buttonClassCast = findViewById(getResources().getIdentifier("button3", "id", getPackageName()));
        buttonClassCast.setText(R.string.class_cast_exception);
        buttonClassCast.setOnClickListener(v -> simulateClassCastException());

        Button buttonArithmetic = findViewById(getResources().getIdentifier("button4", "id", getPackageName()));
        buttonArithmetic.setText(R.string.arithmetic_exception);
        buttonArithmetic.setOnClickListener(v -> simulateArithmeticException());

        Button buttonIllegalArgument = findViewById(getResources().getIdentifier("button5", "id", getPackageName()));
        buttonIllegalArgument.setText(R.string.illegal_argument_exception);
        buttonIllegalArgument.setOnClickListener(v -> simulateIllegalArgumentException());

        Button buttonFileNotFound = findViewById(getResources().getIdentifier("button6", "id", getPackageName()));
        buttonFileNotFound.setText(R.string.file_not_found_exception);
        buttonFileNotFound.setOnClickListener(v -> simulateFileNotFoundException());

        Button buttonNumberFormat = findViewById(getResources().getIdentifier("button7", "id", getPackageName()));
        buttonNumberFormat.setText(R.string.number_format_exception);
        buttonNumberFormat.setOnClickListener(v -> simulateNumberFormatException());

        Button buttonIndexOutOfBounds = findViewById(getResources().getIdentifier("button8", "id", getPackageName()));
        buttonIndexOutOfBounds.setText(R.string.index_out_of_bounds_exception);
        buttonIndexOutOfBounds.setOnClickListener(v -> simulateIndexOutOfBoundsException());
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        return sdf.format(now);
    }
    private void simulateNullPointerException() {
        String nullStr = null;
        if (nullStr == null) {
            Log.e(TAG, getString(R.string.null_pointer_exception) + ": nullStr is null");
            writeErrorToFile(getString(R.string.null_pointer_exception) + ": nullStr is null", null);
            Toast.makeText(this, getString(R.string.null_pointer_exception) + ": nullStr is null", Toast.LENGTH_SHORT).show();
            return;
        }
        // Safe to call length() now
        int length = nullStr.length();
        Log.d(TAG, "String length: " + length);
    }


    private void simulateArrayIndexOutOfBoundsException() {
        try {
            int[] array = new int[5];
            int number = array[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, getString(R.string.array_index_out_of_bounds_exception), e);
            writeErrorToFile(getString(R.string.array_index_out_of_bounds_exception), e);
        }
    }

    private void simulateClassCastException() {
        try {
            Object i = Integer.valueOf(42);
            String s = (String) i;
        } catch (ClassCastException e) {
            Log.e(TAG, getString(R.string.class_cast_exception), e);
            writeErrorToFile(getString(R.string.class_cast_exception), e);
        }
    }

    private void simulateArithmeticException() {
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            Log.e(TAG, getString(R.string.arithmetic_exception), e);
            writeErrorToFile(getString(R.string.arithmetic_exception), e);
        }
    }

    private void simulateIllegalArgumentException() {
        try {
            int size = -1;
            int[] invalidArray = new int[size];

            if (size < 0) {
                throw new IllegalArgumentException("Array size must be non-negative");
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, getString(R.string.illegal_argument_exception), e);
            writeErrorToFile(getString(R.string.illegal_argument_exception), e);
        } catch (NegativeArraySizeException e) {
            Log.e(TAG, getString(R.string.illegal_argument_exception), e);
            writeErrorToFile(getString(R.string.illegal_argument_exception), e);
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
        try {
            int num = Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            Log.e(TAG, getString(R.string.number_format_exception), e);
            writeErrorToFile(getString(R.string.number_format_exception), e);
        }
    }

    private void simulateIndexOutOfBoundsException() {
        try {
            String str = "Hello";
            char ch = str.charAt(10);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, getString(R.string.index_out_of_bounds_exception), e);
            writeErrorToFile(getString(R.string.index_out_of_bounds_exception), e);
        }
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
}

