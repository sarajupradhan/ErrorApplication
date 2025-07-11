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
        int storesSize = getStores() != null ? getStores().size() : 0;
        int assignedStoresSize = getAssignedStores() != null ? getAssignedStores().size() : 0;

        if (assignedStoresSize == 0) {
            android.util.Log.e("MainActivity", "Division by zero: assignedStoresSize is 0. storesSize=" + storesSize);
            // Handle the error appropriately, e.g., show a message or return early
            return;
        }

        int result = storesSize / assignedStoresSize;
        // Optionally log or use the result
    }

    private List<String> getStores(){
        return List.of("Store1", "Store2");
    }

    private List<String> getAssignedStores(){
        return List.of();
    }

}