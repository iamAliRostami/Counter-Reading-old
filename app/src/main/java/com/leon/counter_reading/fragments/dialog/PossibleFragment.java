package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.ON_OFF_LOAD;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.NotificationType.OTHER;
import static com.leon.counter_reading.enums.SharedReferenceKeys.ACCOUNT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.ADDRESS;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_1;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_2;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_EMPTY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_TOTAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.DESCRIPTION;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KARBARI;
import static com.leon.counter_reading.enums.SharedReferenceKeys.READING_REPORT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SERIAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SHOW_AHAD_TITLE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getDigits;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad1;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad2;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhadTotal;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getEshterakMaxLength;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getEshterakMinLength;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.custom_dialog.LovelyChoiceDialog;
import com.leon.counter_reading.view_models.PossibleViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PossibleFragment extends DialogFragment implements View.OnClickListener {
    private static boolean justMobile = false;
    private FragmentPossibleBinding binding;
    private Callback readingActivity;
    private OnOffLoadDto onOffLoadDto;
    private int position;
    private ISharedPreferenceManager sharedPreferenceManager;
    private ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    private ArrayList<KarbariDto> karbariDtosTemp = new ArrayList<>();
    private ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>();
    private ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();

    private PossibleViewModel possible;

    public static PossibleFragment newInstance(OnOffLoadDto onOffLoadDto, int position, boolean justMobile) {
        PossibleFragment.justMobile = justMobile;
        final PossibleFragment fragment = new PossibleFragment();
        fragment.setArguments(putBundle(onOffLoadDto, position));
        fragment.setCancelable(false);
        return fragment;
    }

    static Bundle putBundle(OnOffLoadDto onOffLoadDto, int position) {
        final Bundle args = new Bundle();
        final String json = new Gson().toJson(onOffLoadDto);
        args.putString(ON_OFF_LOAD.getValue(), json);
        args.putInt(POSITION.getValue(), position);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    private void getBundle() {
        if (getArguments() != null) {
            onOffLoadDto = new Gson().fromJson(getArguments().getString(ON_OFF_LOAD.getValue()),
                    OnOffLoadDto.class);
            position = getArguments().getInt(POSITION.getValue());
            getArguments().clear();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        //TODO
//        initialize();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPossibleBinding.inflate(inflater, container, false);
        possible = new PossibleViewModel(justMobile);
        binding.setPossible(possible);
        //TODO
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(requireContext(), OTHER);
        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        if (justMobile) {
//TODO
//            binding.linearLayoutOldEshterak.setVisibility(View.VISIBLE);
//            binding.linearLayoutOldRadif.setVisibility(View.VISIBLE);
//            binding.linearLayoutFatherName.setVisibility(View.VISIBLE);
//            binding.linearLayoutDebt.setVisibility(View.VISIBLE);
//            binding.linearLayoutMobile.setVisibility(View.VISIBLE);
//            binding.editTextMobile.setVisibility(View.VISIBLE);
//            binding.textViewMobile.setVisibility(View.VISIBLE);
//            binding.editTextSerial.setVisibility(View.GONE);
//            binding.editTextAddress.setVisibility(View.GONE);
//            binding.editTextAccount.setVisibility(View.GONE);
//            binding.editTextAhadEmpty.setVisibility(View.GONE);
//            binding.editTextDescription.setVisibility(View.GONE);
//            binding.linearLayoutAhad.setVisibility(View.GONE);
//            binding.editTextAhad1.setVisibility(View.GONE);
//            binding.editTextAhad2.setVisibility(View.GONE);
//            binding.editTextAhadTotal.setVisibility(View.GONE);
//            binding.textViewReport.setVisibility(View.GONE);
//            binding.linearLayoutKarbari.setVisibility(View.GONE);
//            binding.editTextSearch.setVisibility(View.GONE);
//TODO


            binding.textViewDebt.setText(String.valueOf(onOffLoadDto.balance));
            binding.textViewOldRadif.setText(onOffLoadDto.oldRadif != null ? onOffLoadDto.oldRadif : "-");
            binding.textViewOldEshterak.setText(onOffLoadDto.oldEshterak != null ? onOffLoadDto.oldEshterak : "-");
            binding.textViewFatherName.setText(onOffLoadDto.fatherName != null ? onOffLoadDto.fatherName : "-");
            binding.editTextMobile.setText(onOffLoadDto.possibleMobile);
            binding.textViewMobile.setText(onOffLoadDto.mobile != null ? onOffLoadDto.mobile : "-");

            if (onOffLoadDto.mobiles != null) {
                final String[] mobiles = onOffLoadDto.mobiles.split(",");
                String mobile = "";
                for (String mobileTemp : mobiles) {
                    mobile = mobile.concat(mobileTemp.trim().concat("\n"));
                }
                binding.textViewMobiles.setText(mobile.substring(0, mobile.length() - 1));
            }

        } else
            initializeTextViews();
        binding.buttonSubmit.setOnClickListener(this);
        binding.textViewMobile.setOnClickListener(this);
        binding.imageViewMobile.setOnClickListener(this);
        setOnEditTextSearchChangeListener();
    }

    private void setOnEditTextSearchChangeListener() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final ArrayList<String> itemsTemp = new ArrayList<>();
                itemsTemp.add(getString(R.string.select_one));
                karbariDtosTemp.clear();
                for (int j = 0; j < karbariDtos.size(); j++) {
                    if (karbariDtos.get(j).title.contains(charSequence)) {
                        karbariDtosTemp.add(karbariDtos.get(j));
                        itemsTemp.add(karbariDtos.get(j).title);
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

    private void initializeTextViews() {
//TODO
//        binding.editTextAccount.setFilters(new InputFilter[]{
//                new InputFilter.LengthFilter(getEshterakMaxLength(getActiveCompanyName()))});
//        binding.textViewAhad1Title.setText(getAhad1(getActiveCompanyName()).concat(":"));
//        binding.textViewAhad2Title.setText(getAhad2(getActiveCompanyName()).replaceFirst("آحاد ", "").concat(":"));
//        binding.textViewAhadTotalTitle.setText(getAhadTotal(getActiveCompanyName()).replaceFirst("آحاد ", "").concat(":"));
//        binding.editTextAhadEmpty.setHint(getAhad(getActiveCompanyName()).concat(getString(R.string.empty)));
//        binding.editTextAhad1.setHint(getAhad1(getActiveCompanyName()));
//        binding.editTextAhad2.setHint(getAhad2(getActiveCompanyName()));
//        binding.editTextAhadTotal.setHint(getAhadTotal(getActiveCompanyName()));
//TODO

        if (onOffLoadDto.possibleMobile != null)
            binding.editTextMobile.setText(onOffLoadDto.possibleMobile);
        if (onOffLoadDto.possibleAddress != null)
            binding.editTextAddress.setText(onOffLoadDto.possibleAddress);
        if (onOffLoadDto.possibleEshterak != null)
            binding.editTextAccount.setText(onOffLoadDto.possibleEshterak);
        if (onOffLoadDto.possibleCounterSerial != null)
            binding.editTextSerial.setText(onOffLoadDto.possibleCounterSerial);
        if (onOffLoadDto.possibleEmpty != null)
            binding.editTextAhadEmpty.setText(String.valueOf(onOffLoadDto.possibleEmpty));
        if (onOffLoadDto.possibleAhadMaskooniOrAsli != null)
            binding.editTextAhad1.setText(String.valueOf(onOffLoadDto.possibleAhadMaskooniOrAsli));
        if (onOffLoadDto.possibleAhadTejariOrFari != null)
            binding.editTextAhad2.setText(String.valueOf(onOffLoadDto.possibleAhadTejariOrFari));
        if (onOffLoadDto.possibleAhadSaierOrAbBaha != null)
            binding.editTextAhadTotal.setText(String.valueOf(onOffLoadDto.possibleAhadSaierOrAbBaha));

        if (onOffLoadDto.description != null)
            binding.editTextDescription.setText(onOffLoadDto.description);
//TODO
//        binding.editTextSerial.setVisibility(sharedPreferenceManager.getBoolData(SERIAL.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.editTextAddress.setVisibility(sharedPreferenceManager.getBoolData(ADDRESS.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.editTextAccount.setVisibility(sharedPreferenceManager.getBoolData(ACCOUNT.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.editTextAhadEmpty.setVisibility(sharedPreferenceManager.getBoolData(AHAD_EMPTY.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.editTextDescription.setVisibility(sharedPreferenceManager.getBoolData(DESCRIPTION.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.linearLayoutAhad.setVisibility(sharedPreferenceManager.getBoolData(SHOW_AHAD_TITLE.getValue()) ?
//                View.VISIBLE : View.GONE);
//TODO
        binding.textViewAhad1.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));
        binding.textViewAhad2.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));
        binding.textViewAhadTotal.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        binding.editTextAhad1.setVisibility(sharedPreferenceManager.getBoolData(AHAD_1.getValue()) ?
                View.VISIBLE : View.GONE);
        binding.editTextAhad2.setVisibility(sharedPreferenceManager.getBoolData(AHAD_2.getValue()) ?
                View.VISIBLE : View.GONE);
        binding.editTextAhadTotal.setVisibility(sharedPreferenceManager.getBoolData(AHAD_TOTAL.getValue()) ?
                View.VISIBLE : View.GONE);
//TODO
//        binding.linearLayoutMobile.setVisibility(sharedPreferenceManager.getBoolData(MOBILE.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.linearLayoutMobileInput.setVisibility(sharedPreferenceManager.getBoolData(MOBILE.getValue()) ?
//                View.VISIBLE : View.GONE);
//        binding.textViewMobile.setVisibility(sharedPreferenceManager.getBoolData(MOBILE.getValue()) ?
//                View.VISIBLE : View.GONE);
//TODO

        binding.textViewMobile.setText(onOffLoadDto.mobile != null ? onOffLoadDto.mobile : "-");
        if (sharedPreferenceManager.getBoolData(READING_REPORT.getValue())) {
            counterReportDtos = new ArrayList<>(getApplicationComponent().MyDatabase()
                    .counterReportDao().getAllCounterReportByZone(onOffLoadDto.zoneId));
            offLoadReports = new ArrayList<>(getApplicationComponent().MyDatabase().offLoadReportDao()
                    .getAllOffLoadReportById(onOffLoadDto.id, onOffLoadDto.trackNumber));
            binding.textViewReport.setOnClickListener(this);
        } else {
            binding.textViewReport.setVisibility(View.GONE);
        }

        initializeSpinner();
    }

    private void counterReport() {
        final String[] itemNames = new String[counterReportDtos.size()];
        final boolean[] selection = new boolean[counterReportDtos.size()];
        for (int i = 0; i < counterReportDtos.size(); i++) {
            boolean found = false;
            int j = 0;
            while (!found && j < offLoadReports.size()) {
                if (offLoadReports.get(j).reportId == counterReportDtos.get(i).id) {
                    found = true;
                }
                j++;
            }
            selection[i] = found;
            itemNames[i] = counterReportDtos.get(i).title;
        }
        new LovelyChoiceDialog(requireContext())
                .setTopColorRes(R.color.green)
                .setTopTitle(R.string.reports)
                .setItemsMultiChoice(itemNames, selection, (positions, items) -> {
                    for (int i = 0; i < offLoadReports.size(); i++)
                        getApplicationComponent().MyDatabase().offLoadReportDao().
                                deleteOffLoadReport(offLoadReports.get(i).reportId,
                                        onOffLoadDto.trackNumber, onOffLoadDto.id);

                    for (int i = 0; i < positions.size(); i++) {
                        OffLoadReport offLoadReport = new OffLoadReport(onOffLoadDto.id,
                                onOffLoadDto.trackNumber, counterReportDtos.get(positions.get(i)).id);
                        getApplicationComponent().MyDatabase().offLoadReportDao()
                                .insertOffLoadReport(offLoadReport);
                    }
                    counterReportDtos = new ArrayList<>(getApplicationComponent().MyDatabase()
                            .counterReportDao().getAllCounterReportByZone(onOffLoadDto.zoneId));
                    offLoadReports = new ArrayList<>(getApplicationComponent().MyDatabase().offLoadReportDao()
                            .getAllOffLoadReportById(onOffLoadDto.id, onOffLoadDto.trackNumber));
                }).setConfirmButtonText(getString(R.string.ok).concat(" ").concat(getString(R.string.reports))).show();

    }

    private void submitForm() {

        boolean cancel = false;
        View view = null;
        if (sharedPreferenceManager.getBoolData(KARBARI.getValue())) {
            int position = binding.spinnerKarbari.getSelectedItemPosition() - 1;
            if (position >= 0)
                onOffLoadDto.possibleKarbariCode = karbariDtosTemp.get(position).moshtarakinId;
        }
        if (binding.editTextMobile.getText().length() > 0) {
            if (binding.editTextMobile.getText().length() < 11 ||
                    !binding.editTextMobile.getText().toString().substring(0, 2).contains("09")) {
                binding.editTextMobile.setError(getString(R.string.error_format));
                view = binding.editTextMobile;
                cancel = true;
            } else
                onOffLoadDto.possibleMobile = binding.editTextMobile.getText().toString();
        }
        if (binding.editTextSerial.getText().length() > 0) {
            if (binding.editTextSerial.getText().toString().length() < 3) {
                binding.editTextSerial.setError(getString(R.string.error_format));
                view = binding.editTextSerial;
                cancel = true;
            } else
                onOffLoadDto.possibleCounterSerial = binding.editTextSerial.getText().toString();
        }
        if (binding.editTextAccount.getText().length() > 0) {
            if (binding.editTextAccount.getText().toString().length() <
                    getEshterakMinLength(getActiveCompanyName())) {
                binding.editTextAccount.setError(getString(R.string.error_format));
                view = binding.editTextAccount;
                cancel = true;
            } else onOffLoadDto.possibleEshterak = binding.editTextAccount.getText().toString();
        }
        if (binding.editTextDescription.getText().length() > 0) {
            onOffLoadDto.description = binding.editTextDescription.getText().toString();
        }
        if (binding.editTextAddress.getText().length() > 0)
            onOffLoadDto.possibleAddress = binding.editTextAddress.getText().toString();

        if (binding.editTextAhadTotal.getText().length() > 0)
            onOffLoadDto.possibleAhadSaierOrAbBaha = getDigits(binding.editTextAhadTotal.getText().toString());

        if (binding.editTextAhad2.getText().length() > 0)
            onOffLoadDto.possibleAhadTejariOrFari = getDigits(binding.editTextAhad2.getText().toString());

        if (binding.editTextAhad1.getText().length() > 0)
            onOffLoadDto.possibleAhadMaskooniOrAsli = getDigits(binding.editTextAhad1.getText().toString());

        if (binding.editTextAhadEmpty.getText().length() > 0)
            onOffLoadDto.possibleEmpty = getDigits(binding.editTextAhadEmpty.getText().toString());

        if (cancel)
            view.requestFocus();
        else {
            dismiss();
            readingActivity.updateOnOffLoadByNavigation(justMobile, position, onOffLoadDto);
        }
    }

    private void initializeSpinner() {
        if (sharedPreferenceManager.getBoolData(KARBARI.getValue())) {
            karbariDtos = new ArrayList<>(getApplicationComponent().MyDatabase().karbariDao()
                    .getAllKarbariDto());
            karbariDtosTemp = new ArrayList<>(karbariDtos);
            String[] items = new String[karbariDtosTemp.size() + 1];
            for (int i = 0; i < karbariDtosTemp.size(); i++) {
                items[i + 1] = karbariDtosTemp.get(i).title;
            }
            items[0] = getString(R.string.select_one);
            final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
            binding.spinnerKarbari.setAdapter(adapter);
            binding.spinnerKarbari.setSelection(onOffLoadDto.counterStatePosition + 1);
        } else {
            binding.linearLayoutKarbari.setVisibility(View.GONE);
            binding.editTextSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.text_view_mobile || id == R.id.image_view_mobile) {
            if (onOffLoadDto.mobiles != null) {
                final String[] mobiles = onOffLoadDto.mobiles.split(",");
                String mobile = "";
                for (String mobileTemp : mobiles)
                    mobile = mobile.concat(mobileTemp.trim().concat("\n"));
                new CustomDialogModel(Green, requireContext(), mobile, getString(R.string.dear_user),
                        getString(R.string.mobile_number), getString(R.string.accepted));
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
            readingActivity.updateOnOffLoadByAttempt(position, true);
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