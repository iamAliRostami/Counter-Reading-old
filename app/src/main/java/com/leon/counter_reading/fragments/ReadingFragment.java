package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.FragmentTags.POSSIBLE_DIALOG;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.enums.NotificationType.NOT_SAVE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KEYBOARD_TYPE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
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
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private KarbariDto karbariDto;
    private OnOffLoadDto onOffLoadDto;
    private ReadingConfigDefaultDto readingConfigDefaultDto;
    private long lastClickTime = 0;
    private int position, counterStateCode, counterStatePosition, textViewId, buttonId;
    private boolean isMakoos, isMane, canLessThanPre, canEnterNumber, shouldEnterNumber, debtOrNumber;
    private TextView textView;

    public ReadingFragment() {
    }

    private ReadingFragment(int position) {
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
        initializeTextViewNumber();
        initializeViews();
        initializeSpinner();
        initializeButtonSubmit();
        setOnKeyboardButtonsClickListener();
    }

    private void initializeTextViewNumber() {
        binding.editTextNumber.setId(View.generateViewId());
        textView = binding.editTextNumber;
        textView.setOnLongClickListener(onLongClickListener);
        textView.setOnClickListener(onClickListener);
        textViewId = textView.getId();
        if (onOffLoadDto.counterNumber != null)
            textView.setText(String.valueOf(onOffLoadDto.counterNumber));
    }

    private void initializeButtonSubmit() {
        binding.buttonSubmit.setId(View.generateViewId());
        buttonId = binding.buttonSubmit.getId();
        binding.buttonSubmit.setOnClickListener(onClickListener);
    }

    private void changeKeyboardState() {
        if (onOffLoadDto.isLocked) {
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
            binding.imageButtonShowKeyboard.setVisibility(View.GONE);
        } else if (FOCUS_ON_EDIT_TEXT && (shouldEnterNumber || canEnterNumber))
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

        binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.balance));


        binding.textViewPreNumber.setOnClickListener(onClickListener);
        binding.textViewAddress.setOnLongClickListener(onLongClickListener);
    }

    private void initializeSpinner() {
        final String[] items = new String[counterStateDtos.size()];
        for (int i = 0; i < counterStateDtos.size(); i++) {
            items[i] = counterStateDtos.get(i).title;
        }
        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
        binding.spinner.setAdapter(adapter);
        boolean found = false;
        int i;
        for (i = 0; i < counterStateDtos.size() && !found; i++)
            if (counterStateDtos.get(i).id == onOffLoadDto.counterStateId) {
                found = true;
            }
        binding.spinner.setSelection(found ? i - 1 : 0);
        setCounterStateField(found ? i - 1 : 0);
        setOnSpinnerSelectedListener();
    }

    private void setOnSpinnerSelectedListener() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                setCounterStateField(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setCounterStateField(int i) {
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
        if (!canEnterNumber && !shouldEnterNumber) textView.setText("");
        changeKeyboardState();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnKeyboardButtonsClickListener() {
        if (!getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue())) {
            binding.buttonKeyboard0.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard1.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard2.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard3.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard4.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard5.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard6.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard7.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard8.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboard9.setOnClickListener(onKeyboardClickListener);
            binding.buttonKeyboardBackspace.setOnClickListener(onKeyboardClickListener);
        } else {
            binding.buttonKeyboard0.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard1.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard2.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard3.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard4.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard5.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard6.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard7.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard8.setOnTouchListener(onTouchListener);
            binding.buttonKeyboard9.setOnTouchListener(onTouchListener);
            binding.buttonKeyboardBackspace.setOnTouchListener(onTouchListener);
        }
        binding.imageButtonHideKeyboard.setOnClickListener(onKeyboardClickListener);
        binding.imageButtonShowKeyboard.setOnClickListener(onKeyboardClickListener);
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
        new TedPermission(requireContext())
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
                forceClose(requireActivity());
            }
        };
        new TedPermission(requireContext())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(STORAGE_PERMISSIONS).check();
    }

    private void checkPermissions() {
        if (enableGps(requireActivity()))
            if (!checkLocationPermission(requireContext())) {
                askLocationPermission();
            } else if (!checkStoragePermission(getContext())) {
                askStoragePermission();
            } else {
                attemptSend();
            }
    }

    private void attemptSend() {
        if (!shouldEnterNumber && lockProcess(true)) {
            canBeEmpty();
        } else {
            canNotBeEmpty();
        }
    }

    private boolean lockProcess(final boolean canContinue) {
        onOffLoadDto.attemptCount++;
        ((ReadingActivity) requireActivity()).updateOnOffLoadByAttempt(position);
        if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount + 1 == getLockNumber(getActiveCompanyName()))
            new CustomToast().error(getString(R.string.mistakes_error).concat(onOffLoadDto.eshterak)
                            .concat("\nbtn: ").concat(String.valueOf(buttonId)).concat(" , txt: ")
                            .concat(String.valueOf(textViewId))
                    , Toast.LENGTH_LONG);
        if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount == getLockNumber(getActiveCompanyName()))
            new CustomToast().error(getString(R.string.by_mistakes).
                    concat(onOffLoadDto.eshterak).concat(getString(R.string.is_locked))
                    .concat("\nbtn: ").concat(String.valueOf(buttonId)).concat(" , txt: ")
                    .concat(String.valueOf(textViewId)), Toast.LENGTH_SHORT);
        if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount >= getLockNumber(getActiveCompanyName())) {
            onOffLoadDto.isLocked = true;
            textView.setText("");
            ((ReadingActivity) requireActivity()).updateOnOffLoadByLock(position);
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
            binding.imageButtonShowKeyboard.setVisibility(View.GONE);
            return canContinue;
        }
        return true;
    }

    private void canBeEmpty() {
        if (textView.getText().toString().isEmpty() || isMane) {
            ((ReadingActivity) requireActivity()).updateOnOffLoadWithoutCounterNumber(position,
                    counterStateCode, counterStatePosition);
        } else {
            final int currentNumber = getDigits(textView.getText().toString());
            final int use = currentNumber - onOffLoadDto.preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(requireContext(), NOT_SAVE);
                String message = getString(R.string.less_than_pre);
                textView.setError(message);
                message = message.concat("\n").concat(onOffLoadDto.eshterak)
                        .concat("\nbtn: ").concat(String.valueOf(buttonId)).concat(" , txt: ")
                        .concat(String.valueOf(textViewId));
                new CustomToast().warning(message, Toast.LENGTH_LONG);
                textView.requestFocus();
            } else {
                ((ReadingActivity) requireActivity()).updateOnOffLoadByNumber(position, currentNumber,
                        counterStateCode, counterStatePosition);
            }
        }
    }

    private void canNotBeEmpty() {
        if (textView.getText().toString().isEmpty()) {
            makeRing(requireContext(), NOT_SAVE);
            String message = getString(R.string.counter_empty);
            textView.setError(message);
            textView.requestFocus();
            new CustomToast().warning(message, Toast.LENGTH_LONG);
        } else if (lockProcess(false)) {
            final int currentNumber = getDigits(textView.getText().toString());
            final int use = currentNumber - onOffLoadDto.preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(requireContext(), NOT_SAVE);
                String message = getString(R.string.less_than_pre);
                textView.setError(message);
                textView.requestFocus();
                message = message.concat("\n").concat(onOffLoadDto.eshterak)
                        .concat("\nbtn: ").concat(String.valueOf(buttonId)).concat(" , txt: ")
                        .concat(String.valueOf(textViewId));
                new CustomToast().warning(message, Toast.LENGTH_LONG);
            } else {
                notEmpty(currentNumber);
            }
        }
    }

    private void lessThanPre(int currentNumber) {
        if (!isMakoos)
            ((ReadingActivity) requireActivity()).updateOnOffLoadByNumber(position, currentNumber,
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
                    ((ReadingActivity) requireActivity()).updateOnOffLoadByNumber(position, currentNumber,
                            counterStateCode, counterStatePosition, NORMAL.getValue());
                    break;
            }
        }
        if (type != null) {
            final FragmentManager fm = requireActivity().getSupportFragmentManager();
            AreYouSureFragment.newInstance(position, currentNumber, type, counterStateCode,
                    counterStatePosition).show(fm, "ARE_YOU_SURE_".concat(onOffLoadDto.eshterak));
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
                    ((ReadingActivity) requireActivity()).updateOnOffLoadByNumber(position, currentNumber,
                            counterStateCode, counterStatePosition, NORMAL.getValue());
                    break;
            }
        }
        if (type != null) {
            final FragmentManager fm = requireActivity().getSupportFragmentManager();
            AreYouSureFragment.newInstance(position, currentNumber, type, counterStateCode,
                    counterStatePosition).show(fm, "ARE_YOU_SURE_".concat(onOffLoadDto.eshterak));
        }
    }

    private int getDigits(String number) {
        if (!TextUtils.isEmpty(number) && TextUtils.isDigitsOnly(number)) {
            return Integer.parseInt(number);
        } else {
            return 0;
        }
    }

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    final AudioManager am = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(AudioManager.FX_KEY_CLICK, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int id = view.getId();
                if (id == R.id.image_button_show_keyboard || id == R.id.image_button_hide_keyboard) {
                    FOCUS_ON_EDIT_TEXT = !FOCUS_ON_EDIT_TEXT;
                    changeKeyboardState();
                } else if (id == R.id.button_keyboard_backspace) {
                    if (textView.getText().toString().length() > 0)
                        textView.setText(textView.getText().toString()
                                .substring(0, textView.getText().toString().length() - 1));
                } else if (id == R.id.button_keyboard_0) {
                    textView.setText(textView.getText().toString().concat("0"));
                } else if (id == R.id.button_keyboard_1) {
                    textView.setText(textView.getText().toString().concat("1"));
                } else if (id == R.id.button_keyboard_2) {
                    textView.setText(textView.getText().toString().concat("2"));
                } else if (id == R.id.button_keyboard_3) {
                    textView.setText(textView.getText().toString().concat("3"));
                } else if (id == R.id.button_keyboard_4) {
                    textView.setText(textView.getText().toString().concat("4"));
                } else if (id == R.id.button_keyboard_5) {
                    textView.setText(textView.getText().toString().concat("5"));
                } else if (id == R.id.button_keyboard_6) {
                    textView.setText(textView.getText().toString().concat("6"));
                } else if (id == R.id.button_keyboard_7) {
                    textView.setText(textView.getText().toString().concat("7"));
                } else if (id == R.id.button_keyboard_8) {
                    textView.setText(textView.getText().toString().concat("8"));
                } else if (id == R.id.button_keyboard_9) {
                    textView.setText(textView.getText().toString().concat("9"));
                }
            }
            return false;
        }
    };

    private final View.OnClickListener onKeyboardClickListener = view -> {
        try {
            final AudioManager am = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(AudioManager.FX_KEY_CLICK, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int id = view.getId();
        if (id == R.id.image_button_show_keyboard || id == R.id.image_button_hide_keyboard) {
            FOCUS_ON_EDIT_TEXT = !FOCUS_ON_EDIT_TEXT;
            changeKeyboardState();
        } else if (id == R.id.button_keyboard_backspace) {
            if (textView.getText().toString().length() > 0)
                textView.setText(textView.getText().toString()
                        .substring(0, textView.getText().toString().length() - 1));
        } else if (id == R.id.button_keyboard_0) {
            textView.setText(textView.getText().toString().concat("0"));
        } else if (id == R.id.button_keyboard_1) {
            textView.setText(textView.getText().toString().concat("1"));
        } else if (id == R.id.button_keyboard_2) {
            textView.setText(textView.getText().toString().concat("2"));
        } else if (id == R.id.button_keyboard_3) {
            textView.setText(textView.getText().toString().concat("3"));
        } else if (id == R.id.button_keyboard_4) {
            textView.setText(textView.getText().toString().concat("4"));
        } else if (id == R.id.button_keyboard_5) {
            textView.setText(textView.getText().toString().concat("5"));
        } else if (id == R.id.button_keyboard_6) {
            textView.setText(textView.getText().toString().concat("6"));
        } else if (id == R.id.button_keyboard_7) {
            textView.setText(textView.getText().toString().concat("7"));
        } else if (id == R.id.button_keyboard_8) {
            textView.setText(textView.getText().toString().concat("8"));
        } else if (id == R.id.button_keyboard_9) {
            textView.setText(textView.getText().toString().concat("9"));
        }
    };
    private final View.OnClickListener onClickListener = view -> {
        final int id = view.getId();
        if (id == buttonId) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            checkPermissions();
        } else if (id == R.id.text_view_pre_number) {
            if (debtOrNumber) {
                debtOrNumber = false;
                binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.balance));
                binding.textViewPreNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            } else {
                if (onOffLoadDto.hasPreNumber) {
                    debtOrNumber = true;
                    requireActivity().runOnUiThread(() -> {
                        binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber));
                        final TypedValue typedValue = new TypedValue();
                        requireActivity().getTheme().resolveAttribute(android.R.attr.textColor, typedValue, true);
                        binding.textViewPreNumber.setTextColor(typedValue.data);
                    });
                    ((ReadingActivity) requireActivity()).updateOnOffLoadByPreNumber(position);
                } else new CustomToast().warning(getString(R.string.can_not_show_pre));
            }
        } else if (id == textViewId)
            if (!onOffLoadDto.isLocked && (shouldEnterNumber || canEnterNumber)) {
                FOCUS_ON_EDIT_TEXT = true;
                binding.relativeLayoutKeyboard.setVisibility(View.VISIBLE);
            }
    };
    private final View.OnLongClickListener onLongClickListener = view -> {
        final int id = view.getId();
        if (id == R.id.text_view_address)
            ShowDialogOnce(requireContext(), POSSIBLE_DIALOG.getValue().concat(onOffLoadDto.eshterak),
                    PossibleFragment.newInstance(onOffLoadDto, position, true));
        else if (id == textViewId)
            textView.setText("");
        return false;
    };

    @Override
    public void onResume() {
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener((view, i, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK) {
                    if (binding.relativeLayoutKeyboard.getVisibility() == View.VISIBLE)
                        binding.relativeLayoutKeyboard.setVisibility(View.GONE);
                    else requireActivity().onBackPressed();
                    return true;
                }
                return false;
            });
            changeKeyboardState();
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
        karbariDto = null;
        readingConfigDefaultDto = null;
        binding = null;
    }
}