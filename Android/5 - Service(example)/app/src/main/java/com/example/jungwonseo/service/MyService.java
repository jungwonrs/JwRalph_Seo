package com.example.jungwonseo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG = "MyService";


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate() is called");
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        Log.d(TAG, "onStartCommand() is called");

        if (intent == null){
            return Service.START_STICKY;
        } else {
            processCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void processCommand(Intent intent) {
        String command = intent.getStringExtra("command");
        String name = intent.getStringExtra("name");

        Log.d(TAG, "command :" + command + ", name : " + name);

        for (int i = 0; i<5; i++){
            try{
                Thread.sleep(1000);
            } catch (Exception e) {};
            Log.d(TAG, "Waiting" + i + "seconds.");

        }
        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);
        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showIntent.putExtra("command", "show");
        showIntent.putExtra("name", name+"from service.");
        startActivity(showIntent);}


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
