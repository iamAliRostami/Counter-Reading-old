package com.leon.counter_reading.fragments.upload;

import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.YellowRedirect;
import static com.leon.counter_reading.enums.UploadType.MULTIMEDIA;
import static com.leon.counter_reading.enums.UploadType.NORMAL;
import static com.leon.counter_reading.enums.UploadType.OFFLINE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.adapters.SpinnerAdapter;
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

public class UploadFragment extends Fragment implements View.OnClickListener {
    private int type;
    private String[] items;
    private long lastClickTime = 0;
    private FragmentUploadBinding binding;
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private final int[] imageSrc = {R.drawable.img_upload_on, R.drawable.img_upload_off,
            R.drawable.img_multimedia};

    public static UploadFragment newInstance(int type) {
        final UploadFragment fragment = new UploadFragment();
        final Bundle args = new Bundle();
        args.putInt(TYPE.getValue(), type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE.getValue());
            getArguments().clear();
        }
        trackingDtos.clear();
        trackingDtos.addAll(((UploadActivity) requireActivity()).getTrackingDtos());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) savedInstanceState.clear();
        initialize();
    }

    private void initialize() {
        if (type == MULTIMEDIA.getValue()) {
            binding.spinner.setVisibility(View.GONE);
            binding.textViewMultimedia.setVisibility(View.VISIBLE);
            setMultimediaInfo();
        } else {
            items = TrackingDto.getTrackingDtoItems(trackingDtos, getString(R.string.select_one));
            setupSpinner();
        }
        if (type != OFFLINE.getValue())
            binding.textViewOfflineWarning.setVisibility(View.GONE);
        binding.imageViewUpload.setImageResource(imageSrc[type > -1 ? type : 0]);
        binding.buttonUpload.setOnClickListener(this);
    }

    public void setMultimediaInfo() {
        final int imagesCount = getApplicationComponent().MyDatabase().imageDao().getUnsentImageCount(false);
        final int voicesCount = getApplicationComponent().MyDatabase().voiceDao().getUnsentVoiceCount(false);
        final int imagesSize = getApplicationComponent().MyDatabase().imageDao().getUnsentImageSize(false);
        final int voicesSize = getApplicationComponent().MyDatabase().voiceDao().getUnsentVoiceSizes(false);
        final String message = "تعداد عکس: ".concat(String.valueOf(imagesCount))
                .concat(" *** حجم: ").concat(String.valueOf(imagesSize)).concat(" KB").concat("\n")
                .concat("تعداد صدا: ").concat(String.valueOf(voicesCount))
                .concat(" *** حجم: ").concat(String.valueOf(voicesSize)).concat(" KB");
        requireActivity().runOnUiThread(() -> binding.textViewMultimedia.setText(message));
    }

    private void setupSpinner() {
        final SpinnerAdapter adapter = new SpinnerAdapter(requireContext(), items);
        binding.spinner.setAdapter(adapter);
    }

    private boolean checkOnOffLoad() {
        int mane = 0,/*todo*/ unread, alalPercent, imagesCount, voicesCount;
        double alalMane;
        final MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            final String trackingId = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id;
            final int trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            //TODO
//            final int total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackingId,);

            final int total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackingId,
                    myDatabase.counterStateDao().getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));


            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackingId);
            //TODO
    //            final ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
//                    getCounterStateDtosIsMane(true,
//                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
    //            for (int i = 0; i < isManes.size(); i++) {
//                mane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackingId);
    //            }
    //            Log.e("mane 1", String.valueOf(mane));
    //            Log.e("mane 2", String.valueOf(myDatabase.onOffLoadDao().getOnOffLoadIsManeCount1(isManes, trackingId)));
            mane = myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId),
                    trackingId);


            alalPercent = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).alalHesabPercent;
            alalMane = (double) mane / total * 100;
            imagesCount = myDatabase.imageDao().getUnsentImageCountByTrackNumber(trackNumber, false);
            voicesCount = myDatabase.voiceDao().getUnsentVoiceCountByTrackNumber(trackNumber, false);
        } else return false;
        if (unread > 0) {
            String message = String.format(getString(R.string.unread_number), unread);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (mane > 0 && alalMane > (double) alalPercent) {
            String message = String.format(getString(R.string.darsad_alal_1),
                    alalPercent, new DecimalFormat("###.##").format(alalMane), mane);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (!trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).isRepeat &&
                (type != OFFLINE.getValue() && (imagesCount > 0 || voicesCount > 0))) {
            String message = String.format(getString(R.string.unuploaded_multimedia),
                            imagesCount, voicesCount).concat("\n")
                    .concat(getString(R.string.recommend_multimedia));
            new CustomDialogModel(YellowRedirect, requireContext(), message, getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.button_upload) {
            if (type == MULTIMEDIA.getValue()) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
                lastClickTime = SystemClock.elapsedRealtime();
                new PrepareMultimedia(requireActivity(), this, false).execute(requireActivity());
            } else {
                if (checkOnOffLoad()) {
                    sendOnOffLoad();
                }
            }
        }
    }

    private void sendOnOffLoad() {
        if (type == NORMAL.getValue()) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            new PrepareOffLoad(requireActivity(),
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id)
                    .execute(requireActivity());
        } else if (type == OFFLINE.getValue()) {
            new PrepareOffLoadOffline(requireActivity(),
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id)
                    .execute(requireActivity());
        }
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