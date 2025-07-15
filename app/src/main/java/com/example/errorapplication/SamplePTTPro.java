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

                int logLevel = 0;
                try {
                    logLevel = Integer.parseInt(isDebugMode.trim());
                } catch (NumberFormatException nfe) {
                    Log.e("ErrorApplication", "Invalid log_level value: " + isDebugMode, nfe);
                    Toast.makeText(this, "Invalid log_level in config: " + isDebugMode, Toast.LENGTH_SHORT).show();
                    // Optionally, set a default or handle as needed
                }

                Log.d("ErrorApplication","isDebugMode "+logLevel);

            } catch (FileNotFoundException e) {
                Log.e("ErrorApplication", "Config file not found", e);
                Toast.makeText(this, "Config file not found", Toast.LENGTH_SHORT).show();
                return;
            } catch (IOException e) {
                Log.e("ErrorApplication", "IO error reading config", e);
                Toast.makeText(this, "IO error reading config", Toast.LENGTH_SHORT).show();
                return;
            } catch (JSONException e) {
                Log.e("ErrorApplication", "JSON parse error", e);
                Toast.makeText(this, "JSON parse error in config", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.setClassName("com.symbol.wfc.pttpro", "com.symbol.wfc.pttpro.ActivityRoot");
            startActivity(intent);
        }
    }

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