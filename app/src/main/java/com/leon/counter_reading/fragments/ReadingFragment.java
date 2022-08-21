package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.FragmentTags.ARE_YOU_SURE;
import static com.leon.counter_reading.enums.FragmentTags.POSSIBLE_DIALOG;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.enums.NotificationType.NOT_SAVE;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.Constants.FOCUS_ON_EDIT_TEXT;
import static com.leon.counter_reading.helpers.Constants.LOCATION_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.STORAGE_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.counterStateDtos;
import static com.leon.counter_reading.helpers.Constants.karbariDtos;
import static com.leon.counter_reading.helpers.Constants.onOffLoadDtos;
import static com.leon.counter_reading.helpers.Constants.readingConfigDefaultDtos;
import static com.leon.counter_reading.helpers.MyApplication.getDigits;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getLockNumber;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.MakeNotification.ringNotification;
import static com.leon.counter_reading.utils.PermissionManager.checkLocationPermission;
import static com.leon.counter_reading.utils.PermissionManager.checkStoragePermission;
import static com.leon.counter_reading.utils.PermissionManager.enableGps;
import static com.leon.counter_reading.utils.PermissionManager.forceClose;
import static com.leon.counter_reading.utils.reading.Counting.checkHighLow;
import static com.leon.counter_reading.utils.reading.Counting.checkHighLowMakoos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReadingBinding;
import com.leon.counter_reading.fragments.dialog.AreYouSureFragment;
import com.leon.counter_reading.fragments.dialog.PossibleFragment;
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
            final Bundle args = new Bundle();
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
            readingVM.setOnOffLoadDto(onOffLoadDtos.get(readingVM.getPosition()));
//            readingVM.setPosition(readingActivity.getPosition());
//            readingVM.setReadingConfigDefaultDto(readingActivity.getReadingConfigDefaultDto(readingVM.getPosition()));
//            readingVM.setKarbariDto(readingActivity.getKarbariDto(readingVM.getPosition()));
//            readingVM.setOnOffLoadDto(readingActivity.getOnOffLoad(readingVM.getPosition()));
        } catch (Exception e) {
            final Intent intent = requireActivity().getIntent();
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
//TODO
//        this.onOffLoadDto = Constants.onOffLoadDtos.get(position);
//        this.readingConfigDefaultDto = Constants.readingConfigDefaultDtos.get(position);
//        this.karbariDto = Constants.karbariDtos.get(position);
//        Log.e("position", String.valueOf(position));
//        readingVM.setReadingConfigDefaultDto(readingActivity.getReadingConfigDefaultDto(readingVM.getPosition()));
//        readingVM.setKarbariDto(readingActivity.getKarbariDto(readingVM.getPosition()));
//        readingVM.setOnOffLoadDto(readingActivity.getOnOffLoad(readingVM.getPosition()));
        readingVM.setReadingConfigDefaultDto(readingConfigDefaultDtos.get(position));
        readingVM.setKarbariDto(karbariDtos.get(position));
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
//TODO
//        readingVM.setPosition(readingActivity.getPosition());
//        readingVM.setReadingConfigDefaultDto(readingActivity.getReadingConfigDefaultDto(readingVM.getPosition()));
//        readingVM.setKarbariDto(readingActivity.getKarbariDto(readingVM.getPosition()));
//        readingVM.setOnOffLoadDto(readingActivity.getOnOffLoad(readingVM.getPosition()));
//        Log.e("position", String.valueOf(readingActivity.getPosition()));
//        Log.e("name", readingActivity.getOnOffLoad(readingActivity.getPosition()).firstName.concat(readingActivity.getOnOffLoad(readingActivity.getPosition()).sureName));
//        Log.e("position", String.valueOf(readingVM.getPosition()));
//        Log.e("name", readingVM.getOnOffLoadDto().firstName.concat(readingVM.getOnOffLoadDto().sureName));
        binding.setReadingVM(readingVM);
        return binding.getRoot();
    }

    private void initialize() {
        binding.buttonSubmit.setId(readingVM.getButtonId());
        binding.editTextNumber.setId(readingVM.getTextViewId());
        initializeSpinner();
        setOnEventsListener();
        if (!readingVM.getOnOffLoadDto().displayRadif && !readingVM.getOnOffLoadDto().displayBillId)
            binding.textViewRadif.setVisibility(View.GONE);
    }

    private void setOnEventsListener() {
        binding.imageButtonHideKeyboard.setOnClickListener(this);
        binding.imageButtonShowKeyboard.setOnClickListener(this);
        binding.textViewAddress.setOnLongClickListener(this);
        binding.textViewPreNumber.setOnClickListener(this);
        binding.editTextNumber.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
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
//TODO
//        final String[] items = new String[readingActivity.getCounterStateDtos().size()];
//        for (int i = 0; i < readingActivity.getCounterStateDtos().size(); i++) {
//            items[i] = readingActivity.getCounterStateDtos().get(i).title;
//        }
//        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
//        binding.spinner.setAdapter(adapter);
//        boolean found = false;
//        int i;
//        for (i = 0; i < readingActivity.getCounterStateDtos().size() && !found; i++)
//            if (readingActivity.getCounterStateDtos().get(i).id == readingVM.getOnOffLoadDto().counterStateId) {
//                found = true;
//            }
        final String[] items = new String[counterStateDtos.size()];
        for (int i = 0; i < counterStateDtos.size(); i++) {
            items[i] = counterStateDtos.get(i).title;
        }
        final SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
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
//      TODO
//        readingVM.setCounterStateField(readingActivity.getCounterStateDtos().get(i), i);
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
        if (!readingVM.getOnOffLoadDto().isLocked && readingVM.getOnOffLoadDto().attemptCount + 1 == getLockNumber(getActiveCompanyName()))
            new CustomToast().error(getString(R.string.mistakes_error), Toast.LENGTH_LONG);
        if (!readingVM.getOnOffLoadDto().isLocked && readingVM.getOnOffLoadDto().attemptCount == getLockNumber(getActiveCompanyName()))
            new CustomToast().error(getString(R.string.by_mistakes).
                    concat(readingVM.getOnOffLoadDto().eshterak).concat(getString(R.string.is_locked)), Toast.LENGTH_SHORT);
        if (!readingVM.getOnOffLoadDto().isLocked && readingVM.getOnOffLoadDto().attemptCount >= getLockNumber(getActiveCompanyName())) {
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
            readingActivity.updateOnOffLoadWithoutCounterNumber(readingVM.getPosition(),
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
        if (readingVM.getCounterNumber().isEmpty()) {
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

//        OnOffLoadDto getOnOffLoad(int position);
//
//        KarbariDto getKarbariDto(int position);
//
//        ReadingConfigDefaultDto getReadingConfigDefaultDto(int position);
//
//        ArrayList<CounterStateDto> getCounterStateDtos();

        void updateOnOffLoadByPreNumber(int position);

        void updateOnOffLoadByLock(int position);

        void updateOnOffLoadWithoutCounterNumber(int position, int counterStateCode,
                                                 int counterStatePosition);

        void updateOnOffLoadByAttempt(int position, boolean... booleans);

        void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                     int counterStatePosition);

        void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                     int counterStatePosition, int type);
    }
}