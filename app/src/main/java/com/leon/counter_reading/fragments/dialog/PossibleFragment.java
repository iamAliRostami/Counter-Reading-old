package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.JUST_MOBILE;
import static com.leon.counter_reading.enums.BundleEnum.ON_OFF_LOAD;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.NotificationType.OTHER;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KARBARI;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getDigits;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getEshterakMinLength;
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

public class PossibleFragment extends DialogFragment implements View.OnClickListener {
    private FragmentPossibleBinding binding;
    private Callback readingActivity;
    //    private static boolean justMobile = false;
    private ArrayList<KarbariDto> karbariDtosTemp = new ArrayList<>();
    private PossibleViewModel possible;


    public static PossibleFragment newInstance(boolean justMobile, int position, OnOffLoadDto onOffLoadDto) {
//        PossibleFragment.justMobile = justMobile;
        final PossibleFragment fragment = new PossibleFragment();
        fragment.setArguments(putBundle(justMobile, position, onOffLoadDto));
        fragment.setCancelable(false);
        return fragment;
    }

    static Bundle putBundle(boolean justMobile, int position, OnOffLoadDto onOffLoadDto) {
        final Bundle args = new Bundle();
        final String json = new Gson().toJson(onOffLoadDto);
        args.putString(ON_OFF_LOAD.getValue(), json);
        args.putInt(POSITION.getValue(), position);
        args.putBoolean(JUST_MOBILE.getValue(), justMobile);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    private void getBundle() {
        if (getArguments() != null) {
            possible = new PossibleViewModel(getArguments().getBoolean(JUST_MOBILE.getValue()),
                    getArguments().getInt(POSITION.getValue()), new Gson().fromJson(getArguments()
                    .getString(ON_OFF_LOAD.getValue()), OnOffLoadDto.class));
            getArguments().clear();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPossibleBinding.inflate(inflater, container, false);
        binding.setPossible(possible);
        //TODO
//        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(requireContext(), OTHER);
        onEditTextSearchChangeListener();
        initializeSpinner();
        binding.buttonSubmit.setOnClickListener(this);
        binding.textViewReport.setOnClickListener(this);
        binding.textViewMobile.setOnClickListener(this);
        binding.imageViewMobile.setOnClickListener(this);
    }

    private void onEditTextSearchChangeListener() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final ArrayList<String> itemsTemp = new ArrayList<>();
                itemsTemp.add(getString(R.string.select_one));
                karbariDtosTemp.clear();
                for (int j = 0; j < possible.getKarbariDtos().size(); j++) {
                    if (possible.getKarbariDtos().get(j).title.contains(charSequence)) {
                        karbariDtosTemp.add(possible.getKarbariDtos().get(j));
                        itemsTemp.add(possible.getKarbariDtos().get(j).title);
                    }
                }
                final String[] items = itemsTemp.toArray(new String[0]);
                final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
                binding.spinnerKarbari.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
                        getApplicationComponent().MyDatabase().offLoadReportDao().
                                deleteOffLoadReport(possible.getOffLoadReports().get(i).reportId,
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
        boolean cancel = false;
        View view = null;
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(KARBARI.getValue())) {
            int position = binding.spinnerKarbari.getSelectedItemPosition() - 1;
            if (position >= 0)
                possible.getOnOffLoadDto().possibleKarbariCode = karbariDtosTemp.get(position).moshtarakinId;
        }
        if (binding.editTextMobile.getText().length() > 0) {
            if (binding.editTextMobile.getText().length() < 11 ||
                    !binding.editTextMobile.getText().toString().substring(0, 2).contains("09")) {
                binding.editTextMobile.setError(getString(R.string.error_format));
                view = binding.editTextMobile;
                cancel = true;
            } else
                possible.getOnOffLoadDto().possibleMobile = binding.editTextMobile.getText().toString();
        }
        if (binding.editTextSerial.getText().length() > 0) {
            if (binding.editTextSerial.getText().toString().length() < 3) {
                binding.editTextSerial.setError(getString(R.string.error_format));
                view = binding.editTextSerial;
                cancel = true;
            } else
                possible.getOnOffLoadDto().possibleCounterSerial = binding.editTextSerial.getText().toString();
        }
        if (binding.editTextAccount.getText().length() > 0) {
            if (binding.editTextAccount.getText().toString().length() <
                    getEshterakMinLength(getActiveCompanyName())) {
                binding.editTextAccount.setError(getString(R.string.error_format));
                view = binding.editTextAccount;
                cancel = true;
            } else
                possible.getOnOffLoadDto().possibleEshterak = binding.editTextAccount.getText().toString();
        }
        if (binding.editTextDescription.getText().length() > 0) {
            possible.getOnOffLoadDto().description = binding.editTextDescription.getText().toString();
        }
        if (binding.editTextAddress.getText().length() > 0)
            possible.getOnOffLoadDto().possibleAddress = binding.editTextAddress.getText().toString();

        if (binding.editTextAhadTotal.getText().length() > 0)
            possible.getOnOffLoadDto().possibleAhadSaierOrAbBaha = getDigits(binding.editTextAhadTotal.getText().toString());

        if (binding.editTextAhad2.getText().length() > 0)
            possible.getOnOffLoadDto().possibleAhadTejariOrFari = getDigits(binding.editTextAhad2.getText().toString());

        if (binding.editTextAhad1.getText().length() > 0)
            possible.getOnOffLoadDto().possibleAhadMaskooniOrAsli = getDigits(binding.editTextAhad1.getText().toString());

        if (binding.editTextAhadEmpty.getText().length() > 0)
            possible.getOnOffLoadDto().possibleEmpty = getDigits(binding.editTextAhadEmpty.getText().toString());

        if (cancel)
            view.requestFocus();
        else {
            dismiss();
            readingActivity.updateOnOffLoadByNavigation(possible.isJustMobile()/*justMobile*/, possible.getPosition(),
                    possible.getOnOffLoadDto());
        }
    }

    private void initializeSpinner() {
        karbariDtosTemp = new ArrayList<>(possible.getKarbariDtos());
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

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.text_view_mobile || id == R.id.image_view_mobile) {
            if (possible.getMobiles() != null) {
//                final String[] mobiles = possible.getOnOffLoadDto().mobiles.split(",");
//                String mobile = "";
//                for (String mobileTemp : mobiles)
//                    mobile = mobile.concat(mobileTemp.trim().concat("\n"));
                new CustomDialogModel(Green, requireContext(), possible.getMobiles(),
                        getString(R.string.dear_user), getString(R.string.mobile_number),
                        getString(R.string.accepted));
            } else new CustomToast().warning("موردی یافت نشد.");
        } else if (id == R.id.text_view_report) {
            counterReport();
        } else if (id == R.id.button_submit) {
            submitForm();
        } else if (id == R.id.button_close) {
            dismiss();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface Callback {
        void updateOnOffLoadByNavigation(boolean justMobile, int position, OnOffLoadDto onOffLoadDto);

        void updateOnOffLoadByAttempt(int position, boolean... booleans);
    }
}