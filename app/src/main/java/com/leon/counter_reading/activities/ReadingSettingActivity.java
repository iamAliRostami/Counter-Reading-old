package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.graphics.Color;
import android.os.Debug;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerTabAdapter;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingSettingBinding;
import com.leon.counter_reading.fragments.reading_setting.ReadingPossibleSettingFragment;
import com.leon.counter_reading.fragments.reading_setting.ReadingSettingActiveFragment;
import com.leon.counter_reading.fragments.reading_setting.ReadingSettingDeleteFragment;
import com.leon.counter_reading.fragments.reading_setting.ReadingSettingFeaturesFragment;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.DepthPageTransformer2;

import java.util.ArrayList;

public class ReadingSettingActivity extends BaseActivity implements View.OnClickListener {
    private ActivityReadingSettingBinding binding;
    private int currentState;
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityReadingSettingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        trackingDtos.addAll(getApplicationComponent().MyDatabase().trackingDao()
                .getTrackingDtoNotArchive(false));
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        binding.textViewRead.setOnClickListener(this);
        binding.textViewFeatures.setOnClickListener(this);
        binding.textViewNavigation.setOnClickListener(this);
        binding.textViewDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        setColor();
        if (id == R.id.text_view_delete) {
            binding.textViewDelete.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(3);
        } else if (id == R.id.text_view_navigation) {
            binding.textViewNavigation.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(2);
        } else if (id == R.id.text_view_features) {
            binding.textViewFeatures.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(1);
        } else if (id == R.id.text_view_read) {
            binding.textViewRead.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(0);
        }
        setPadding();
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
        final ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(this);
        adapter.addFragment(ReadingSettingActiveFragment.newInstance(trackingDtos));
        adapter.addFragment(ReadingSettingFeaturesFragment.newInstance());
        adapter.addFragment(new ReadingPossibleSettingFragment());
        adapter.addFragment(ReadingSettingDeleteFragment.newInstance(trackingDtos));
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.registerOnPageChangeCallback(pageChangeListener);
        binding.viewPager.setPageTransformer(new DepthPageTransformer2());
    }

    private final ViewPager2.OnPageChangeCallback pageChangeListener = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            final int currentPage = binding.viewPager.getCurrentItem();
            if (currentPage == 3 || currentPage == 0) {
                int previousState = currentState;
                currentState = state;
                if (previousState == 1 && currentState == 0) {
                    binding.viewPager.setCurrentItem(currentPage == 0 ? 3 : 0);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
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
    };

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