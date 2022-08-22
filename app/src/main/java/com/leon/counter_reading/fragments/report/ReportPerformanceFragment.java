package com.leon.counter_reading.fragments.report;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentReportPerformanceBinding;
import com.leon.counter_reading.utils.performance.GetPerformance;
import com.leon.counter_reading.view_models.PerformanceViewModel;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;

public class ReportPerformanceFragment extends Fragment implements View.OnClickListener {
    private FragmentReportPerformanceBinding binding;
    private long lastClickTime = 0;
    private final PerformanceViewModel performanceVM = new PerformanceViewModel();

    public ReportPerformanceFragment() {
    }

    public static ReportPerformanceFragment newInstance() {
        return new ReportPerformanceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportPerformanceBinding.inflate(inflater, container, false);
        binding.setPerformanceVM(performanceVM);
        return binding.getRoot();
    }

    private void initialize() {
        binding.textViewFrom.setOnClickListener(this);
        binding.textViewEnd.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
    }

    public void setTextViewTextSetter(PerformanceViewModel performanceVM) {
        this.performanceVM.setDetailVisibility(View.VISIBLE);
        this.performanceVM.setAdiCount(performanceVM.getAdiCount());
        this.performanceVM.setOveralCount(performanceVM.getOveralCount());
        this.performanceVM.setMediaCount(performanceVM.getMediaCount());
        this.performanceVM.setForbiddenCount(performanceVM.getForbiddenCount());
        this.performanceVM.setManeCount(performanceVM.getManeCount());
        this.performanceVM.setXarabCount(performanceVM.getXarabCount());
        this.performanceVM.setTavizCount(performanceVM.getTavizCount());
        this.performanceVM.setFaqedCount(performanceVM.getFaqedCount());
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.button_submit) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            new GetPerformance(requireActivity(), this, performanceVM).execute(requireActivity());
        } else if (id == R.id.text_view_from || id == R.id.text_view_end) {
            final DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
            datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Single);
            datePickerDialog.setDisableDaysAgo(false);
            datePickerDialog.setTextSizeTitle(10.0f);
            datePickerDialog.setTextSizeWeek(12.0f);
            datePickerDialog.setTextSizeDate(14.0f);
            datePickerDialog.setCanceledOnTouchOutside(true);
            datePickerDialog.setOnSingleDateSelectedListener(date -> {
                performanceVM.setDetailVisibility(View.GONE);
                if (id == R.id.text_view_from)
                    performanceVM.setFromDate(date.getPersianShortDate());
                else performanceVM.setToDate(date.getPersianShortDate());
            });
            datePickerDialog.showDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}