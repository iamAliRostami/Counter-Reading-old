package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.ON_OFF_LOAD;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getEshterakMinLength;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentNavigationBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.view_models.NavigationViewModel;

public class NavigationFragment extends DialogFragment implements View.OnClickListener, TextWatcher {
    private FragmentNavigationBinding binding;
    private NavigationViewModel navigationVM;
    private Callback readingActivity;

    public NavigationFragment() {
    }

    public static NavigationFragment newInstance(int position, OnOffLoadDto onOffLoadDto) {
        final NavigationFragment fragment = new NavigationFragment();
        final Bundle args = new Bundle();
        args.putString(ON_OFF_LOAD.getValue(), new Gson().toJson(onOffLoadDto));
        args.putInt(POSITION.getValue(), position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final OnOffLoadDto onOffLoadDto = new Gson().fromJson(getArguments().getString(ON_OFF_LOAD.getValue()),
                    OnOffLoadDto.class);
            navigationVM = new NavigationViewModel(onOffLoadDto, getArguments().getInt(POSITION.getValue()));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNavigationBinding.inflate(inflater, container, false);
        binding.setNavigationVM(navigationVM);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        initializeImageViews();
        binding.buttonNavigation.setOnClickListener(this);
        binding.editTextAccount.addTextChangedListener(this);
        binding.editTextPhone.addTextChangedListener(this);
        binding.editTextMobile.addTextChangedListener(this);
        binding.editTextSerialCounter.addTextChangedListener(this);
    }

    private void submitNavigation() {
        if (!navigationVM.getPossibleEshterak().isEmpty() &&
                navigationVM.getPossibleEshterak().length() < getEshterakMinLength(getActiveCompanyName())) {
            binding.editTextAccount.setError(getString(R.string.error_format));
            binding.editTextAccount.requestFocus();
            return;
        } else if (!navigationVM.getPossiblePhoneNumber().isEmpty() &&
                navigationVM.getPossiblePhoneNumber().length() < 8) {
            binding.editTextPhone.setError(getString(R.string.error_format));
            binding.editTextPhone.requestFocus();
            return;
        } else if (!navigationVM.getPossibleMobile().isEmpty() &&
                (navigationVM.getPossibleMobile().length() < 11 ||
                        !navigationVM.getPossibleMobile().substring(0, 2).contains("09"))) {
            binding.editTextMobile.setError(getString(R.string.error_format));
            binding.editTextMobile.requestFocus();
            return;
        } else if (!navigationVM.getPossibleCounterSerial().isEmpty() &&
                navigationVM.getPossibleCounterSerial().length() < 3) {
            binding.editTextSerialCounter.setError(getString(R.string.error_format));
            binding.editTextSerialCounter.requestFocus();
            return;
        }
        navigationVM.updateOnOffLoadDto();
        getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(navigationVM.getOnOffLoadDto().id,
                navigationVM.getOnOffLoadDto().possibleEshterak, navigationVM.getOnOffLoadDto().possibleMobile,
                navigationVM.getOnOffLoadDto().possibleEmpty, navigationVM.getOnOffLoadDto().possiblePhoneNumber,
                navigationVM.getOnOffLoadDto().possibleCounterSerial, navigationVM.getOnOffLoadDto().possibleAddress);
        dismiss();
        readingActivity.setResult(navigationVM.getPosition(), navigationVM.getOnOffLoadDto().id);
    }


    private void initializeImageViews() {
        binding.imageViewAccount.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_subscribe));
        binding.imageViewAddress.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_address));
        binding.imageViewCounterSerial.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_counter));
        binding.imageViewPhoneNumber.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_phone));
        binding.imageViewMobile.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_mobile));
        binding.imageViewEmpty.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_home));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == binding.editTextAccount.getEditableText()) {
            if (editable.toString().length() == navigationVM.getEshterakMaxLength())
                binding.editTextPhone.requestFocus();
        } else if (editable == binding.editTextMobile.getEditableText()) {
            if (editable.toString().length() == 11 && editable.toString().substring(0, 2).contains("09")) {
                binding.editTextSerialCounter.requestFocus();
            } else binding.editTextMobile.setError(getString(R.string.error_format));
        } else if (editable == binding.editTextPhone.getEditableText()) {
            if (editable.toString().length() == 8) {
                binding.editTextMobile.requestFocus();
            }
        } else if (editable == binding.editTextSerialCounter.getEditableText()) {
            if (editable.toString().length() == 15) binding.editTextAddress.requestFocus();
        }
    }


    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.button_navigation) {
            submitNavigation();
        }
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