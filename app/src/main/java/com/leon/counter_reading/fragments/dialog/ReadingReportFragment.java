package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.BundleEnum.ZONE_ID;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.adapters.ReadingReportAdapter;
import com.leon.counter_reading.databinding.FragmentReadingReportBinding;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.reporting.GetReadingReportDBData;

import java.util.ArrayList;

public class ReadingReportFragment extends DialogFragment {
    private static ReadingReportFragment instance;
    private FragmentReadingReportBinding binding;
    private String uuid;
    private int position, trackNumber, zoneId;
    private Callback readingActivity;

    public ReadingReportFragment() {
    }

    public static ReadingReportFragment newInstance() {
        return instance;
    }

    public static ReadingReportFragment newInstance(String uuid, int trackingNumber, int position,
                                                    int zoneId) {
        instance = new ReadingReportFragment();
        Bundle args = new Bundle();
        args.putString(BILL_ID.getValue(), uuid);
        args.putInt(TRACKING.getValue(), trackingNumber);
        args.putInt(POSITION.getValue(), position);
        args.putInt(ZONE_ID.getValue(), zoneId);
        instance.setArguments(args);
        instance.setCancelable(false);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BILL_ID.getValue());
            position = getArguments().getInt(POSITION.getValue());
            trackNumber = getArguments().getInt(TRACKING.getValue());
            zoneId = getArguments().getInt(ZONE_ID.getValue());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingReportBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        new GetReadingReportDBData(requireActivity(), trackNumber, zoneId, uuid).execute(requireActivity());
        binding.buttonSubmit.setOnClickListener(v -> {
            dismiss();
            readingActivity.setResult(position, uuid);
        });
    }

    public void setupRecyclerView(ArrayList<CounterReportDto> counterReportDtos,
                                  ArrayList<OffLoadReport> offLoadReports) {
        final ReadingReportAdapter adapter =
                new ReadingReportAdapter(requireContext(), uuid, trackNumber, counterReportDtos, offLoadReports);
        requireActivity().runOnUiThread(() -> binding.listViewReports.setAdapter(adapter));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    public void onResume() {
        if (getDialog() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }

    public interface Callback {
        void setResult(int position, String uuid);
    }
}