package com.example.jungwonseo.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CUSTOMER = 102;
    public static final int REQUEST_CODE_EXPENSE = 103;
    public static final int REQUEST_CODE_PRODUCT = 104;

    //cellphone back button setting.
    InputMethodManager imm;
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //back button
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //get data from MyService.java
        Intent passedIntent =getIntent();
        processIntent(passedIntent);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "Click BACK again to exit", Toast.LENGTH_LONG).show();
        }
    }


    //get data from MyService.java
    @Override
    protected void onNewIntent(Intent intent){
        processIntent(intent);
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        TextView text = (TextView) findViewById(R.id.loginData);
        if (intent != null) {

            String data = intent.getStringExtra("FinalData");

            text.setText(data);

        }
    }

    public void customerClick(View v){
        Intent intent = new Intent(getApplicationContext(), Customer.class);
        startActivityForResult(intent, REQUEST_CODE_CUSTOMER);
    }

    public void expenseClick(View v){
        Intent intent = new Intent(getApplicationContext(), Expense.class);
        startActivityForResult(intent, REQUEST_CODE_EXPENSE);
    }

    public void productClick(View v){
        Intent intent = new Intent(getApplicationContext(), Product.class);
        startActivityForResult(intent, REQUEST_CODE_PRODUCT);
    }


}
