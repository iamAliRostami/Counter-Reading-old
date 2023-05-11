package com.leon.counter_reading.fragments.report;

import static com.leon.counter_reading.enums.BundleEnum.IS_MANE;
import static com.leon.counter_reading.enums.BundleEnum.READ_STATUS;
import static com.leon.counter_reading.enums.BundleEnum.TOTAL;
import static com.leon.counter_reading.enums.ReadStatusEnum.ALL_MANE;
import static com.leon.counter_reading.enums.ReadStatusEnum.ALL_MANE_UNREAD;
import static com.leon.counter_reading.helpers.Constants.POSITION;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.activities.ReportActivity;
import com.leon.counter_reading.adapters.SpinnerAdapter;
import com.leon.counter_reading.databinding.FragmentReportTemporaryBinding;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.view_models.TemporaryViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReportTemporaryFragment extends Fragment {
    private final TemporaryViewModel temporaryVM = new TemporaryViewModel();
    private FragmentReportTemporaryBinding binding;
    private Callback reportActivity;

    public static ReportTemporaryFragment newInstance(int total, int isMane) {
        final ReportTemporaryFragment fragment = new ReportTemporaryFragment();
        final Bundle args = new Bundle();
        args.putInt(TOTAL.getValue(), total);
        args.putInt(IS_MANE.getValue(), isMane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            temporaryVM.setTotal(getArguments().getInt(TOTAL.getValue()));
            temporaryVM.setMane(getArguments().getInt(IS_MANE.getValue()));
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportTemporaryBinding.inflate(inflater, container, false);
        binding.setTemporaryVM(temporaryVM);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        binding.imageViewTemporary.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.img_temporary_report));
        initializeSpinner();
    }

    private void initializeSpinner() {
        temporaryVM.setItems(CounterStateDto.getCounterStateItems(reportActivity.getCounterStateDtos(),
                new String[]{getString(R.string.select_one), getString(R.string.all_mane),
                        getString(R.string.all_mane_unread)}));
        final SpinnerAdapter adapter = new SpinnerAdapter(requireContext(), temporaryVM.getItems());
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (temporaryVM.isFirst()) {
                    temporaryVM.setFirst(false);
                    return;
                }
                final Intent intent = new Intent(getActivity(), ReadingActivity.class);
                final Gson gson = new Gson();
                final ArrayList<String> json = new ArrayList<>();
                if (position == 0) {
                    for (int i = 0, counterStateDtosSize = reportActivity.getCounterStateDtos().size(); i < counterStateDtosSize; i++) {
                        CounterStateDto counterStateDto = reportActivity.getCounterStateDtos().get(i);
                        json.add(gson.toJson(counterStateDto.id));
                    }
                } else if (position > reportActivity.getCounterStateDtos().size()) {
                    for (int i = 0, counterStateDtosSize = reportActivity.getCounterStateDtos().size(); i < counterStateDtosSize; i++) {
                        CounterStateDto counterStateDto = reportActivity.getCounterStateDtos().get(i);
                        if (counterStateDto.isMane)
                            json.add(gson.toJson(counterStateDto.id));
                    }
                } else {
                    json.add(gson.toJson(reportActivity.getCounterStateDtos().get(position - 1).id));
                }
                if (position == reportActivity.getCounterStateDtos().size() + 2) {
                    intent.putExtra(READ_STATUS.getValue(), ALL_MANE_UNREAD.getValue());
                } else {
                    intent.putExtra(READ_STATUS.getValue(), ALL_MANE.getValue());
                }
                intent.putExtra(IS_MANE.getValue(), json);
                POSITION = 1;
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ReportActivity)
            reportActivity = (Callback) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            binding.spinner.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        temporaryVM.setFirst(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewTemporary.setImageDrawable(null);
        binding = null;
    }

    public interface Callback {
        ArrayList<CounterStateDto> getCounterStateDtos();
    }
}