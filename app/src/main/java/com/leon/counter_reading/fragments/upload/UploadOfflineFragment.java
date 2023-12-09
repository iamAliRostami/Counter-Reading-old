package com.leon.counter_reading.fragments.upload;

import static com.leon.counter_reading.enums.DialogType.YellowRedirect;
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
import com.leon.counter_reading.adapters.SpinnerAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.uploading.PrepareOffLoadOffline;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UploadOfflineFragment extends Fragment {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private FragmentUploadBinding binding;
    private Activity activity;
    private String[] items;

    public static UploadOfflineFragment newInstance() {
        return new UploadOfflineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        trackingDtos.clear();
        trackingDtos.addAll(((UploadActivity) activity).getTrackingDtos());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }


    private void initialize() {
        items = TrackingDto.getTrackingDtoItems(trackingDtos, getString(R.string.select_one));
        setupSpinner();
        binding.imageViewUpload.setImageResource(R.drawable.img_upload_off);
        setOnButtonUploadClickListener();
    }

    private void setupSpinner() {
        SpinnerAdapter adapter = new SpinnerAdapter(activity, items);
        binding.spinner.setAdapter(adapter);
    }

    private boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent, imagesCount, voicesCount, trackNumber;
        String trackingId;
        double alalMane;
        MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            trackingId = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id;
            //TODO
    //            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackingId);
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackingId,
                    myDatabase.counterStateDao().getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackingId);
//            ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
//                    getCounterStateDtosIsMane(true,
//                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
//            for (int i = 0; i < isManes.size(); i++) {
//                mane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackingId);
//            }
            mane = myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(myDatabase.counterStateDao().
                            getCounterStateDtosIsMane(true,
                                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId),
                    trackingId);
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
            new CustomDialogModel(YellowRedirect, activity, message, getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    private void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (checkOnOffLoad())
                sendOnOffLoad();
        });
    }

    private void sendOnOffLoad() {
        new PrepareOffLoadOffline(activity,
                trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id)
                .execute(activity);
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