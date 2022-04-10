package com.leon.counter_reading.fragments.upload;

import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.YellowRedirect;
import static com.leon.counter_reading.enums.UploadType.MULTIMEDIA;
import static com.leon.counter_reading.enums.UploadType.NORMAL;
import static com.leon.counter_reading.enums.UploadType.OFFLINE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.Bundle;
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
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.uploading.PrepareMultimedia;
import com.leon.counter_reading.utils.uploading.PrepareOffLoad;
import com.leon.counter_reading.utils.uploading.PrepareOffLoadOffline;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UploadFragment extends Fragment {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private final int[] imageSrc = {R.drawable.img_upload_on, R.drawable.img_upload_off,
            R.drawable.img_multimedia};
    private FragmentUploadBinding binding;
    private Activity activity;
    private int type;
    private String[] items;

    public static UploadFragment newInstance(int type) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE.getValue(), type);
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
            type = getArguments().getInt(TYPE.getValue());
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
        if (type == MULTIMEDIA.getValue()) {
            binding.spinner.setVisibility(View.GONE);
            binding.textViewMultimedia.setVisibility(View.VISIBLE);
            setMultimediaInfo(activity);
        } else {
            items = TrackingDto.getTrackingDtoItems(trackingDtos, getString(R.string.select_one));
            setupSpinner();
        }
        if (type != OFFLINE.getValue())
            binding.textViewOfflineWarning.setVisibility(View.GONE);
        binding.imageViewUpload.setImageResource(imageSrc[type > -1 ? type : 0]);
        setOnButtonUploadClickListener();
    }

    public void setMultimediaInfo(Activity activity) {
        final int imagesCount = getApplicationComponent().MyDatabase().imageDao().getUnsentImageCount(false);
        final int voicesCount = getApplicationComponent().MyDatabase().voiceDao().getUnsentVoiceCount(false);
        final int imagesSize = getApplicationComponent().MyDatabase().imageDao().getUnsentImageSize(false);
        final int voicesSize = getApplicationComponent().MyDatabase().voiceDao().getUnsentVoiceSizes(false);

//        String message = String.format(activity.getString(R.string.unuploaded_multimedia), imagesCount, voicesCount);
        String message = "تعداد عکس: ".concat(String.valueOf(imagesCount))
                .concat(" *** حجم: ").concat(String.valueOf(imagesSize)).concat(" KB").concat("\n")
                .concat("تعداد صدا: ").concat(String.valueOf(voicesCount))
                .concat(" *** حجم: ").concat(String.valueOf(voicesSize)).concat(" KB");
        activity.runOnUiThread(() -> binding.textViewMultimedia.setText(message));
    }

    private void setupSpinner() {
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    private boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent, imagesCount, voicesCount, trackNumber;
        String trackingId;
        double alalMane;
        MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            trackingId = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id;
            trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackingId);
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackingId);
            final ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
            for (int i = 0; i < isManes.size(); i++) {
                mane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackingId);
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
        } else if (type != OFFLINE.getValue() && (imagesCount > 0 || voicesCount > 0)) {
            String message = String.format(getString(R.string.unuploaded_multimedia),
                    imagesCount, voicesCount).concat("\n")
                    .concat(getString(R.string.recommend_multimedia));
            new CustomDialogModel(YellowRedirect, activity, message, getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    private void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == MULTIMEDIA.getValue()) {
                binding.buttonUpload.setEnabled(true);
                new PrepareMultimedia(activity, this, false).execute(activity);
            } else {
                if (checkOnOffLoad()) {
                    sendOnOffLoad();
                }
            }
        });
    }

    private void sendOnOffLoad() {
        if (type == NORMAL.getValue()) {
            binding.buttonUpload.setEnabled(true);
            new PrepareOffLoad(activity,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id, this)
                    .execute(activity);
        } else if (type == OFFLINE.getValue()) {
            new PrepareOffLoadOffline(activity,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id)
                    .execute(activity);
        }
    }

    public void setButtonState() {
        binding.buttonUpload.setEnabled(true);
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
            sendOnOffLoad();
        }
    }
}