package com.example.jungwonseo.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Customer extends AppCompatActivity {
    public static final int REQUEST_CODE_LOGIN = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
    }

    public void menuClick(View view) {
        finish();

    }

    //show up login Menu
    public void loginClick(View view){
        Intent intent= new Intent(getApplicationContext(), Login.class);
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
        finish();
    }
}
