package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.JUST_MOBILE;
import static com.leon.counter_reading.enums.BundleEnum.ON_OFF_LOAD;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.NotificationType.OTHER;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getEshterakMinLength;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

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
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentPossibleBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.custom_dialog.LovelyChoiceDialog;
import com.leon.counter_reading.view_models.PossibleViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PossibleFragment extends DialogFragment implements View.OnClickListener, TextWatcher {
    private FragmentPossibleBinding binding;
    private Callback readingActivity;
    private PossibleViewModel possible;
    private ArrayList<KarbariDto> karbariDtosTemp = new ArrayList<>();

    public static PossibleFragment newInstance(boolean justMobile, int position,
                                               OnOffLoadDto onOffLoadDto) {
        final PossibleFragment fragment = new PossibleFragment();
        final Bundle args = new Bundle();
        args.putString(ON_OFF_LOAD.getValue(), new Gson().toJson(onOffLoadDto));
        args.putBoolean(JUST_MOBILE.getValue(), justMobile);
        args.putInt(POSITION.getValue(), position);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            possible = new PossibleViewModel(getArguments().getBoolean(JUST_MOBILE.getValue()),
                    getArguments().getInt(POSITION.getValue()), new Gson().fromJson(getArguments()
                    .getString(ON_OFF_LOAD.getValue()), OnOffLoadDto.class));
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPossibleBinding.inflate(inflater, container, false);
        binding.setPossible(possible);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        makeRing(requireContext(), OTHER);
        initializeSpinner();
        binding.buttonSubmit.setOnClickListener(this);
        binding.textViewReport.setOnClickListener(this);
        binding.textViewMobile.setOnClickListener(this);
        binding.imageViewMobile.setOnClickListener(this);
        binding.editTextSearch.addTextChangedListener(this);
    }

    private void initializeSpinner() {
        karbariDtosTemp = new ArrayList<>(possible.getKarbari());
        String[] items = new String[karbariDtosTemp.size() + 1];
        for (int i = 0; i < karbariDtosTemp.size(); i++) {
            items[i + 1] = karbariDtosTemp.get(i).title;
        }
        items[0] = getString(R.string.select_one);
        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
        binding.spinnerKarbari.setAdapter(adapter);
        if (possible.getOnOffLoadDto().counterStatePosition != null)
            binding.spinnerKarbari.setSelection(possible.getOnOffLoadDto().counterStatePosition + 1);
    }

    private void counterReport() {
        final String[] itemNames = new String[possible.getCounterReports().size()];
        final boolean[] selection = new boolean[possible.getCounterReports().size()];
        for (int i = 0; i < possible.getCounterReports().size(); i++) {
            boolean found = false;
            int j = 0;
            while (!found && j < possible.getOffLoadReports().size()) {
                if (possible.getOffLoadReports().get(j).reportId == possible.getCounterReports().get(i).id) {
                    found = true;
                }
                j++;
            }
            selection[i] = found;
            itemNames[i] = possible.getCounterReports().get(i).title;
        }
        new LovelyChoiceDialog(requireContext()).setTopColorRes(R.color.green).setTopTitle(R.string.reports)
                .setItemsMultiChoice(itemNames, selection, (positions, items) -> {
                    for (int i = 0; i < possible.getOffLoadReports().size(); i++)
                        getApplicationComponent().MyDatabase().offLoadReportDao()
                                .deleteOffLoadReport(possible.getOffLoadReports().get(i).reportId,
                                        possible.getOnOffLoadDto().trackNumber, possible.getOnOffLoadDto().id);
                    for (int i = 0; i < positions.size(); i++) {
                        OffLoadReport offLoadReport = new OffLoadReport(possible.getOnOffLoadDto().id,
                                possible.getOnOffLoadDto().trackNumber, possible.getCounterReports().get(positions.get(i)).id);
                        getApplicationComponent().MyDatabase().offLoadReportDao()
                                .insertOffLoadReport(offLoadReport);
                    }
                    possible.setCounterReports(new ArrayList<>(getApplicationComponent().MyDatabase()
                            .counterReportDao().getAllCounterReportByZone(possible.getOnOffLoadDto().zoneId)));
                    possible.setOffLoadReports(new ArrayList<>(getApplicationComponent().MyDatabase().offLoadReportDao()
                            .getAllOffLoadReportById(possible.getOnOffLoadDto().id, possible.getOnOffLoadDto().trackNumber)));
                }).setConfirmButtonText(getString(R.string.ok).concat(" ").concat(getString(R.string.reports))).show();

    }

    private void submitForm() {
        if (binding.spinnerKarbari.getSelectedItemPosition() - 1 > 0)
            possible.getOnOffLoadDto().possibleKarbariCode =
                    karbariDtosTemp.get(binding.spinnerKarbari.getSelectedItemPosition() - 1).moshtarakinId;
        if (possible.getPossibleMobile() != null && possible.getPossibleMobile().length() > 0) {
            if (possible.getPossibleMobile().length() < 11 || !possible.getPossibleMobile().substring(0, 2).contains("09")) {
                binding.editTextMobile.setError(getString(R.string.error_format));
                binding.editTextMobile.requestFocus();
                return;
            }
        }
        if (possible.getPossibleCounterSerial() != null && possible.getPossibleCounterSerial().length() > 0) {
            if (possible.getPossibleCounterSerial().length() < 3) {
                binding.editTextSerial.setError(getString(R.string.error_format));
                binding.editTextSerial.requestFocus();
                return;
            }
        }
        if (possible.getPossibleEshterak() != null && possible.getPossibleEshterak().length() > 0) {
            if (possible.getPossibleEshterak().length() < getEshterakMinLength(getActiveCompanyName())) {
                binding.editTextAccount.setError(getString(R.string.error_format));
                binding.editTextAccount.requestFocus();
                return;
            }
        }
        possible.updateOnOffLoadDto();
        dismiss();
        readingActivity.updateOnOffLoadByNavigation(possible.isJustMobile(), possible.getPosition(),
                possible.getOnOffLoadDto());
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.text_view_mobile || id == R.id.image_view_mobile) {
            if (possible.getMobiles() != null) {
                new CustomDialogModel(Green, requireContext(), possible.getMobiles(),
                        getString(R.string.dear_user), getString(R.string.mobile_number),
                        getString(R.string.accepted));
            } else new CustomToast().warning("موردی یافت نشد.");
        } else if (id == R.id.text_view_report) {
            counterReport();
        } else if (id == R.id.button_submit) {
            submitForm();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        final ArrayList<String> itemsTemp = new ArrayList<>();
        itemsTemp.add(getString(R.string.select_one));
        karbariDtosTemp.clear();
        for (int j = 0; j < possible.getKarbari().size(); j++) {
            if (possible.getKarbari().get(j).title.contains(charSequence)) {
                karbariDtosTemp.add(possible.getKarbari().get(j));
                itemsTemp.add(possible.getKarbari().get(j).title);
            }
        }
        final String[] items = itemsTemp.toArray(new String[0]);
        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
        binding.spinnerKarbari.setAdapter(adapter);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    @Override
    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            readingActivity.updateOnOffLoadByAttempt(possible.getPosition(), true);
            new CustomDialogModel(Red, requireContext(), getString(R.string.refresh_page),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted));
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface Callback {
        void updateOnOffLoadByNavigation(boolean justMobile, int position, OnOffLoadDto onOffLoadDto);

        void updateOnOffLoadByAttempt(int position, boolean... booleans);
    }
}