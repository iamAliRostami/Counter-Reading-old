package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.Manifest;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.fragments.AreYouSureFragment;
import com.leon.counter_reading.fragments.PossibleFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.reading.Counting;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByIsShown;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadDtoByLock;

import java.util.ArrayList;

public class ViewPagerAdapterReading2 extends RecyclerView.Adapter<ViewHolderReading> {
    private final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    private final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private final ArrayList<OnOffLoadDto> onOffLoadDtos;
    private final ArrayList<OnOffLoadDto> onOffLoadDtosTemp;
    private final ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    private final SpinnerCustomAdapter adapter;
    private final Activity activity;

    private int counterStateCode;
    private int counterStatePosition;
    private boolean isMane;
    private boolean isMakoos;
    private boolean canLessThanPre;

    public ViewPagerAdapterReading2(ReadingData readingData, ArrayList<OnOffLoadDto> onOffLoadDtosTemp,
                                    Activity activity) {
        this.activity = activity;
        this.onOffLoadDtos = new ArrayList<>(readingData.onOffLoadDtos);
        this.onOffLoadDtosTemp = new ArrayList<>(onOffLoadDtosTemp);
        final String[] items = new String[readingData.counterStateDtos.size()];
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            items[i] = readingData.counterStateDtos.get(i).title;
        }
        adapter = new SpinnerCustomAdapter(activity, items);
        counterStateDtos.addAll(readingData.counterStateDtos);
        for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
            int k = 0;
            boolean found = false;
            while (!found && k < readingData.readingConfigDefaultDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).zoneId == readingData.readingConfigDefaultDtos.get(k).zoneId) {
                    readingConfigDefaultDtos.add(readingData.readingConfigDefaultDtos.get(k));
                    found = true;
                }
                k++;
            }
            k = 0;
            found = false;
            while (!found && k < readingData.karbariDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).karbariCode == readingData.karbariDtos.get(k).moshtarakinId) {
                    karbariDtos.add(readingData.karbariDtos.get(k));
                    found = true;
                }
                k++;
            }
            k = 0;
            found = false;
            while (!found && k < readingData.trackingDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).trackNumber == readingData.trackingDtos.get(k).trackNumber) {
                    readingData.onOffLoadDtos.get(i).hasPreNumber = readingData.trackingDtos.get(k).hasPreNumber;
                    readingData.onOffLoadDtos.get(i).displayBillId = readingData.trackingDtos.get(k).displayBillId;
                    readingData.onOffLoadDtos.get(i).displayRadif = readingData.trackingDtos.get(k).displayRadif;
                    found = true;
                }
                k++;
            }
            for (int j = 0; j < readingData.qotrDictionary.size(); j++) {
                if (readingData.onOffLoadDtos.get(i).qotrCode == readingData.qotrDictionary.get(j).id)
                    readingData.onOffLoadDtos.get(i).qotr = readingData.qotrDictionary.get(j).title;
                if (readingData.onOffLoadDtos.get(i).sifoonQotrCode == readingData.qotrDictionary.get(j).id)
                    readingData.onOffLoadDtos.get(i).sifoonQotr = readingData.qotrDictionary.get(j).title;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolderReading onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(activity).inflate(R.layout.fragment_reading, parent, false);
        view.setRotationY(180);
        return new ViewHolderReading(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderReading holder, int position) {
        holder.setIsRecyclable(false);
        holder.editTextNumber.setOnLongClickListener(view -> {
            holder.editTextNumber.setText("");
            return false;
        });
        final OnOffLoadDto onOffLoadDto = onOffLoadDtos.get(position);
        if (onOffLoadDto.isLocked) {
            new CustomToast().error(activity.getString(R.string.by_mistakes)
                    .concat(onOffLoadDto.eshterak).concat(activity
                            .getString(R.string.is_locked)), Toast.LENGTH_SHORT);
            holder.editTextNumber.setFocusable(false);
            holder.editTextNumber.setEnabled(false);
        }

        initializeViews(holder, onOffLoadDto, position);
        initializeSpinner(holder, onOffLoadDto);
        holder.buttonSubmit.setOnClickListener(v -> checkPermissions(holder, position));
    }

    private void initializeViews(ViewHolderReading holder, OnOffLoadDto onOffLoadDto, int position) {
        holder.textViewAhad1Title.setText(DifferentCompanyManager.getAhad1(
                DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        holder.textViewAhad2Title.setText(DifferentCompanyManager.getAhad2(
                DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        holder.textViewAhadTotalTitle.setText(DifferentCompanyManager.getAhadTotal(
                DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        holder.textViewAddress.setText(onOffLoadDto.address);
        holder.textViewName.setText(onOffLoadDto.firstName.concat(" ")
                .concat(onOffLoadDto.sureName));
        holder.textViewPreDate.setText(onOffLoadDto.preDate);
        holder.textViewSerial.setText(onOffLoadDto.counterSerial);

        if (onOffLoadDto.displayRadif)
            holder.textViewRadif.setText(String.valueOf(onOffLoadDto.radif));
        else if (onOffLoadDto.displayBillId)
            holder.textViewRadif.setText(String.valueOf(onOffLoadDto.billId));
        else holder.textViewRadif.setVisibility(View.GONE);

        holder.textViewAhad1.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));

        if (onOffLoadDto.counterNumber != null) {
            holder.editTextNumber.setText(String.valueOf(onOffLoadDto.counterNumber));
        }
        holder.textViewAhad2.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));
        holder.textViewAhadTotal.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        if (readingConfigDefaultDtos.get(position).isOnQeraatCode) {
            holder.textViewCode.setText(onOffLoadDto.qeraatCode);
        } else holder.textViewCode.setText(onOffLoadDto.eshterak);

        holder.textViewKarbari.setText(karbariDtos.get(position).title);
        holder.textViewBranch.setText(onOffLoadDto.qotr.equals(activity.getString(R.string.unknown)) ? "-" : onOffLoadDto.qotr);
        holder.textViewSiphon.setText(onOffLoadDto.sifoonQotr.equals(activity.getString(R.string.unknown)) ? "-" : onOffLoadDto.sifoonQotr);

        if (onOffLoadDto.counterNumberShown) {
            holder.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber));
        }
        holder.textViewPreNumber.setOnClickListener(v -> {
            if (onOffLoadDto.hasPreNumber) {
                activity.runOnUiThread(() ->
                        holder.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber)));
                new UpdateOnOffLoadByIsShown(onOffLoadDto).execute(activity);
            } else {
                new CustomToast().warning(activity.getString(R.string.can_not_show_pre));
            }
        });
        holder.textViewAddress.setOnLongClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            PossibleFragment possibleFragment = PossibleFragment.newInstance(onOffLoadDto, position,
                    true);
            possibleFragment.show(fragmentManager, activity.getString(R.string.dynamic_navigation));
            return false;
        });
    }

    private void initializeSpinner(ViewHolderReading holder,OnOffLoadDto onOffLoadDto) {
        holder.spinner.setAdapter(adapter);
        boolean found = false;
        int i;
        for (i = 0; i < counterStateDtos.size() && !found; i++)
            if (counterStateDtos.get(i).id == onOffLoadDto.counterStateId) {
                found = true;
            }
        holder.spinner.setSelection(found ? i - 1 : 0);
        setOnSpinnerSelectedListener(holder);
    }

    private void setOnSpinnerSelectedListener(ViewHolderReading holder) {
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                CounterStateDto counterStateDto = counterStateDtos.get(i);
                holder.editTextNumber.setEnabled(counterStateDto.canEnterNumber
                        || counterStateDto.shouldEnterNumber);
                if (!(counterStateDto.canEnterNumber || counterStateDto.shouldEnterNumber))
                    holder.editTextNumber.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void attemptSend(ViewHolderReading holder, int position) {
        counterStatePosition = holder.spinner.getSelectedItemPosition();
        counterStateCode = counterStateDtos.get(counterStatePosition).id;
        canLessThanPre = counterStateDtos.get(counterStatePosition).canNumberBeLessThanPre;
        isMane = counterStateDtos.get(counterStatePosition).isMane;
        isMakoos = counterStateDtos.get(counterStatePosition).title.equals("معکوس");
        boolean canBeEmpty = !counterStateDtos.get(counterStatePosition).shouldEnterNumber;
        if (canBeEmpty) {
            canBeEmpty(holder, position);
        } else {
            canNotBeEmpty(holder, position);
        }
    }

    private void canBeEmpty(ViewHolderReading holder, int position) {
        if (holder.editTextNumber.getText().toString().isEmpty() || isMane) {
            ((ReadingActivity) activity).updateOnOffLoadWithoutCounterNumber(position,
                    counterStateCode, counterStatePosition);
        } else {
            View view = holder.editTextNumber;
            int currentNumber = Integer.parseInt(holder.editTextNumber.getText().toString());
            int use = currentNumber - onOffLoadDtos.get(position).preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber, position);
            } else if (use < 0) {
                makeRing(activity, NotificationType.NOT_SAVE);
                holder.editTextNumber.setError(activity.getString(R.string.less_than_pre));
                view.requestFocus();
            }
        }
    }

    private void canNotBeEmpty(ViewHolderReading holder, int position) {
        View view = holder.editTextNumber;
        if (holder.editTextNumber.getText().toString().isEmpty()) {
            makeRing(activity, NotificationType.NOT_SAVE);
            holder.editTextNumber.setError(activity.getString(R.string.counter_empty));
            view.requestFocus();
        } else {
            int currentNumber = Integer.parseInt(holder.editTextNumber.getText().toString());
            int use = currentNumber - onOffLoadDtos.get(position).preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber, position);
            } else if (use < 0) {
                makeRing(activity, NotificationType.NOT_SAVE);
                holder.editTextNumber.setError(activity.getString(R.string.less_than_pre));
                view.requestFocus();
            } else {
                notEmpty(currentNumber, position);
            }
        }
    }

    private void lessThanPre(int currentNumber, int position) {
        if (!isMakoos)
            ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position, currentNumber,
                    counterStateCode, counterStatePosition);
        else {
            notEmptyIsMakoos(currentNumber, position);
        }
    }

    private void notEmptyIsMakoos(int currentNumber, int position) {
        int type = -1000;
        if (currentNumber == onOffLoadDtos.get(position).preNumber) {
            type = HighLowStateEnum.ZERO.getValue();
        } else {
            int status = Counting.checkHighLowMakoos(onOffLoadDtos.get(position),
                    karbariDtos.get(position), readingConfigDefaultDtos.get(position), currentNumber);
            switch (status) {
                case 1:
                    type = HighLowStateEnum.HIGH.getValue();
                    break;
                case -1:
                    type = HighLowStateEnum.LOW.getValue();
                    break;
                case 0:
                    ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position,
                            currentNumber, counterStateCode, counterStatePosition,
                            HighLowStateEnum.NORMAL.getValue());
                    break;
            }
        }
        if (type != -1000) {
            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            AreYouSureFragment areYouSureFragment = AreYouSureFragment.newInstance(position,
                    currentNumber, type, counterStateCode, counterStatePosition);
            areYouSureFragment.show(fragmentManager, activity.getString(R.string.use_out_of_range));
        }
    }

    private void notEmpty(int currentNumber, int position) {
        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        AreYouSureFragment areYouSureFragment;
        if (currentNumber == onOffLoadDtos.get(position).preNumber) {
            areYouSureFragment = AreYouSureFragment.newInstance(
                    position, currentNumber, HighLowStateEnum.ZERO.getValue(),
                    counterStateCode, counterStatePosition);
            areYouSureFragment.show(fragmentManager, activity.getString(R.string.use_out_of_range));
        } else {
            int status = Counting.checkHighLow(onOffLoadDtos.get(position),
                    karbariDtos.get(position), readingConfigDefaultDtos.get(position), currentNumber);
            switch (status) {
                case 1:
                    areYouSureFragment = AreYouSureFragment.newInstance(
                            position, currentNumber, HighLowStateEnum.HIGH.getValue(),
                            counterStateCode, counterStatePosition);
                    areYouSureFragment.show(fragmentManager, activity.getString(R.string.use_out_of_range));
                    break;
                case -1:
                    areYouSureFragment = AreYouSureFragment.newInstance(
                            position, currentNumber, HighLowStateEnum.LOW.getValue(),
                            counterStateCode, counterStatePosition);
                    areYouSureFragment.show(fragmentManager, activity.getString(R.string.use_out_of_range));
                    break;
                case 0:
                    ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position,
                            currentNumber, counterStateCode, counterStatePosition,
                            HighLowStateEnum.NORMAL.getValue());
                    break;
            }
        }
    }

    private void askLocationPermission(ViewHolderReading holder, int position) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkPermissions(holder, position);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                new CustomToast().warning("به علت عدم دسترسی به مکان یابی، امکان ثبت وجود ندارد.");
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION).check();
    }

    private void askStoragePermission(ViewHolderReading holder, int position) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkPermissions(holder, position);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).check();
    }

    private void checkPermissions(ViewHolderReading holder, int position) {
        if (PermissionManager.gpsEnabledNew(activity))
            if (PermissionManager.checkLocationPermission(getContext())) {
                askLocationPermission(holder, position);
            } else if (PermissionManager.checkStoragePermission(getContext())) {
                askStoragePermission(holder, position);
            } else {
                onOffLoadDtos.get(position).attemptCount++;
                if (!onOffLoadDtos.get(position).isLocked && onOffLoadDtos.get(position).attemptCount + 1 == DifferentCompanyManager.getLockNumber(DifferentCompanyManager.getActiveCompanyName()))
                    new CustomToast().warning(activity.getString(R.string.mistakes_error), Toast.LENGTH_LONG);
                if (!onOffLoadDtos.get(position).isLocked && onOffLoadDtos.get(position).attemptCount == DifferentCompanyManager.getLockNumber(DifferentCompanyManager.getActiveCompanyName()))
                    new CustomToast().error(activity.getString(R.string.by_mistakes).
                            concat(onOffLoadDtos.get(position).eshterak).concat(activity.getString(R.string.is_locked)), Toast.LENGTH_LONG);
                MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().
                        updateOnOffLoadByAttemptNumber(onOffLoadDtos.get(position).id,
                                onOffLoadDtos.get(position).attemptCount);
                if (!onOffLoadDtos.get(position).isLocked && onOffLoadDtos.get(position).attemptCount >= DifferentCompanyManager.getLockNumber(DifferentCompanyManager.getActiveCompanyName())) {
                    new UpdateOnOffLoadDtoByLock(onOffLoadDtos, onOffLoadDtosTemp, position,
                            onOffLoadDtos.get(position).trackNumber, onOffLoadDtos.get(position).id)
                            .execute(activity);
                } else {
                    attemptSend(holder, position);
                }
            }
    }

    @Override
    public int getItemCount() {
        return onOffLoadDtos.size();
    }
}
