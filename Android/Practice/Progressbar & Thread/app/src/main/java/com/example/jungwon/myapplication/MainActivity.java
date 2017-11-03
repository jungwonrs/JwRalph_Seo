package com.example.jungwon.myapplication;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Thread thread;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("ProcessBar");

        progressBar = findViewById(R.id.progressbar);
        progressBar.setIndeterminate(false); //show progressing status. Default:false.
        progressBar.setMax(100); // progressbar maximum size

        Button button;
        button = findViewById(R.id.progressbar_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(MainActivity.this, Dialog.class);
              startActivity(intent); // show dialog box
            }
        });

        //Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(i<100){
                    i++;
                    progressBar.setProgress(i);
                    try{
                        thread.sleep(1000); // 1000 = 1 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("seo", Integer.toString(i)); //Check thread status
                }
            }
        }).start(); //to start thread
    }


}