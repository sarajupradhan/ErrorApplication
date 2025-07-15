package com.zebra.pttproservice;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

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