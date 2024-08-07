package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.Constants.POSITION;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;

import android.content.Intent;
import android.os.Debug;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.leon.counter_reading.R;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityHomeBinding;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private ActivityHomeBinding binding;
    private boolean exit = false;

    @Override
    protected void initialize() {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        initializeImageViews();
        setOnImageViewClickListener();
    }

    void initializeImageViews() {
        binding.imageViewAppSetting.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_app_settings));
        binding.imageViewDownload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_download_information));
        binding.imageViewUpload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_upload));
        binding.imageViewReadingSetting.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_reading_settings));
        binding.imageViewHelp.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_help));
        binding.imageViewExit.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_exit));
        binding.imageViewReading.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_readings));
        binding.imageViewReport.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_reading_report));
        binding.imageViewLocation.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.img_location));
    }

    void setOnImageViewClickListener() {
        binding.linearLayoutDownload.setOnClickListener(this);
        binding.linearLayoutReading.setOnClickListener(this);
        binding.linearLayoutUpload.setOnClickListener(this);
        binding.linearLayoutReport.setOnClickListener(this);
        binding.linearLayoutHelp.setOnClickListener(this);
        binding.linearLayoutLocation.setOnClickListener(this);
        binding.linearLayoutAppSetting.setOnClickListener(this);
        binding.linearLayoutReadingSetting.setOnClickListener(this);
        binding.linearLayoutExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        if (id == R.id.linear_layout_download) {
            POSITION = 0;
            intent = new Intent(getApplicationContext(), DownloadActivity.class);
        } else if (id == R.id.linear_layout_reading) {
            POSITION = 1;
            intent = new Intent(getApplicationContext(), ReadingActivity.class);
        } else if (id == R.id.linear_layout_upload) {
            POSITION = 2;
            intent = new Intent(getApplicationContext(), UploadActivity.class);
        } else if (id == R.id.linear_layout_report) {
            POSITION = 3;
            intent = new Intent(getApplicationContext(), ReportActivity.class);
        } else if (id == R.id.linear_layout_location) {
            POSITION = 4;
            intent = new Intent(getApplicationContext(), LocationActivity.class);
        } else if (id == R.id.linear_layout_reading_setting) {
            POSITION = 5;
            intent = new Intent(getApplicationContext(), ReadingSettingActivity.class);
        } else if (id == R.id.linear_layout_app_setting) {
            POSITION = 6;
            intent = new Intent(getApplicationContext(), SettingActivity.class);
        } else if (id == R.id.linear_layout_help) {
            POSITION = 7;
            intent = new Intent(getApplicationContext(), HelpActivity.class);
        } else if (id == R.id.linear_layout_exit) {
            exit = true;
            finishAffinity();
        }
        if (id != R.id.linear_layout_exit) {
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            binding.imageViewAppSetting.setImageDrawable(null);
            binding.imageViewDownload.setImageDrawable(null);
            binding.imageViewUpload.setImageDrawable(null);
            binding.imageViewReadingSetting.setImageDrawable(null);
            binding.imageViewHelp.setImageDrawable(null);
            binding.imageViewExit.setImageDrawable(null);
            binding.imageViewReading.setImageDrawable(null);
            binding.imageViewReport.setImageDrawable(null);
            binding.imageViewLocation.setImageDrawable(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding = null;
        if (exit)
            android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}