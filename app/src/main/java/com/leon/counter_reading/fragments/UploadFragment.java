package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.OfflineUtils.getStorageDirectories;
import static com.leon.counter_reading.utils.OfflineUtils.writeOnSdCard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.UploadType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.uploading.PrepareMultimedia;
import com.leon.counter_reading.utils.uploading.PrepareOffLoad;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UploadFragment extends Fragment {
    private FragmentUploadBinding binding;
    private Activity activity;
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private final int[] imageSrc = {R.drawable.img_upload_on, R.drawable.img_upload_off,
            R.drawable.img_multimedia};
    private int type;
    private String[] items;

    public static UploadFragment newInstance(int type) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TYPE.getValue(), type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getBundle();
    }

    private void getBundle() {
        trackingDtos.clear();
        trackingDtos.addAll(((UploadActivity) activity).getTrackingDtos());
        if (getArguments() != null) {
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        if (type == UploadType.MULTIMEDIA.getValue()) {
            binding.spinner.setVisibility(View.GONE);
            binding.textViewMultimedia.setVisibility(View.VISIBLE);
            setMultimediaInfo(activity);
        } else {
            items = TrackingDto.getTrackingDtoItems(trackingDtos, getString(R.string.select_one));
            setupSpinner();
        }
        binding.imageViewUpload.setImageResource(imageSrc[type]);
        setOnButtonUploadClickListener();
    }

    public void setMultimediaInfo(Activity activity) {
        int imagesCount = MyApplication.getApplicationComponent().MyDatabase().imageDao().getUnsentImageCount(false);
        int voicesCount = MyApplication.getApplicationComponent().MyDatabase().voiceDao().getUnsentVoiceCount(false);
        String message = String.format(activity.getString(R.string.unuploaded_multimedia), imagesCount, voicesCount);
        activity.runOnUiThread(() -> binding.textViewMultimedia.setText(message));
    }

    private void setupSpinner() {
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    private boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent, imagesCount, voicesCount, trackNumber;
        double alalMane;
        MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackNumber);
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackNumber);
            ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
            for (int i = 0; i < isManes.size(); i++) {
                mane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackNumber);
            }
            alalPercent = myDatabase.trackingDao().getAlalHesabByZoneId(trackingDtos
                    .get(binding.spinner.getSelectedItemPosition() - 1).zoneId);
            alalMane = (double) mane / total * 100;
            imagesCount = myDatabase.imageDao().getUnsentImageCountByTrackNumber(trackNumber, false);
            voicesCount = myDatabase.voiceDao().getUnsentVoiceCountByTrackNumber(trackNumber, false);
        } else return false;
        if (unread > 0) {
            String message = String.format(getString(R.string.unread_number), unread);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (mane > 0 && alalMane > (double) alalPercent) {
            String message = String.format(getString(R.string.darsad_alal_1), alalPercent, new DecimalFormat("###.##").format(alalMane), mane);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (imagesCount > 0 || voicesCount > 0) {
            String message = String.format(getString(R.string.unuploaded_multimedia),
                    imagesCount, voicesCount).concat("\n")
                    .concat(getString(R.string.recommend_multimedia));
            new CustomDialogModel(DialogType.YellowRedirect, activity, message,
                    getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    private void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == UploadType.NORMAL.getValue()) {
                if (checkOnOffLoad())
                    new PrepareOffLoad(activity,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id).
                            execute(activity);
            } else if (type == UploadType.OFFLINE.getValue()) {
                String[] retArray = getStorageDirectories();
                if (retArray.length == 0) {
                    Log.e("state", "Sdcard not Exists");
                } else {
                    for (String s : retArray) {
                        Log.e("path ", s);
                        writeOnSdCard(s);
                    }
                }
            } else if (type == UploadType.MULTIMEDIA.getValue()) {
                new PrepareMultimedia(activity, this, false).execute(activity);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trackingDtos.clear();
        items = null;
    }

    private class Inline implements CustomDialogModel.Inline {
        @Override
        public void inline() {
            new PrepareOffLoad(activity,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id).
                    execute(activity);
        }
    }
}