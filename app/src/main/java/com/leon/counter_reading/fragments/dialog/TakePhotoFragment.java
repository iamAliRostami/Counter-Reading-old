package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.COUNTER_HAS_IMAGE;
import static com.leon.counter_reading.enums.BundleEnum.IMAGE;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.REPORT_HAS_IMAGE;
import static com.leon.counter_reading.enums.BundleEnum.SENT;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.BundleEnum.TRACK_HAS_IMAGE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.helpers.Constants.CURRENT_IMAGE_SIZE;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.gallerySelector;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;
import static com.leon.counter_reading.utils.CustomFile.saveTempBitmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ImageViewAdapter;
import com.leon.counter_reading.databinding.FragmentTakePhotoBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.photo.PrepareMultimedia;

import java.util.ArrayList;

public class TakePhotoFragment extends DialogFragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, View.OnClickListener {
    private final ArrayList<Image> images = new ArrayList<>();
    private FragmentTakePhotoBinding binding;
    private ImageViewAdapter imageViewAdapter;
    private Callback readingActivity;
    private int replace = 0, position, trackNumber;
    private String uuid;
    private boolean result, counterHasImage, reportHasImage, trackHasImage;
    private long lastClickTime = 0;
    private Uri uri;

    public TakePhotoFragment() {
    }

    public static TakePhotoFragment newInstance(boolean sent, String uuid, int trackingNumber,
                                                int position, boolean image, boolean counterHasImage,
                                                boolean reportHasImage, boolean trackHasImage) {
        TakePhotoFragment fragment = newInstance(sent, uuid, trackingNumber);
        Bundle args;
        if (fragment.getArguments() != null)
            args = fragment.getArguments();
        else args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        args.putBoolean(IMAGE.getValue(), image);
        args.putBoolean(COUNTER_HAS_IMAGE.getValue(), counterHasImage);
        args.putBoolean(REPORT_HAS_IMAGE.getValue(), reportHasImage);
        args.putBoolean(TRACK_HAS_IMAGE.getValue(), trackHasImage);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    public static TakePhotoFragment newInstance(boolean sent, String uuid, int trackingNumber) {
        TakePhotoFragment fragment = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putBoolean(SENT.getValue(), sent);
        args.putString(BILL_ID.getValue(), uuid);
        args.putInt(TRACKING.getValue(), trackingNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTakePhotoBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        if (getArguments() != null) {
            uuid = getArguments().getString(BILL_ID.getValue());
            position = getArguments().getInt(POSITION.getValue());
            trackNumber = getArguments().getInt(TRACKING.getValue());
            result = getArguments().getBoolean(IMAGE.getValue());
            counterHasImage = getArguments().getBoolean(COUNTER_HAS_IMAGE.getValue());
            reportHasImage = getArguments().getBoolean(REPORT_HAS_IMAGE.getValue());
            trackHasImage = getArguments().getBoolean(TRACK_HAS_IMAGE.getValue());
            binding.textViewNotSent.setVisibility(getArguments().getBoolean(SENT.getValue()) ?
                    View.GONE : View.VISIBLE);
            getArguments().clear();
        }
        if (gallerySelector())
            binding.checkBoxGallery.setVisibility(View.GONE);
        imageGridViewSetup();
        binding.buttonSaveSend.setOnClickListener(this);
    }

    private void imageGridViewSetup() {
        images.clear();
        if (!result) {
            images.addAll(getApplicationComponent().MyDatabase().imageDao().getImagesByOnOffLoadId(uuid));
        }
        if (isAdded() && getContext() != null) {
            imageViewAdapter = new ImageViewAdapter(getContext(), images);
            binding.gridViewImage.setAdapter(imageViewAdapter);
        }
        binding.gridViewImage.setOnItemClickListener(this);
        binding.gridViewImage.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
        lastClickTime = SystemClock.elapsedRealtime();
        replace = imageViewAdapter.setReplace(position);
        if (replace >= 0)
            openResourceForResult();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_save_send) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            if (!checkImageForce() && isAdded() && getContext() != null) {
                try {
                    new PrepareMultimedia(getContext(), images, binding.editTextDescription.getText().toString(),
                            getTag(), result).execute(requireActivity());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkImageForce() {
        if (images.isEmpty()) {
            if (counterHasImage)
                new CustomToast().error("در این وضعیت کنتور الصاق عکس الزامی است", Toast.LENGTH_LONG);
            if (reportHasImage)
                new CustomToast().error("گزارشات کنتور نیازمند الصاق عکس است", Toast.LENGTH_LONG);
            if (trackHasImage)
                new CustomToast().error(String.format("شماره پیگیری %s نیازمند الصاق عکس است", trackNumber), Toast.LENGTH_LONG);
            return counterHasImage || reportHasImage || trackHasImage;
        }
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        imageViewAdapter.showImageHighQuality(position);
        return true;
    }

    private void openResourceForResult() {
        if (binding.checkBoxGallery.isChecked())
            openGalleryForResult();
        else openCameraForResult();
    }

    private void openGalleryForResult() {
        if (isAdded() && getContext() != null) {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryResultLauncher.launch(galleryIntent);
        } else {
            new CustomToast().error("صفحه ی عکس را بسته و مجددا باز کنید.", Toast.LENGTH_LONG);
        }
    }

    private void openCameraForResult() {
        if (isAdded() && getContext() != null) {
            uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID
                    .concat(".FileProvider"), createImageFile(getContext()));
            cameraResultLauncher.launch(uri);
        } else {
            new CustomToast().error("صفحه ی عکس را بسته و مجددا باز کنید.", Toast.LENGTH_LONG);
        }
    }

    private final ActivityResultLauncher<Uri> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), o -> {
                if (o) {
                    if (isAdded() && getContext() != null) prepareImage(false);
                    else {
                        new CustomToast().error("صفحه ی عکس را بسته و مجددا باز کنید.", Toast.LENGTH_LONG);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null &&
                        result.getData().getData() != null) {
                    if (isAdded() && getContext() != null) {
                        uri = result.getData().getData();
                        prepareImage(true);
                    } else {
                        new CustomToast().error("صفحه ی عکس را بسته و مجددا باز کنید.", Toast.LENGTH_LONG);
                    }
                }
            });


    private void prepareImage(boolean isGallery) {
        try {
            final Image image = new Image();
            image.address = saveTempBitmap(uri, getContext(), isGallery);
            if (CURRENT_IMAGE_SIZE > 0) {
                image.size = CURRENT_IMAGE_SIZE;
                image.OnOffLoadId = uuid;
                image.trackNumber = trackNumber;
                image.isGallery = isGallery;
                if (replace > 0) {
                    getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(replace - 1).id);
                    images.set(replace - 1, image);
                } else images.add(image);
                imageViewAdapter.notifyDataSetChanged();
                binding.buttonSaveSend.setEnabled(true);
            } else new CustomToast().error("مجددا تلاش کنید.");
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void setResult() {
        try {
            dismiss();
            if (result)
                readingActivity.setPhotoResult(position);
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            readingActivity.updateOnOffLoadByAttempt(position, true);
            new CustomDialogModel(Red, requireContext(), R.string.refresh_page, R.string.dear_user,
                    R.string.take_screen_shot, R.string.accepted);
        }
        super.onResume();
    }

    public interface Callback {
        void setPhotoResult(int position);

        void updateOnOffLoadByAttempt(int position, boolean... booleans);
    }
}