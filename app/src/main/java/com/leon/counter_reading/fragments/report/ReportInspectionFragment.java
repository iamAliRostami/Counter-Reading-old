package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.databinding.FragmentReportInspectionBinding;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.reporting.PrepareOffload;
import com.leon.counter_reading.view_models.ReportInspectionViewModel;

public class ReportInspectionFragment extends Fragment {
    private final ReportInspectionViewModel reportInspection = new ReportInspectionViewModel();
    private FragmentReportInspectionBinding binding;
    private long lastClickTime = 0;

    public static ReportInspectionFragment newInstance() {
        return new ReportInspectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReportInspection();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportInspectionBinding.inflate(inflater, container, false);
        binding.setReportInspection(reportInspection);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.buttonSubmitInspection.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            new PrepareOffload(requireActivity(), this).execute(requireActivity());
        });
    }

    public void setReportInspection() {
        final MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        reportInspection.setTotalReport(myDatabase.offLoadReportDao().getTotalReport());
        reportInspection.setUnsentReports(myDatabase.offLoadReportDao().getReportBySent(false));
        reportInspection.setSentReports(myDatabase.offLoadReportDao().getReportBySent(true));
    }
}