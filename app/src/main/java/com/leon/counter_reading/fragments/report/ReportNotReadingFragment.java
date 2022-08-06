package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.enums.BundleEnum.TOTAL;
import static com.leon.counter_reading.enums.BundleEnum.UNREAD;
import static com.leon.counter_reading.helpers.Constants.POSITION;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentReportNotReadingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;

import org.jetbrains.annotations.NotNull;

public class ReportNotReadingFragment extends Fragment {
    private FragmentReportNotReadingBinding binding;
    private int unread, total;
    private Activity activity;

    public static ReportNotReadingFragment newInstance(int total, int unread) {
        ReportNotReadingFragment fragment = new ReportNotReadingFragment();
        fragment.setArguments(putBundle(total, unread));
        return fragment;
    }

    static Bundle putBundle(int total, int unread) {
        Bundle args = new Bundle();
        args.putInt(UNREAD.getValue(), unread);
        args.putInt(TOTAL.getValue(), total);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportNotReadingBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.textViewNotRead.setText(String.valueOf(unread));
        binding.textViewTotal.setText(String.valueOf(total));
        binding.imageViewNotRead.setImageDrawable(ContextCompat.getDrawable(activity,
                R.drawable.img_not_read));
        binding.buttonContinue.setOnClickListener(v -> {
            final Intent intent = new Intent(activity, ReadingActivity.class);
            intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.UNREAD.getValue());
            POSITION = 1;
            startActivity(intent);
        });
    }

    private void getBundle() {
        if (getArguments() != null) {
            total = getArguments().getInt(TOTAL.getValue());
            unread = getArguments().getInt(UNREAD.getValue());
            getArguments().clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewNotRead.setImageDrawable(null);
    }
}