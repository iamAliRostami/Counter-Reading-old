package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.FragmentTags.ARE_YOU_SURE;
import static com.leon.counter_reading.enums.FragmentTags.POSSIBLE_DIALOG;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.enums.NotificationType.NOT_SAVE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KEYBOARD_TYPE;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.Constants.FOCUS_ON_EDIT_TEXT;
import static com.leon.counter_reading.helpers.Constants.LOCATION_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.STORAGE_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.counterStateDtos;
import static com.leon.counter_reading.helpers.Constants.guilds;
import static com.leon.counter_reading.helpers.Constants.karbariDtos;
import static com.leon.counter_reading.helpers.Constants.onOffLoadDtos;
import static com.leon.counter_reading.helpers.Constants.readingConfigDefaultDtos;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getLockNumber;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getDigits;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.MakeNotification.ringNotification;
import static com.leon.counter_reading.utils.PermissionManager.checkLocationPermission;
import static com.leon.counter_reading.utils.PermissionManager.checkStoragePermission;
import static com.leon.counter_reading.utils.PermissionManager.enableGps;
import static com.leon.counter_reading.utils.PermissionManager.forceClose;
import static com.leon.counter_reading.utils.reading.Counting.checkHighLow;
import static com.leon.counter_reading.utils.reading.Counting.checkHighLowMakoos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerAdapter;
import com.leon.counter_reading.databinding.FragmentReadingBinding;
import com.leon.counter_reading.fragments.dialog.AreYouSureFragment;
import com.leon.counter_reading.fragments.dialog.PossibleFragment;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.view_models.ReadingViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private final ReadingViewModel readingVM = new ReadingViewModel();
    private FragmentReadingBinding binding;
    private Callback readingActivity;
    private long lastClickTime = 0;

    public ReadingFragment() {
    }

    public static ReadingFragment newInstance() {
        return new ReadingFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.clear();
            Bundle args = new Bundle();
            args.putInt(POSITION.getValue(), readingVM.getPosition());
            outState.putAll(args);
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

    private void getBundle(final Bundle bundle) {
        try {
            readingVM.setPosition(bundle.getInt(POSITION.getValue()));
            readingVM.setReadingConfigDefaultDto(readingConfigDefaultDtos.get(readingVM.getPosition()));
            readingVM.setKarbariDto(karbariDtos.get(readingVM.getPosition()));
            readingVM.setGuilds(guilds.get(readingVM.getPosition()));
            readingVM.setOnOffLoadDto(onOffLoadDtos.get(readingVM.getPosition()));
        } catch (Exception e) {
            Intent intent = requireActivity().getIntent();
            requireActivity().finish();
            startActivity(intent);
        }
        bundle.clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
        setHasOptionsMenu(true);
    }

    private ReadingFragment(int position) {
        readingVM.setPosition(position);
        readingVM.setReadingConfigDefaultDto(readingConfigDefaultDtos.get(position));
        readingVM.setKarbariDto(karbariDtos.get(position));
        readingVM.setGuilds(guilds.get(position));
        readingVM.setOnOffLoadDto(onOffLoadDtos.get(position));
    }

    public static ReadingFragment newInstance(int position) {
        return new ReadingFragment(position);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) savedInstanceState.clear();
        binding = FragmentReadingBinding.inflate(inflater, container, false);
        try {
            ILocationTracking tracking = getLocationTracker(requireActivity()) != null ? getLocationTracker(requireActivity()) : null;
            if (tracking != null)
                readingVM.setAccuracy((int) tracking.getAccuracy());
            else readingVM.setAccuracy(-1);
        } catch (Exception e) {
            e.printStackTrace();
            readingVM.setAccuracy(-1);
        }

        binding.setReadingVM(readingVM);
        return binding.getRoot();
    }

    private void initialize() {
        binding.buttonSubmit.setId(readingVM.getButtonId());
        binding.editTextNumber.setId(readingVM.getTextViewId());
        binding.textViewPreNumber.setSelected(true);
        binding.textViewKarbari.setSelected(true);
        initializeSpinner();
        setOnEventsListener();
        if (!readingVM.getOnOffLoadDto().displayRadif && !readingVM.getOnOffLoadDto().displayBillId)
            binding.textViewRadif.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnEventsListener() {
        binding.imageButtonHideKeyboard.setOnClickListener(this);
        binding.imageButtonShowKeyboard.setOnClickListener(this);
        binding.textViewAddress.setOnLongClickListener(this);
        binding.textViewPreNumber.setOnClickListener(this);
        binding.editTextNumber.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
        //TODO
        binding.buttonKeyboard0.setOnClickListener(clickListener);
        binding.buttonKeyboard1.setOnClickListener(clickListener);
        binding.buttonKeyboard2.setOnClickListener(clickListener);
        binding.buttonKeyboard3.setOnClickListener(clickListener);
        binding.buttonKeyboard4.setOnClickListener(clickListener);
        binding.buttonKeyboard5.setOnClickListener(clickListener);
        binding.buttonKeyboard6.setOnClickListener(clickListener);
        binding.buttonKeyboard7.setOnClickListener(clickListener);
        binding.buttonKeyboard8.setOnClickListener(clickListener);
        binding.buttonKeyboard9.setOnClickListener(clickListener);
        binding.buttonKeyboardBackspace.setOnClickListener(clickListener);

        binding.buttonKeyboard0.setOnTouchListener(touchListener);
        binding.buttonKeyboard1.setOnTouchListener(touchListener);
        binding.buttonKeyboard2.setOnTouchListener(touchListener);
        binding.buttonKeyboard3.setOnTouchListener(touchListener);
        binding.buttonKeyboard4.setOnTouchListener(touchListener);
        binding.buttonKeyboard5.setOnTouchListener(touchListener);
        binding.buttonKeyboard6.setOnTouchListener(touchListener);
        binding.buttonKeyboard7.setOnTouchListener(touchListener);
        binding.buttonKeyboard8.setOnTouchListener(touchListener);
        binding.buttonKeyboard9.setOnTouchListener(touchListener);
        binding.buttonKeyboardBackspace.setOnTouchListener(touchListener);
        //TODO
    }

    private void changeKeyboardState() {
        if (readingVM.getOnOffLoadDto().isLocked) {
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
            binding.imageButtonShowKeyboard.setVisibility(View.GONE);
        } else if (FOCUS_ON_EDIT_TEXT && (readingVM.isShouldEnterNumber() || readingVM.isCanEnterNumber()))
            binding.relativeLayoutKeyboard.setVisibility(View.VISIBLE);
        else
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
    }

    private void initializeSpinner() {
        String[] items = new String[counterStateDtos.size()];
        for (int i = 0; i < counterStateDtos.size(); i++) {
            items[i] = counterStateDtos.get(i).title;
        }
        SpinnerAdapter adapter = new SpinnerAdapter(requireActivity(), items);
        binding.spinner.setAdapter(adapter);
        boolean found = false;
        int i;
        for (i = 0; i < counterStateDtos.size() && !found; i++)
            if (counterStateDtos.get(i).id == readingVM.getOnOffLoadDto().counterStateId) {
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
        readingVM.setCounterStateField(counterStateDtos.get(i), i);
        binding.imageButtonShowKeyboard.setVisibility(readingVM.isCanEnterNumber() ||
                readingVM.isShouldEnterNumber() ? View.VISIBLE : View.GONE);
        changeKeyboardState();
    }

    private void attemptSend() {
        if (!readingVM.isShouldEnterNumber() && lockProcess(true)) {
            canBeEmpty();
        } else {
            canNotBeEmpty();
        }
    }

    private boolean lockProcess(final boolean canContinue) {
        readingVM.getOnOffLoadDto().attemptCount++;
        readingActivity.updateOnOffLoadByAttempt(readingVM.getPosition());
        if (!readingVM.getOnOffLoadDto().isLocked && readingVM.getOnOffLoadDto().attemptCount + 1 == getLockNumber())
            new CustomToast().error(getString(R.string.mistakes_error), Toast.LENGTH_LONG);
        if (!readingVM.getOnOffLoadDto().isLocked && readingVM.getOnOffLoadDto().attemptCount == getLockNumber())
            new CustomToast().error(getString(R.string.by_mistakes).
                    concat(readingVM.getOnOffLoadDto().eshterak).concat(getString(R.string.is_locked)), Toast.LENGTH_SHORT);
        if (!readingVM.getOnOffLoadDto().isLocked && readingVM.getOnOffLoadDto().attemptCount >= getLockNumber()) {
            readingVM.getOnOffLoadDto().isLocked = true;
            readingVM.setCounterNumber("");
            readingActivity.updateOnOffLoadByLock(readingVM.getPosition());
            binding.relativeLayoutKeyboard.setVisibility(View.GONE);
            binding.imageButtonShowKeyboard.setVisibility(View.GONE);
            return canContinue;
        }
        return true;
    }

    private void canBeEmpty() {
        if (readingVM.getCounterNumber().isEmpty() || readingVM.isMane()) {
            readingActivity.updateOnOffLoadWithoutNumber(readingVM.getPosition(),
                    readingVM.getCounterStateCode(), readingVM.getCounterStatePosition());
        } else {
            final int currentNumber = getDigits(readingVM.getCounterNumber());
            final int use = currentNumber - readingVM.getOnOffLoadDto().preNumber;
            if (readingVM.isCanLessThanPre()) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(requireContext(), NOT_SAVE);
                final String message = getString(R.string.less_than_pre);
                new CustomToast().warning(message, Toast.LENGTH_LONG);
                binding.editTextNumber.setError(message);
                binding.editTextNumber.requestFocus();
            } else {
                readingActivity.updateOnOffLoadByNumber(readingVM.getPosition(), currentNumber,
                        readingVM.getCounterStateCode(), readingVM.getCounterStatePosition());
            }
        }
    }

    private void canNotBeEmpty() {
        if (readingVM.getCounterNumber() == null || readingVM.getCounterNumber().isEmpty()) {
            makeRing(requireContext(), NOT_SAVE);
            String message = getString(R.string.counter_empty);
            binding.editTextNumber.setError(message);
            binding.editTextNumber.requestFocus();
            new CustomToast().warning(message, Toast.LENGTH_LONG);
        } else if (lockProcess(false)) {
            final int currentNumber = getDigits(readingVM.getCounterNumber());
            final int use = currentNumber - readingVM.getOnOffLoadDto().preNumber;
            if (readingVM.isCanLessThanPre()) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(requireContext(), NOT_SAVE);
                final String message = getString(R.string.less_than_pre);
                binding.editTextNumber.setError(message);
                binding.editTextNumber.requestFocus();
                new CustomToast().warning(message, Toast.LENGTH_LONG);
            } else {
                notEmpty(currentNumber);
            }
        }
    }

    private void lessThanPre(int currentNumber) {
        if (!readingVM.isMakoos())
            readingActivity.updateOnOffLoadByNumber(readingVM.getPosition(), currentNumber,
                    readingVM.getCounterStateCode(), readingVM.getCounterStatePosition());
        else {
            notEmptyIsMakoos(currentNumber);
        }
    }

    private void notEmptyIsMakoos(int currentNumber) {
        Integer type = null;
        if (currentNumber == readingVM.getOnOffLoadDto().preNumber) {
            type = ZERO.getValue();
        } else {
            final int status = checkHighLowMakoos(readingVM.getOnOffLoadDto(), readingVM.getKarbariDto(),
                    readingVM.getReadingConfigDefaultDto(), currentNumber);
            if (status == HIGH.getValue() || status == LOW.getValue()) {
                type = status;
            } else if (status == NORMAL.getValue()) {
                readingActivity.updateOnOffLoadByNumber(readingVM.getPosition(), currentNumber,
                        readingVM.getCounterStateCode(), readingVM.getCounterStatePosition(), NORMAL.getValue());
            }
        }
        if (type != null) {
            final FragmentManager fm = requireActivity().getSupportFragmentManager();
            AreYouSureFragment.newInstance(readingVM.getPosition(), currentNumber, type,
                    readingVM.getCounterStateCode(), readingVM.getCounterStatePosition()).show(fm,
                    String.format("%s%s", ARE_YOU_SURE.getValue(), readingVM.getOnOffLoadDto().eshterak));
        }
    }

    private void notEmpty(int currentNumber) {
        Integer type = null;
        if (currentNumber == readingVM.getOnOffLoadDto().preNumber) {
            //TODO esf
            type = ZERO.getValue();
        } else {
            final int status = checkHighLow(readingVM.getOnOffLoadDto(), readingVM.getKarbariDto(),
                    readingVM.getReadingConfigDefaultDto(), currentNumber);
            if (status == HIGH.getValue() || status == LOW.getValue()) {
                type = status;
            } else if (status == NORMAL.getValue()) {
                readingActivity.updateOnOffLoadByNumber(readingVM.getPosition(),
                        currentNumber, readingVM.getCounterStateCode(), readingVM.getCounterStatePosition(), NORMAL.getValue());
            }
        }
        if (type != null) {
            final FragmentManager fm = requireActivity().getSupportFragmentManager();
            AreYouSureFragment.newInstance(readingVM.getPosition(), currentNumber, type, readingVM.getCounterStateCode(),
                    readingVM.getCounterStatePosition()).show(fm, ARE_YOU_SURE.getValue().concat(readingVM.getOnOffLoadDto().eshterak));
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == readingVM.getButtonId()) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            checkPermissions();
        } else if (id == R.id.text_view_pre_number) {
            if (readingVM.switchDebtNumber())
                readingActivity.updateOnOffLoadByPreNumber(readingVM.getPosition());
        } else if (id == readingVM.getTextViewId()) {
            if (!readingVM.getOnOffLoadDto().isLocked && (readingVM.isShouldEnterNumber() || readingVM.isCanEnterNumber())) {
                FOCUS_ON_EDIT_TEXT = true;
                binding.relativeLayoutKeyboard.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.image_button_show_keyboard || id == R.id.image_button_hide_keyboard) {
            ringNotification();
            FOCUS_ON_EDIT_TEXT = !FOCUS_ON_EDIT_TEXT;
            changeKeyboardState();
        }
    }

    private final View.OnClickListener clickListener = v -> {
        if (!getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue()))
            keyboardEvent(v);
    };
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener touchListener = (v, event) -> {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue()))
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                keyboardEvent(v);
        return false;
    };

    private void keyboardEvent(View view) {
        binding.buttonSubmit.setEnabled(false);
        ringNotification();
        if (view.getId() == R.id.button_keyboard_backspace) {
            if (readingVM.getCounterNumber() != null && !readingVM.getCounterNumber().isEmpty())
                readingVM.setCounterNumber(readingVM.getCounterNumber().substring(0, readingVM.getCounterNumber().length() - 1));
        } else if (readingVM.getCounterNumber() != null && readingVM.getCounterNumber().length() < 9) {
            readingVM.setCounterNumber((readingVM.getCounterNumber() != null ? readingVM.getCounterNumber() : "")
                    .concat(((Button) view).getText().toString()));
        }
        binding.buttonSubmit.setEnabled(true);
    }

    @Override
    public boolean onLongClick(View view) {
        final int id = view.getId();
        if (id == R.id.text_view_address)
            ShowDialogOnce(requireContext(), POSSIBLE_DIALOG.getValue().concat(readingVM.getOnOffLoadDto().eshterak),
                    PossibleFragment.newInstance(true, readingVM.getPosition(), readingVM.getOnOffLoadDto()));
        return false;
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
                new CustomToast().warning(getString(R.string.cant_fine_location));
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

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

    public interface Callback {
        int getPosition();

        void updateOnOffLoadByPreNumber(int position);

        void updateOnOffLoadByLock(int position);

        void updateOnOffLoadWithoutNumber(int position, int counterStateCode,
                                          int counterStatePosition);

        void updateOnOffLoadByAttempt(int position, boolean... booleans);

        void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                     int counterStatePosition);

        void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                     int counterStatePosition, int type);
    }
}