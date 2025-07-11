package com.example.errorapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import android.util.Log;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        Button buttonNullPointer = findViewById(getResources().getIdentifier("button7", "id", getPackageName()));
        buttonNullPointer.setText(R.string.null_pointer_exception);
        buttonNullPointer.setOnClickListener(v -> simulateNullPointerException());

    }
    private void simulateNullPointerException() {
        List<String> applicationStates = getApplicationScreen();
        String domain = applicationStates.get(0);
    }

    private List<String> getApplicationScreen(){
        return null;
    }
}