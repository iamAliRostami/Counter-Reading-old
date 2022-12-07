package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.graphics.Color;
import android.os.Debug;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingSettingBinding;
import com.leon.counter_reading.fragments.reading_setting.ReadingPossibleSettingFragment;
import com.leon.counter_reading.fragments.reading_setting.ReadingSettingActiveFragment;
import com.leon.counter_reading.fragments.reading_setting.ReadingSettingDeleteFragment;
import com.leon.counter_reading.fragments.reading_setting.ReadingSettingFeaturesFragment;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.DepthPageTransformer;

import java.util.ArrayList;

public class ReadingSettingActivity extends BaseActivity {
    private ActivityReadingSettingBinding binding;
    private int previousState, currentState;
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityReadingSettingBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);

        trackingDtos.addAll(getApplicationComponent().MyDatabase().trackingDao()
                .getTrackingDtoNotArchive(false));
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        textViewRead();
        textViewFeatures();
        textViewDelete();
        textViewNavigation();
    }

    private void textViewRead() {
        binding.textViewRead.setOnClickListener(view -> {
            setColor();
            binding.textViewRead.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void textViewFeatures() {
        binding.textViewFeatures.setOnClickListener(view -> {
            setColor();
            binding.textViewFeatures.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    private void textViewNavigation() {
        binding.textViewNavigation.setOnClickListener(view -> {
            setColor();
            binding.textViewNavigation.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    private void textViewDelete() {
        binding.textViewDelete.setOnClickListener(view -> {
            setColor();
            binding.textViewDelete.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(3);
        });
    }

    private void setColor() {
        binding.textViewRead.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewRead.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
        binding.textViewFeatures.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewFeatures.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
        binding.textViewDelete.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDelete.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
        binding.textViewNavigation.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewNavigation.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewRead.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewFeatures.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDelete.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewNavigation.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        final ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(ReadingSettingActiveFragment.newInstance(trackingDtos));
        adapter.addFragment(ReadingSettingFeaturesFragment.newInstance());
        adapter.addFragment(new ReadingPossibleSettingFragment());
        adapter.addFragment(ReadingSettingDeleteFragment.newInstance(trackingDtos));

        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewRead.callOnClick();
                } else if (position == 1) {
                    binding.textViewFeatures.callOnClick();
                } else if (position == 2) {
                    binding.textViewNavigation.callOnClick();
                } else if (position == 3) {
                    binding.textViewDelete.callOnClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                final int currentPage = binding.viewPager.getCurrentItem();
                if (currentPage == 3 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        binding.viewPager.setCurrentItem(currentPage == 0 ? 3 : 0);
                    }
                }
            }
        });
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
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
        trackingDtos = null;
        binding = null;
        super.onDestroy();
    }
}