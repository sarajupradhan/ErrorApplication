package com.example.errorapplication.ui;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.errorapplication.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        Button buttonArrayIndex = findViewById(getResources().getIdentifier("button3", "id", getPackageName()));
        buttonArrayIndex.setText(R.string.arithmetic_exception);
        buttonArrayIndex.setOnClickListener(v -> simulateArithmeticException());


    }

    private void simulateArithmeticException() {
        int result = getStores().size() / getAssignedStores().size();
    }

    private List<String> getStores(){
        return List.of("Store1", "Store2");
    }

    private List<String> getAssignedStores(){
        return List.of();
    }

}