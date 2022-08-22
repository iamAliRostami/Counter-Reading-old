package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.enums.BundleEnum.READ_STATUS;
import static com.leon.counter_reading.enums.BundleEnum.TOTAL;
import static com.leon.counter_reading.enums.BundleEnum.UNREAD;
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
import com.leon.counter_reading.databinding.FragmentReportNotReadingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;

import org.jetbrains.annotations.NotNull;

public class ReportNotReadingFragment extends Fragment implements View.OnClickListener {
    private FragmentReportNotReadingBinding binding;
    private int unread, total;

    public static ReportNotReadingFragment newInstance(int total, int unread) {
        final ReportNotReadingFragment fragment = new ReportNotReadingFragment();
        final Bundle args = new Bundle();
        args.putInt(UNREAD.getValue(), unread);
        args.putInt(TOTAL.getValue(), total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            total = getArguments().getInt(TOTAL.getValue());
            unread = getArguments().getInt(UNREAD.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportNotReadingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        binding.textViewNotRead.setText(String.valueOf(unread));
        binding.textViewTotal.setText(String.valueOf(total));
        binding.imageViewNotRead.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.img_not_read));
        binding.buttonContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_continue) {
            final Intent intent = new Intent(requireContext(), ReadingActivity.class);
            intent.putExtra(READ_STATUS.getValue(), ReadStatusEnum.UNREAD.getValue());
            POSITION = 1;
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewNotRead.setImageDrawable(null);
    }
}