package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.enums.BundleEnum.READ_STATUS;
import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.enums.ReadStatusEnum.READ;
import static com.leon.counter_reading.enums.ReadStatusEnum.STATE;
import static com.leon.counter_reading.helpers.Constants.POSITION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentReportTotalBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.view_models.TotalViewModel;

import org.eazegraph.lib.models.PieModel;
import org.jetbrains.annotations.NotNull;

public class ReportTotalFragment extends Fragment implements View.OnClickListener {
    private FragmentReportTotalBinding binding;
    private TotalViewModel totalVM;

    public static ReportTotalFragment newInstance(int zero, int normal, int high, int low) {
        final ReportTotalFragment fragment = new ReportTotalFragment();
        final Bundle args = new Bundle();
        args.putInt(BundleEnum.ZERO.getValue(), zero);
        args.putInt(BundleEnum.HIGH.getValue(), high);
        args.putInt(BundleEnum.LOW.getValue(), low);
        args.putInt(BundleEnum.NORMAL.getValue(), normal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalVM = new TotalViewModel(getArguments().getInt(BundleEnum.ZERO.getValue()),
                    getArguments().getInt(BundleEnum.NORMAL.getValue()),
                    getArguments().getInt(BundleEnum.HIGH.getValue()),
                    getArguments().getInt(BundleEnum.LOW.getValue()));
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportTotalBinding.inflate(inflater, container, false);
        binding.setTotalVM(totalVM);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        setupChart();
        binding.linearLayoutHigh.setOnClickListener(this);
        binding.linearLayoutLow.setOnClickListener(this);
        binding.linearLayoutZero.setOnClickListener(this);
        binding.linearLayoutNormal.setOnClickListener(this);
        binding.linearLayoutTotal.setOnClickListener(this);
    }

    private void setupChart() {
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.zero), totalVM.getZero(), ContextCompat.getColor(requireContext(), R.color.blue)));
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.normal), totalVM.getNormal(), ContextCompat.getColor(requireContext(), R.color.green)));
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.down), totalVM.getLow(), ContextCompat.getColor(requireContext(), R.color.yellow)));
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.up), totalVM.getHigh(), ContextCompat.getColor(requireContext(), R.color.red)));
        binding.pieChart.startAnimation();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        final Intent intent = new Intent(getActivity(), ReadingActivity.class);
        intent.putExtra(READ_STATUS.getValue(), STATE.getValue());
        if (id == R.id.linear_layout_normal) {
            intent.putExtra(TYPE.getValue(), NORMAL.getValue());
        } else if (id == R.id.linear_layout_zero) {
            intent.putExtra(TYPE.getValue(), ZERO.getValue());
        } else if (id == R.id.linear_layout_high) {
            intent.putExtra(TYPE.getValue(), HIGH.getValue());
        } else if (id == R.id.linear_layout_low) {
            intent.putExtra(TYPE.getValue(), LOW.getValue());
        } else {
            intent.putExtra(READ_STATUS.getValue(), READ.getValue());
        }
        POSITION = 1;
        startActivity(intent);
        requireActivity().finish();
    }
}