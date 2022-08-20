package com.leon.counter_reading.helpers;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.activities.StartActivity;
import com.leon.counter_reading.base_items.BaseActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this, ReadingActivity.class));
        startActivity(new Intent(this, StartActivity.class));
    }
}