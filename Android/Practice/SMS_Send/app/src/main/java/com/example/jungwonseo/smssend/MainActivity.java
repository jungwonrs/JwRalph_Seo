package com.example.jungwonseo.smssend;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    InputMethodManager imm;
    EditText Input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);

        //keyboard
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //count words
        Input = (EditText)findViewById(R.id.typeMessage);
        Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView TextCounter;
                TextCounter = (TextView)findViewById(R.id.textView);


                if(s.length() > 70)
                {
                    Toast.makeText(getApplicationContext(), "Cannot send", Toast.LENGTH_SHORT).show();
                }
                TextCounter.setText(s.length() + "/70");
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //keyboard setting
    public void layoutClick(View v) {
        imm.hideSoftInputFromWindow(Input.getWindowToken(),0);
    }


    //close the app button
    public void quit(View view) {
        finish();
        System.exit(0);
    }

    //call number
    private String number(){
        EditText receiver = (EditText)findViewById(R.id.receiver);

        String sendNumber = receiver.getText().toString();

        return sendNumber;
    }

    //call body
    private String SMSbody(){
       EditText Input = (EditText)findViewById(R.id.typeMessage);
        String body = Input.getText().toString();

        return body;
    }

    //send message button
    public void sending(View view) {

        Button sending = (Button) findViewById(R.id.Send);
        sending.setOnClickListener(new Button.OnClickListener() {


            @Override
            public void onClick(View v) {
                sendSMS(number(),SMSbody() );

                Log.d("test", SMSbody());
                Log.d("test", number());

            }
        });

    }

    //sms send
    private void sendSMS(String number, String body){
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"),0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"),0);

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));

        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(number, null, body, sentIntent, deliveredIntent);


    }


}
