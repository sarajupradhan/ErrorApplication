package com.example.errorapplication;

import static android.view.KeyEvent.ACTION_UP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SamplePTTPro extends AppCompatActivity {

    private BroadcastReceiver provisioningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ErrorApplication","onReceive ");
            KeyEvent key =(KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
            if(key.getAction()!=ACTION_UP){
                return;
            }
            onReceiveL2();
        }
    };

    private void onReceiveL2() {

        File configFile = new File("/data/local/tmp/WFCPTTProDefault.json");
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
                String isDebugMode = jsonObject.getString("log_level");
                Log.d("ErrorApplication","isDebugMode "+Integer.parseInt(isDebugMode));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent();
            intent.setClassName("com.symbol.wfc.pttpro", "com.symbol.wfc.pttpro.ActivityRoot");
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sample_pttpro);
        View login_esn_button = findViewById(R.id.login_button);
        login_esn_button.setEnabled(false);
        registerForScan();

        findViewById(R.id.button_menu_3dots).setOnLongClickListener(view -> {
            startActivity(new Intent(SamplePTTPro.this, MainActivity.class));
            return true;
        });
    }

    private void registerForScan() {
        IntentFilter provFilter = new IntentFilter();
//        provFilter.addAction("com.zebra.wfc.ACTION_SCAN");
        provFilter.addAction("com.symbol.button.R1");
        provFilter.addAction("com.symbol.button.L1");
        provFilter.addCategory("android.intent.category.DEFAULT");
        registerReceiver(provisioningReceiver, provFilter,RECEIVER_EXPORTED);
    }
}
// Timestamp: 2025-07-15T16:14:46.564024
