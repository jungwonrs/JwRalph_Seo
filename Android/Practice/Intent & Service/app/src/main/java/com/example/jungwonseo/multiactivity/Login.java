package com.example.jungwonseo.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    //for keyboard variable
    InputMethodManager imm;
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //keyboard
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }


    //backbtn -> exit application
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

    //keyboard setting
    public void loginLayout(View view) {
        EditText loginInput = (EditText) findViewById(R.id.loginInput);
        imm.hideSoftInputFromWindow(loginInput.getWindowToken(), 0);
    }

    public void loginClick(View view) {
        EditText idInput = (EditText) findViewById(R.id.loginInput);
        EditText pwInput = (EditText) findViewById(R.id.passwordInput);

        String CidInput = idInput.getText().toString();
        String CpwInput = pwInput.getText().toString();


        if (CidInput.equals("")) {

            Toast.makeText(getApplicationContext(), "Please type your id", Toast.LENGTH_SHORT).show();

        } else if (CpwInput.equals("")) {
            Toast.makeText(getApplicationContext(), "Please type your password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Welcome, " + CidInput, Toast.LENGTH_SHORT).show();

            // time stamp
            long tempTime = System.currentTimeMillis();
            String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", tempTime).toString();

            //data send to service
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            intent.putExtra("id", CidInput);
            intent.putExtra("time", date);
            startService(intent);
            finish();
        }
    }


}