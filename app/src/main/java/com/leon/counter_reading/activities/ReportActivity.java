package com.leon.counter_reading.activities;

import android.graphics.Color;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerTabAdapter;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReportBinding;
import com.leon.counter_reading.fragments.report.ReportForbidsFragment;
import com.leon.counter_reading.fragments.report.ReportInspectionFragment;
import com.leon.counter_reading.fragments.report.ReportNotReadingFragment;
import com.leon.counter_reading.fragments.report.ReportPerformanceFragment;
import com.leon.counter_reading.fragments.report.ReportTemporaryFragment;
import com.leon.counter_reading.fragments.report.ReportTotalFragment;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.reporting.GetReportDBData;

import java.util.ArrayList;

public class ReportActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, ReportTemporaryFragment.Callback {
    private ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private ActivityReportBinding binding;
    private int currentState;

    @Override
    protected void initialize() {
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        new GetReportDBData(this).execute(this);
        binding.textViewTotal.setOnClickListener(this);
        binding.textViewNotRead.setOnClickListener(this);
        binding.textViewForbid.setOnClickListener(this);
        binding.textViewInspection.setOnClickListener(this);
        binding.textViewPerformance.setOnClickListener(this);
        binding.textViewTemporary.setOnClickListener(this);
    }

    private void setColor() {
        binding.textViewNotRead.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewNotRead.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));

        binding.textViewTotal.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewTotal.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));

        binding.textViewTemporary.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewTemporary.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));

        binding.textViewInspection.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewInspection.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));

        binding.textViewForbid.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewForbid.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));

        binding.textViewPerformance.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewPerformance.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
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

    public void setupViewPager(ArrayList<CounterStateDto> counterState, int zero, int normal,
                               int high, int low, int total, int isMane, int unread) {
        this.counterStateDtos = new ArrayList<>(counterState);
        final ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        adapter.addFragment(ReportTotalFragment.newInstance(zero, normal, high, low));
        adapter.addFragment(ReportNotReadingFragment.newInstance(total, unread));
        adapter.addFragment(ReportTemporaryFragment.newInstance(total, isMane));
        adapter.addFragment(ReportForbidsFragment.newInstance());
        adapter.addFragment(ReportInspectionFragment.newInstance());
        adapter.addFragment(ReportPerformanceFragment.newInstance());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(this);
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

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
            final int previousState = currentState;
            currentState = state;
            if (previousState == 1 && currentState == 0) {
                binding.viewPager.setCurrentItem(currentPage == 0 ? 5 : 0);
            }
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        setColor();
        if (id == R.id.text_view_performance) {
            binding.textViewPerformance.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(5);
        } else if (id == R.id.text_view_inspection) {
            binding.textViewInspection.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(4);
        } else if (id == R.id.text_view_forbid) {
            binding.textViewForbid.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(3);
        } else if (id == R.id.text_view_temporary) {
            binding.textViewTemporary.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(2);
        } else if (id == R.id.text_view_not_read) {
            binding.textViewNotRead.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(1);
        } else if (id == R.id.text_view_total) {
            binding.textViewTotal.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(0);
        }
        setPadding();
    }

    @Override
    public ArrayList<CounterStateDto> getCounterStateDtos() {
        return counterStateDtos;
    }

    @Override
    protected void onDestroy() {
        binding = null;
        counterStateDtos = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}