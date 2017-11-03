package com.example.jungwonseo.multiactivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            //Start_Sticky == restart
            return Service.START_STICKY;
        }else {
            processCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void processCommand(Intent intent){
        String id = intent.getStringExtra("id");
        String time = intent.getStringExtra("time");

        Log.d(TAG, "id : " +id+ " time : " +time);

        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);
        showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        showIntent.putExtra("FinalData", "ID"+"\n" +id+"\n\n"+"Time"+"\n"+time);
        startActivity(showIntent);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
