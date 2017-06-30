package com.example.hankwu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button floatWindowBtn = new Button(this);

//        setContentView(R.layout.activity_main);
        setContentView(floatWindowBtn);

        floatWindowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this,FloatingWindow.class));
            }
        });


    }
}
