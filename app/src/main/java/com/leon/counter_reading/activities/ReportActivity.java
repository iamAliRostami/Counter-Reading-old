package com.leon.counter_reading.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReportBinding;
import com.leon.counter_reading.fragments.report.ReportForbidsFragment;
import com.leon.counter_reading.fragments.report.ReportInspectionFragment;
import com.leon.counter_reading.fragments.report.ReportNotReadingFragment;
import com.leon.counter_reading.fragments.report.ReportPerformanceFragment;
import com.leon.counter_reading.fragments.report.ReportTemporaryFragment;
import com.leon.counter_reading.fragments.report.ReportTotalFragment;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.reporting.GetReportDBData;

import java.util.ArrayList;

public class ReportActivity extends BaseActivity implements ReportTemporaryFragment.Callback {
    private ActivityReportBinding binding;
    private Activity activity;
    private int previousState;
    private int currentState;
    private ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        new GetReportDBData(activity).execute(activity);
        initializeTextViews();
    }

    private void initializeTextViews() {
        textViewTotalNormal();
        textViewTemporary();
        textViewNotRead();
        textViewInspection();
        textViewForbid();
        textViewPerformance();
    }

    private void textViewTotalNormal() {
        binding.textViewTotal.setOnClickListener(view -> {
            setColor();
            binding.textViewTotal.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void textViewNotRead() {
        binding.textViewNotRead.setOnClickListener(view -> {
            setColor();
            binding.textViewNotRead.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    private void textViewTemporary() {
        binding.textViewTemporary.setOnClickListener(view -> {
            setColor();
            binding.textViewTemporary.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    private void textViewForbid() {
        binding.textViewForbid.setOnClickListener(view -> {
            setColor();
            binding.textViewForbid.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(3);
        });
    }

    private void textViewInspection() {
        binding.textViewInspection.setOnClickListener(view -> {
            setColor();
            binding.textViewInspection.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(4);
        });
    }


    private void textViewPerformance() {
        binding.textViewPerformance.setOnClickListener(view -> {
            setColor();
            binding.textViewPerformance.setBackground(ContextCompat.getDrawable(activity,
                    R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(5);
        });
    }

    private void setColor() {
        binding.textViewNotRead.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewNotRead.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewTotal.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewTotal.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewTemporary.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewTemporary.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));

        binding.textViewInspection.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewInspection.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewForbid.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewForbid.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));

        binding.textViewPerformance.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewPerformance.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
    }

    private void setPadding() {
        final int medium = (int) getResources().getDimension(R.dimen.medium_dp);
        final int large = (int) getResources().getDimension(R.dimen.large_dp);
        binding.textViewTotal.setPadding(large, medium, large, medium);
        binding.textViewNotRead.setPadding(large, medium, large, medium);
        binding.textViewTemporary.setPadding(large, medium, large, medium);
        binding.textViewInspection.setPadding(large, medium, large, medium);
        binding.textViewForbid.setPadding(large, medium, large, medium);
        binding.textViewPerformance.setPadding(large, medium, large, medium);
    }

    public void setupViewPager(ArrayList<CounterStateDto> counterState, ArrayList<TrackingDto> tracking,
                               int zero, int normal, int high, int low, int total, int isMane,
                               int unread) {
        this.counterStateDtos = new ArrayList<>(counterState);
        this.trackingDtos = new ArrayList<>(tracking);

        final ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(ReportTotalFragment.newInstance(zero, normal, high, low));
        adapter.addFragment(ReportNotReadingFragment.newInstance(total, unread));
        adapter.addFragment(ReportTemporaryFragment.newInstance(total, isMane));
        adapter.addFragment(ReportForbidsFragment.newInstance(
        ));
        adapter.addFragment(ReportInspectionFragment.newInstance());
        adapter.addFragment(ReportPerformanceFragment.newInstance());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewTotal.callOnClick();
                } else if (position == 1) {
                    binding.textViewNotRead.callOnClick();
                } else if (position == 2) {
                    binding.textViewTemporary.callOnClick();
                } else if (position == 3) {
                    binding.textViewForbid.callOnClick();
                } else if (position == 4) {
                    binding.textViewInspection.callOnClick();
                } else if (position == 5) {
                    binding.textViewPerformance.callOnClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                final int currentPage = binding.viewPager.getCurrentItem();
                if (currentPage == 5 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        binding.viewPager.setCurrentItem(currentPage == 0 ? 5 : 0);
                    }
                }
            }
        });
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public ArrayList<CounterStateDto> getCounterStateDtos() {
        return counterStateDtos;
    }

    public ArrayList<TrackingDto> getTrackingDtos() {
        return trackingDtos;
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
        binding = null;
        counterStateDtos = null;
        trackingDtos = null;
        super.onDestroy();
    }
}