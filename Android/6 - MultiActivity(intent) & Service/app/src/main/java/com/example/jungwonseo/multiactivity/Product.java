package com.example.jungwonseo.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.example.jungwonseo.multiactivity.Customer.REQUEST_CODE_LOGIN;

public class Product extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
    }
    public void menuClick(View view) {
        finish();
    }

    public void loginClick(View view){
        Intent intent= new Intent(getApplicationContext(), Login.class);
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
        finish();
    }

}
