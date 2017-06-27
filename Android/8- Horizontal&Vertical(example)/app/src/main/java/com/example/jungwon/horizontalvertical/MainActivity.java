package com.example.jungwon.horizontalvertical;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String name;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showToast("Oncreate");

     editText = (EditText) findViewById(R.id.editText);

    Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            name = editText.getText().toString();
            Toast.makeText(getApplicationContext(), "Input values are saved  " + name, Toast.LENGTH_LONG).show();
        }
    });

        if (savedInstanceState != null) {
        name = savedInstanceState.getString("name");

        Toast.makeText(getApplicationContext(), "values are retured  " + name, Toast.LENGTH_LONG).show();
    }
}
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", name);
    }


    public void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

}


