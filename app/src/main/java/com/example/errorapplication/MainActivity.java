
// Example fixed code
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    TextView textView = findViewById(R.id.textView);
    if (textView != null) {
        textView.setText("Hello, World!");
    } else {
        Log.e("MainActivity", "TextView is null");
    }
}
