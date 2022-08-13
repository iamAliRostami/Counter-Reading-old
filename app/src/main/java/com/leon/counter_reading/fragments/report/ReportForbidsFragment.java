package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.databinding.FragmentReportForbidsBinding;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.reporting.PrepareReport;
import com.leon.counter_reading.view_models.ReportForbidViewModel;

public class ReportForbidsFragment extends Fragment {
    private long lastClickTime = 0;
    private final ReportForbidViewModel reportForbid = new ReportForbidViewModel();
    private FragmentReportForbidsBinding binding;

    public static ReportForbidsFragment newInstance() {
        return new ReportForbidsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReportForbid();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportForbidsBinding.inflate(inflater, container, false);
        binding.setReportForbid(reportForbid);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.buttonSubmitForbidden.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            new PrepareReport(requireActivity(), this).execute(requireActivity());
        });
    }

    public void setButtonState() {
        binding.buttonSubmitForbidden.setEnabled(true);
    }

    public void setReportForbid() {
        final MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        reportForbid.setTotalReport(myDatabase.forbiddenDao().getTotalForbidden());
        reportForbid.setUnsentReports(myDatabase.forbiddenDao().getTotalForbiddenBySent(false));
        reportForbid.setSentReports(myDatabase.forbiddenDao().getTotalForbiddenBySent(true));
        reportForbid.setForbiddenReports(myDatabase.forbiddenDao().getTotalForbiddenByType(false));
        reportForbid.setActivationReports(myDatabase.forbiddenDao().getTotalForbiddenByType(true));
    }
}