package com.example.xposedmoudle;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xposedmoudle.replace.MyInstrumetation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
//        tv.setText("1234567890");
//        startActivity(new Intent());
        MyInstrumetation myInstrumetation = new MyInstrumetation(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "======:onPause");
    }
}