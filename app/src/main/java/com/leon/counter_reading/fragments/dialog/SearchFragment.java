package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.SearchTypeEnum.All;
import static com.leon.counter_reading.enums.SearchTypeEnum.BARCODE;
import static com.leon.counter_reading.enums.SearchTypeEnum.NAME;
import static com.leon.counter_reading.enums.SearchTypeEnum.RADIF;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getSecondSearchItem;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentSearchBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

public class SearchFragment extends DialogFragment {
    private FragmentSearchBinding binding;
    private int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeSpinner();
        setOnButtonSearchClickListener();
        binding.editTextSearch.requestFocus();
    }

    private void setOnButtonSearchClickListener() {
        binding.buttonSearch.setOnClickListener(v -> {
            if (type == All.getValue()) {
                ((ReadingActivity) requireActivity()).search(type, null, false);
                dismiss();
            } else {
                final String key = binding.editTextSearch.getText().toString();
                if (key.isEmpty()) {
                    binding.editTextSearch.setError(getString(R.string.error_empty));
                    binding.editTextSearch.requestFocus();
                } else {
                    if (((ReadingActivity) requireActivity()).search(type, key, binding.checkBoxGoToPage.isChecked()))
                        dismiss();
                }
            }
        });
    }

    private void initializeSpinner() {
        final String[] items = getResources().getStringArray(R.array.search_option);
        items[1] = getSecondSearchItem(getActiveCompanyName());
        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), items);
        binding.spinnerSearch.setAdapter(adapter);
        binding.spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                binding.checkBoxGoToPage.setVisibility(type >= NAME.getValue() ? View.GONE :
                        View.VISIBLE);
                binding.editTextSearch.setInputType(type == NAME.getValue() ?
                        InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_NUMBER);
                binding.editTextSearch.setVisibility(type >= BARCODE.getValue() ? View.GONE :
                        View.VISIBLE);
                if (type == BARCODE.getValue())
                    scanFromFragment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void scanFromFragment() {
        IntentIntegrator.forSupportFragment(this).setOrientationLocked(false).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                new CustomToast().warning(getString(R.string.data_not_found));
            } else {
                binding.editTextSearch.setText(result.getContents());
                binding.editTextSearch.setVisibility(View.VISIBLE);
            }
            binding.spinnerSearch.setSelection(RADIF.getValue());
        }
    }

    @Override
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}