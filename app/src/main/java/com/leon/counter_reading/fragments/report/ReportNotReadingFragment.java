package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.enums.BundleEnum.BEFORE_READ;
import static com.leon.counter_reading.enums.BundleEnum.CONTINUE;
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
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.view_models.NotReadingViewModel;

import org.jetbrains.annotations.NotNull;

public class ReportNotReadingFragment extends Fragment implements View.OnClickListener {
    private FragmentReportNotReadingBinding binding;
    private NotReadingViewModel notReadingVM;

    public static ReportNotReadingFragment newInstance(int total, int unread, int beforeRead, int continueRead) {
        final ReportNotReadingFragment fragment = new ReportNotReadingFragment();
        final Bundle args = new Bundle();
        args.putInt(UNREAD.getValue(), unread);
        args.putInt(BEFORE_READ.getValue(), beforeRead);
        args.putInt(CONTINUE.getValue(), continueRead);
        args.putInt(TOTAL.getValue(), total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notReadingVM = new NotReadingViewModel(getArguments().getInt(TOTAL.getValue()),
                    getArguments().getInt(UNREAD.getValue()), getArguments().getInt(BEFORE_READ.getValue()),
                    getArguments().getInt(CONTINUE.getValue()));
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportNotReadingBinding.inflate(inflater, container, false);
        binding.setNotReadingVM(notReadingVM);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        binding.imageViewNotRead.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.img_not_read));
        binding.buttonContinue.setOnClickListener(this);
        binding.buttonUnread.setOnClickListener(this);
        binding.buttonRead.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(requireContext(), ReadingActivity.class);
        if (id == R.id.button_continue) {
            intent.putExtra(READ_STATUS.getValue(), ReadStatusEnum.CONTINUE.getValue());
        } else if (id == R.id.button_unread) {
            intent.putExtra(READ_STATUS.getValue(), ReadStatusEnum.UNREAD.getValue());
        } else if (id == R.id.button_read) {
            intent.putExtra(READ_STATUS.getValue(), ReadStatusEnum.BEFORE_READ.getValue());
        }
        POSITION = 1;
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewNotRead.setImageDrawable(null);
    }
}