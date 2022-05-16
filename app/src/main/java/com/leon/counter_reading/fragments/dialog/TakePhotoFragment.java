package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.IMAGE;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.SENT;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.CompanyNames.TSE;
import static com.leon.counter_reading.helpers.Constants.CURRENT_IMAGE_SIZE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;
import static com.leon.counter_reading.utils.CustomFile.saveTempBitmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.ImageViewAdapter;
import com.leon.counter_reading.databinding.FragmentTakePhotoBinding;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.photo.PrepareMultimedia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TakePhotoFragment extends DialogFragment {
    public static int replace = 0;
    private static TakePhotoFragment instance;
    private FragmentTakePhotoBinding binding;
    private ImageViewAdapter imageViewAdapter;
    private int position, trackNumber;
    private String uuid, path;
    private boolean result;
    private final ArrayList<Image> images = new ArrayList<>();

    public TakePhotoFragment() {
    }

    public static TakePhotoFragment newInstance(boolean sent, String uuid, int trackingNumber,
                                                int position, boolean image) {
        instance = newInstance(sent, uuid, trackingNumber);
        final Bundle args;
        if (instance.getArguments() != null)
            args = instance.getArguments();
        else args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        args.putBoolean(IMAGE.getValue(), image);
        instance.setArguments(args);
        instance.setCancelable(false);
        return instance;
    }

    public static TakePhotoFragment newInstance(boolean sent, String uuid, int trackingNumber) {
        instance = new TakePhotoFragment();
        final Bundle args = new Bundle();
        args.putBoolean(SENT.getValue(), sent);
        args.putString(BILL_ID.getValue(), uuid);
        args.putInt(TRACKING.getValue(), trackingNumber);
        instance.setArguments(args);
        return instance;
    }

    public static TakePhotoFragment newInstance() {
        return instance;
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
            binding.textViewNotSent.setVisibility(getArguments().getBoolean(SENT.getValue()) ?
                    View.GONE : View.VISIBLE);
            getArguments().clear();
        }
        if (BuildConfig.COMPANY_NAME == TSE)
            binding.checkBoxGallery.setVisibility(View.VISIBLE);
        imageSetup();
        setOnButtonSendClickListener();
    }

    private void imageSetup() {
        images.clear();
        if (!result) {
            images.addAll(getApplicationComponent().MyDatabase().imageDao().getImagesByOnOffLoadId(uuid));
        }
        imageViewAdapter = new ImageViewAdapter(requireContext(), images);
        binding.gridViewImage.setAdapter(imageViewAdapter);
    }

    private void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v -> {
            binding.buttonSaveSend.setEnabled(false);
            new PrepareMultimedia(images, binding.editTextDescription.getText().toString(), result,
                    requireActivity()).execute(requireActivity());
        });
    }

    public void openResourceForResult() {
//        Log.e("checkBoxGallery", String.valueOf(binding.checkBoxGallery.isChecked()));
        if (binding.checkBoxGallery.isChecked()) openGalleryForResult();
        else openCameraForResult();
    }

    private void openGalleryForResult() {
        final Intent galleryIntent = new Intent("android.intent.action.PICK");
        if (galleryIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            galleryIntent.setType("image/*");
            galleryResultLauncher.launch(galleryIntent);
        }
    }

    private void openCameraForResult() {
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getContext() != null && cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(getContext());
            } catch (IOException e) {
                new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
            }
            if (photoFile != null) {
                try {
                    path = photoFile.getPath();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID.concat(".provider"), photoFile));
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    cameraResultLauncher.launch(cameraIntent);
                } catch (Exception e) {
                    new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        } else {
            new CustomToast().error("به صفحه ی عکس را بسته و مجددا باز کنید.", Toast.LENGTH_LONG);
        }
    }

    private final ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) prepareImage();
                imageViewAdapter.notifyDataSetChanged();
                binding.buttonSaveSend.setEnabled(true);
            });

    private final ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null &&
                        result.getData().getData() != null) {
                    try {
                        final InputStream inputStream = requireContext().getContentResolver()
                                .openInputStream(result.getData().getData());
                        final Image image = new Image();
                        image.address = saveTempBitmap(inputStream, requireContext());
                        prepareImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                imageViewAdapter.notifyDataSetChanged();
                binding.buttonSaveSend.setEnabled(true);
            });

    private void prepareImage(Image image) {
        try {
//            image.address = saveTempBitmap(path, requireContext());
            image.size = CURRENT_IMAGE_SIZE;
            image.OnOffLoadId = uuid;
            image.trackNumber = trackNumber;
            if (replace > 0) {
                getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(replace - 1).id);
                images.set(replace - 1, image);
            } else images.add(image);
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void prepareImage() {
        final Image image = new Image();
        try {
            image.address = saveTempBitmap(path, requireContext());
            image.size = CURRENT_IMAGE_SIZE;
            image.OnOffLoadId = uuid;
            image.trackNumber = trackNumber;
            if (replace > 0) {
                getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(replace - 1).id);
                images.set(replace - 1, image);
            } else images.add(image);
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public void setResult() {
        try {
            dismiss();
            if (result)
                ((ReadingActivity) requireActivity()).setPhotoResult(position);
        } catch (Exception e) {
            new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }

//    private Callback readingActivity;
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof Activity) readingActivity = (Callback) context;
//    }
//    public interface Callback {
//        void setPhotoResult(int position);
//    }
}