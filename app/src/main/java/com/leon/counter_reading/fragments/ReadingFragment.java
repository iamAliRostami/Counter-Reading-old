package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.enums.NotificationType.NOT_SAVE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowFragmentDialogOnce;
import static com.leon.counter_reading.helpers.Constants.FOCUS_ON_EDIT_TEXT;
import static com.leon.counter_reading.helpers.Constants.LOCATION_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.STORAGE_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.counterStateDtos;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad1;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad2;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhadTotal;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getLockNumber;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.PermissionManager.checkLocationPermission;
import static com.leon.counter_reading.utils.PermissionManager.checkStoragePermission;
import static com.leon.counter_reading.utils.PermissionManager.enableGps;
import static com.leon.counter_reading.utils.PermissionManager.forceClose;
import static com.leon.counter_reading.utils.reading.Counting.checkHighLow;
import static com.leon.counter_reading.utils.reading.Counting.checkHighLowMakoos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReadingBinding;
import com.leon.counter_reading.fragments.dialog.AreYouSureFragment;
import com.leon.counter_reading.fragments.dialog.PossibleFragment;
import com.leon.counter_reading.helpers.Constants;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingFragment extends Fragment {
    private FragmentReadingBinding binding;
    private Activity activity;
    private KarbariDto karbariDto;
    private OnOffLoadDto onOffLoadDto;
    private ReadingConfigDefaultDto readingConfigDefaultDto;
    private int position, counterStateCode, counterStatePosition;
    private boolean isMakoos, isMane, canLessThanPre, canEnterNumber, shouldEnterNumber;

    public ReadingFragment() {
    }

    public ReadingFragment(int position) {
        this.position = position;
        this.onOffLoadDto = Constants.onOffLoadDtos.get(position);
        this.readingConfigDefaultDto = Constants.readingConfigDefaultDtos.get(position);
        this.karbariDto = Constants.karbariDtos.get(position);
    }

    public static ReadingFragment newInstance(int position) {
        return new ReadingFragment(position);
    }

    private static Bundle putBundle(int position) {
        final Bundle args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        return args;
    }

    private void getBundle(final Bundle bundle) {
        try {
            position = bundle.getInt(POSITION.getValue());
            this.onOffLoadDto = Constants.onOffLoadDtos.get(position);
            this.readingConfigDefaultDto = Constants.readingConfigDefaultDtos.get(position);
            this.karbariDto = Constants.karbariDtos.get(position);
        } catch (Exception e) {
            final Intent intent = requireActivity().getIntent();
            requireActivity().finish();
            startActivity(intent);
        }
        bundle.clear();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.clear();
            outState.putAll(putBundle(position));
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            getBundle(savedInstanceState);
            savedInstanceState.clear();
        } else if (getArguments() != null) {
            getBundle(getArguments());
            getArguments().clear();
        }
        activity = getActivity();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.clear();
        }
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()))
            binding.relativeLayoutReading.setRotationY(180);
        initialize();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) savedInstanceState.clear();
        binding = FragmentReadingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void initialize() {
        binding.editTextNumber.setOnLongClickListener(onLongClickListener);
        if (onOffLoadDto.counterNumber != null)
            binding.editTextNumber.setText(String.valueOf(onOffLoadDto.counterNumber));
        initializeViews();
        initializeSpinner();
//        initializeEditText();
        setOnButtonSubmitClickListener();
        setOnKeyboardButtonsClickListener();
    }

    private void initializeEditText() {
        if (onOffLoadDto.isLocked)
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
        else if (FOCUS_ON_EDIT_TEXT && (shouldEnterNumber || canEnterNumber))
            binding.relativeLayoutKeyboard.setVisibility(View.VISIBLE);
        else
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
    }

    private void initializeViews() {
        binding.textViewAhad1Title.setText(getAhad1(getActiveCompanyName()).concat(" : "));
        binding.textViewAhad2Title.setText(getAhad2(getActiveCompanyName()).concat(" : "));
        binding.textViewAhadTotalTitle.setText(getAhadTotal(getActiveCompanyName()).concat(" : "));
        binding.textViewAddress.setText(onOffLoadDto.address);
        binding.textViewName.setText(onOffLoadDto.firstName.concat(" ").concat(onOffLoadDto.sureName));
        binding.textViewPreDate.setText(onOffLoadDto.preDate);
        binding.textViewSerial.setText(onOffLoadDto.counterSerial);

        if (onOffLoadDto.displayRadif)
            binding.textViewRadif.setText(String.valueOf(onOffLoadDto.radif));
        else if (onOffLoadDto.displayBillId)
            binding.textViewRadif.setText(String.valueOf(onOffLoadDto.billId));
        else binding.textViewRadif.setVisibility(View.GONE);

        binding.textViewAhad1.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));
        binding.textViewAhad2.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));
        binding.textViewAhadTotal.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        if (readingConfigDefaultDto.isOnQeraatCode)
            binding.textViewCode.setText(onOffLoadDto.qeraatCode);
        else binding.textViewCode.setText(onOffLoadDto.eshterak);
        if (karbariDto.title == null)
            new CustomToast().warning("کاربری اشتراک ".concat(onOffLoadDto.eshterak).concat(" به درستی بارگیری نشده است."));
        else binding.textViewKarbari.setText(karbariDto.title);
        if (onOffLoadDto.qotr == null)
            new CustomToast().warning("قطر اشتراک ".concat(onOffLoadDto.eshterak).concat(" به درستی بارگیری نشده است."));
        else
            binding.textViewBranch.setText(onOffLoadDto.qotr.equals("مشخص نشده") ? "-" : onOffLoadDto.qotr);
        if (onOffLoadDto.sifoonQotr == null)
            new CustomToast().warning("قطر سیفون اشتراک ".concat(onOffLoadDto.eshterak).concat(" به درستی بارگیری نشده است."));
        else
            binding.textViewSiphon.setText(onOffLoadDto.sifoonQotr.equals("مشخص نشده") ? "-" : onOffLoadDto.sifoonQotr);

        if (onOffLoadDto.counterNumberShown)
            binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber));

        binding.textViewPreNumber.setOnClickListener(onClickListener);
        binding.textViewAddress.setOnLongClickListener(onLongClickListener);
    }

    private void initializeSpinner() {
        final String[] items = new String[counterStateDtos.size()];
        for (int i = 0; i < counterStateDtos.size(); i++) {
            items[i] = counterStateDtos.get(i).title;
        }
        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(adapter);
        boolean found = false;
        int i;
        for (i = 0; i < counterStateDtos.size() && !found; i++)
            if (counterStateDtos.get(i).id == onOffLoadDto.counterStateId) {
                found = true;
            }
        binding.spinner.setSelection(found ? i - 1 : 0);
        setOnSpinnerSelectedListener();
    }

    private void setOnSpinnerSelectedListener() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                counterStatePosition = i;
                final CounterStateDto counterStateDto = counterStateDtos.get(counterStatePosition);
                counterStateCode = counterStateDto.id;
                isMane = counterStateDto.isMane;
                shouldEnterNumber = counterStateDto.shouldEnterNumber;
                canEnterNumber = counterStateDto.canEnterNumber;
                canLessThanPre = counterStateDto.canNumberBeLessThanPre;
                isMakoos = counterStateDto.title.equals("معکوس");

                binding.imageButtonShowKeyboard.setVisibility(canEnterNumber || shouldEnterNumber ?
                        View.VISIBLE : View.GONE);
                if (!canEnterNumber && !shouldEnterNumber) binding.editTextNumber.setText("");
                initializeEditText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setOnButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(onClickListener);
    }

    private void setOnKeyboardButtonsClickListener() {
        binding.buttonKeyboard0.setOnClickListener(onClickListener);
        binding.buttonKeyboard1.setOnClickListener(onClickListener);
        binding.buttonKeyboard2.setOnClickListener(onClickListener);
        binding.buttonKeyboard3.setOnClickListener(onClickListener);
        binding.buttonKeyboard4.setOnClickListener(onClickListener);
        binding.buttonKeyboard5.setOnClickListener(onClickListener);
        binding.buttonKeyboard6.setOnClickListener(onClickListener);
        binding.buttonKeyboard7.setOnClickListener(onClickListener);
        binding.buttonKeyboard8.setOnClickListener(onClickListener);
        binding.buttonKeyboard9.setOnClickListener(onClickListener);
        binding.buttonKeyboardBackspace.setOnClickListener(onClickListener);
        binding.imageButtonHideKeyboard.setOnClickListener(onClickListener);
        binding.imageButtonShowKeyboard.setOnClickListener(onClickListener);
    }

    private void askLocationPermission() {
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                new CustomToast().warning("به علت عدم دسترسی به مکان یابی، امکان ثبت وجود ندارد.");
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(LOCATION_PERMISSIONS).check();
    }

    private void askStoragePermission() {
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(STORAGE_PERMISSIONS).check();
    }

    private void checkPermissions() {
        if (enableGps(activity))
            if (!checkLocationPermission(getContext())) {
                askLocationPermission();
            } else if (!checkStoragePermission(getContext())) {
                askStoragePermission();
            } else {
                attemptSend();
            }
    }

    private boolean lockProcess(boolean canBeEmpty) {
        onOffLoadDto.attemptCount++;
        if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount + 1 == getLockNumber(getActiveCompanyName()))
            new CustomToast().error(getString(R.string.mistakes_error), Toast.LENGTH_LONG);
        if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount == getLockNumber(getActiveCompanyName()))
            new CustomToast().error(getString(R.string.by_mistakes).
                    concat(onOffLoadDto.eshterak).concat(getString(R.string.is_locked)), Toast.LENGTH_SHORT);
        ((ReadingActivity) activity).updateOnOffLoadByAttempt(position);
        if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount >= getLockNumber(getActiveCompanyName())) {
            ((ReadingActivity) activity).updateOnOffLoadByLock(position);
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
            return canBeEmpty;
        }
        return true;
    }

    private void attemptSend() {
        if (!shouldEnterNumber && lockProcess(true)) {
            canBeEmpty();
        } else {
            canNotBeEmpty();
        }
    }

    private void canBeEmpty() {
        if (binding.editTextNumber.getText().toString().isEmpty() || isMane) {
            ((ReadingActivity) activity).updateOnOffLoadWithoutCounterNumber(position,
                    counterStateCode, counterStatePosition);
        } else {
            final int currentNumber = getDigits(binding.editTextNumber.getText().toString());
            final int use = currentNumber - onOffLoadDto.preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(activity, NOT_SAVE);
                binding.editTextNumber.setError(getString(R.string.less_than_pre));
                binding.editTextNumber.requestFocus();
            }
        }
    }

    private void canNotBeEmpty() {
        if (binding.editTextNumber.getText().toString().isEmpty()) {
            makeRing(activity, NOT_SAVE);
            binding.editTextNumber.setError(getString(R.string.counter_empty));
            binding.editTextNumber.requestFocus();
        } else if (lockProcess(!shouldEnterNumber)) {
            final int currentNumber = getDigits(binding.editTextNumber.getText().toString());
            final int use = currentNumber - onOffLoadDto.preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(activity, NOT_SAVE);
                binding.editTextNumber.setError(getString(R.string.less_than_pre));
                binding.editTextNumber.requestFocus();
            } else {
                notEmpty(currentNumber);
            }
        }
    }

    private int getDigits(String number) {
        if (!TextUtils.isEmpty(number) && TextUtils.isDigitsOnly(number)) {
            return Integer.parseInt(number);
        } else {
            return 0;
        }
    }

    private void lessThanPre(int currentNumber) {
        if (!isMakoos)
            ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position, currentNumber,
                    counterStateCode, counterStatePosition);
        else {
            notEmptyIsMakoos(currentNumber);
        }
    }

    private void notEmptyIsMakoos(int currentNumber) {
        Integer type = null;
        if (currentNumber == onOffLoadDto.preNumber) {
            type = ZERO.getValue();
        } else {
            int status = checkHighLowMakoos(onOffLoadDto, karbariDto, readingConfigDefaultDto,
                    currentNumber);
            switch (status) {
                case 1:
                    type = HIGH.getValue();
                    break;
                case -1:
                    type = LOW.getValue();
                    break;
                case 0:
                    ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position,
                            currentNumber, counterStateCode, counterStatePosition, NORMAL.getValue());
                    break;
            }
        }
        if (type != null) {
            ShowFragmentDialogOnce(activity, "ARE_YOU_SURE_DIALOG",
                    AreYouSureFragment.newInstance(position, currentNumber, type, counterStateCode,
                            counterStatePosition));
        }
    }

    private void notEmpty(int currentNumber) {
        Integer type = null;
        if (currentNumber == onOffLoadDto.preNumber) {
            type = ZERO.getValue();
        } else {
            final int status = checkHighLow(onOffLoadDto, karbariDto, readingConfigDefaultDto, currentNumber);
            switch (status) {
                case 1:
                    type = HIGH.getValue();
                    break;
                case -1:
                    type = LOW.getValue();
                    break;
                case 0:
                    ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position,
                            currentNumber, counterStateCode, counterStatePosition, NORMAL.getValue());
                    break;
            }
        }
        if (type != null) {
            ShowFragmentDialogOnce(activity, "ARE_YOU_SURE_DIALOG", AreYouSureFragment
                    .newInstance(position, currentNumber, type, counterStateCode, counterStatePosition));
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener onClickListener = view -> {
        final int id = view.getId();
        switch (id) {
            case R.id.image_button_show_keyboard:
            case R.id.image_button_hide_keyboard:
                FOCUS_ON_EDIT_TEXT = !FOCUS_ON_EDIT_TEXT;
                initializeEditText();
                break;
            case R.id.button_keyboard_backspace:
                if (binding.editTextNumber.getText().toString().length() > 0)
                    binding.editTextNumber.setText(binding.editTextNumber.getText().toString()
                            .substring(0, binding.editTextNumber.getText().toString().length() - 1));
                break;
            case R.id.text_view_pre_number:
                if (onOffLoadDto.hasPreNumber) {
                    activity.runOnUiThread(() ->
                            binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber)));
                    ((ReadingActivity) activity).updateOnOffLoadByPreNumber(position);
                } else {
                    new CustomToast().warning(getString(R.string.can_not_show_pre));
                }
                break;
            case R.id.button_keyboard_0:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("0"));
                break;
            case R.id.button_keyboard_1:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("1"));
                break;
            case R.id.button_keyboard_2:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("2"));
                break;
            case R.id.button_keyboard_3:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("3"));
                break;
            case R.id.button_keyboard_4:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("4"));
                break;
            case R.id.button_keyboard_5:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("5"));
                break;
            case R.id.button_keyboard_6:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("6"));
                break;
            case R.id.button_keyboard_7:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("7"));
                break;
            case R.id.button_keyboard_8:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("8"));
                break;
            case R.id.button_keyboard_9:
                binding.editTextNumber.setText(binding.editTextNumber.getText().toString().concat("9"));
                break;
            case R.id.button_submit:
                checkPermissions();
                break;

        }
    };

    @SuppressLint("NonConstantResourceId")
    private final View.OnLongClickListener onLongClickListener = view -> {
        final int id = view.getId();
        switch (id) {
            case R.id.edit_text_address:
                ShowFragmentDialogOnce(activity, "SHOW_POSSIBLE_DIALOG", PossibleFragment
                        .newInstance(onOffLoadDto, position, true));
                break;
            case R.id.edit_text_number:
                binding.editTextNumber.setText("");
                break;
        }
        return false;
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        karbariDto = null;
        readingConfigDefaultDto = null;
        binding = null;
    }
}