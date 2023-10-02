package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.BundleEnum.ZONE_ID;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.utils.MakeNotification.makeVibrate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ReadingReportAdapter;
import com.leon.counter_reading.adapters.recycler_view.RecyclerItemClickListener;
import com.leon.counter_reading.databinding.FragmentReadingReportBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.reporting.GetReadingReportDBData;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ReadingReportFragment extends DialogFragment {
    //    private ArrayList<OffLoadReport> offLoadReports;
    private ReadingReportAdapter adapter;
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
        final Bundle args = new Bundle();
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
//        makeVibrate(requireContext(), 200);
        new GetReadingReportDBData(requireActivity(), trackNumber, zoneId, uuid).execute(requireActivity());
        onItemClickListener();
        binding.buttonSubmit.setOnClickListener(v -> {
            makeVibrate(requireContext(), 200);
            String message = MessageFormat.format(getString(R.string.selected_report_number), adapter.selectedCount());
            new CustomToast().info(message);
            dismiss();
            readingActivity.setResult(position, uuid);
        });
    }

    private void onItemClickListener() {
        binding.listViewReports.addOnItemTouchListener(new RecyclerItemClickListener(requireContext(),
                binding.listViewReports, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                makeVibrate(requireContext(), 100);
                adapter.update(position);
                binding.buttonSubmit.setText(MessageFormat.format(getString(R.string.submit_count), adapter.selectedCount()));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public void setupRecyclerView(ArrayList<CounterReportDto> counterReportDtos,
                                  ArrayList<OffLoadReport> offLoadReports) {
        adapter = new ReadingReportAdapter(requireContext(), uuid, trackNumber, position, counterReportDtos, offLoadReports);
        requireActivity().runOnUiThread(() -> {
            binding.listViewReports.setAdapter(adapter);
            binding.listViewReports.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.buttonSubmit.setText(MessageFormat.format(getString(R.string.submit_count), adapter.selectedCount()));
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            new CustomDialogModel(Red, requireContext(), getString(R.string.refresh_page),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted));
        }
        super.onResume();
    }

    public interface Callback {
        void setResult(int position, String uuid);
    }
}