package com.example.jungwonseo.touchevent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    GestureDetector detector;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "back button!!!!", Toast.LENGTH_LONG).show();
            return true;
        }
        
        return false;
 }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        View view = findViewById(R.id.view1);
        View view2 = findViewById(R.id.view3);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                float curX = event.getX();
                float curY = event.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    println("finger pressure: " + curX + ", " + curY);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    println("finger move:" + curX + ", " + curY);
                } else if (action == MotionEvent.ACTION_UP) {
                    println("finger off" + curX + ", " + curY);
                }

                return true;
            }
        });

        view2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {


            @Override
            public boolean onDown(MotionEvent e) {
                println("onDown");
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                println("onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                println("onSingleTapUp");

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                println("onScroll : " +distanceX+", "+distanceY);

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                println("onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                println("onFling : " +velocityX+", "+velocityY);

                return true;
            }
        });

    }


    public void println(String data) {
        textView.append(data + "\n");
    }

}
