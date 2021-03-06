package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.Constants.ZIP_ROOT;
import static com.leon.counter_reading.helpers.Constants.zipAddress;
import static com.leon.counter_reading.utils.CustomFile.copyFile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityUploadBinding;
import com.leon.counter_reading.enums.UploadType;
import com.leon.counter_reading.fragments.upload.UploadFragment;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.uploading.GetUploadDBData;

import java.io.File;
import java.util.ArrayList;

public class UploadActivity extends BaseActivity {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private ActivityUploadBinding binding;
    private int previousState, currentState;

    public ArrayList<TrackingDto> getTrackingDtos() {
        return trackingDtos;
    }

    @Override
    protected void initialize() {
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.getCompanyName(DifferentCompanyManager.getActiveCompanyName()));

        new GetUploadDBData(this).execute(this);
    }

    private void setupViewPager() {
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(UploadFragment.newInstance(UploadType.NORMAL.getValue()));
        adapter.addFragment(UploadFragment.newInstance(UploadType.OFFLINE.getValue()));
//        adapter.addFragment(UploadOfflineFragment.newInstance());
        adapter.addFragment(UploadFragment.newInstance(UploadType.MULTIMEDIA.getValue()));
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewUpload.callOnClick();
                } else if (position == 1) {
                    binding.textViewUploadOff.callOnClick();
                } else if (position == 2) {
                    binding.textViewUploadMultimedia.callOnClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int currentPage = binding.viewPager.getCurrentItem();
                if (currentPage == 2 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        binding.viewPager.setCurrentItem(currentPage == 0 ? 2 : 0);
                    }
                }
            }
        });
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    public void setupUI(ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos.clear();
        this.trackingDtos.addAll(trackingDtos);
        runOnUiThread(() -> {
            setupViewPager();
            initializeTextViews();
        });
    }

    void initializeTextViews() {
        textViewUploadNormal();
        textViewUploadMultimedia();
        textViewUploadOff();
    }

    void textViewUploadOff() {
        binding.textViewUploadOff.setOnClickListener(view -> {
            setColor();
            binding.textViewUploadOff.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    void textViewUploadMultimedia() {
        binding.textViewUploadMultimedia.setOnClickListener(view -> {
            setColor();
            binding.textViewUploadMultimedia.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    void textViewUploadNormal() {
        binding.textViewUpload.setOnClickListener(view -> {
            setColor();
            binding.textViewUpload.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void setColor() {
        binding.textViewUploadOff.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUploadOff.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewUpload.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUpload.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewUploadMultimedia.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUploadMultimedia.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewUpload.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewUploadOff.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewUploadMultimedia.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ZIP_ROOT && resultCode == RESULT_OK && data != null) {
            copyFile(new File(zipAddress), data.getData(), getApplicationContext());
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
        super.onDestroy();
    }
}