package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.IMAGE;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.SENT;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.helpers.Constants.CURRENT_IMAGE_SIZE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.compressBitmap;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.leon.counter_reading.adapters.ImageViewAdapter;
import com.leon.counter_reading.databinding.FragmentTakePhotoBinding;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.photo.PrepareMultimedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TakePhotoFragment extends DialogFragment {
    public static int replace = 0;
    public Callback readingActivity;
    private FragmentTakePhotoBinding binding;
    private ImageViewAdapter imageViewAdapter;
    private String uuid;
    private String path;
    private boolean result;
    private int position, trackNumber;
    private final ArrayList<Image> images = new ArrayList<>();
    private static TakePhotoFragment instance;

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
            binding.textViewNotSent.setVisibility(getArguments()
                    .getBoolean(SENT.getValue()) ? View.GONE : View.VISIBLE);
            getArguments().clear();
        }
        imageSetup();
        setOnButtonSendClickListener();
    }

    private void imageSetup() {
        images.clear();
        if (!result) {
            images.addAll(getApplicationComponent().MyDatabase()
                    .imageDao().getImagesByOnOffLoadId(uuid));
            for (int i = 0; i < images.size(); i++) {
                images.get(i).bitmap = CustomFile.loadImage(requireContext(), images.get(i).address);
            }
        }
        imageViewAdapter = new ImageViewAdapter(requireContext(), images);
        binding.gridViewImage.setAdapter(imageViewAdapter);
    }

    private void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v -> {
            binding.buttonSaveSend.setEnabled(false);
            new PrepareMultimedia(requireActivity(), position, result,
                    binding.editTextDescription.getText().toString(), images).execute(requireActivity());
        });
    }

    public void openSomeActivityForResult() {
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(requireContext());
            } catch (IOException e) {
                new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
            }
            if (photoFile != null) {
                try {
                    path = photoFile.getPath();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(requireContext(),
                            BuildConfig.APPLICATION_ID.concat(".provider"), photoFile));
                    someActivityResultLauncher.launch(cameraIntent);
                } catch (Exception e) {
                    new CustomToast().error(e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    prepareImage();
                    binding.buttonSaveSend.setEnabled(true);
                }
                imageViewAdapter.notifyDataSetChanged();
            });

    private void prepareImage() {
        final Image image = new Image();
        try {
            image.bitmap = compressBitmap(BitmapFactory.decodeFile(path));
            if (image.bitmap != null) {
                image.size = CURRENT_IMAGE_SIZE;
            }
            image.OnOffLoadId = uuid;
            image.trackNumber = trackNumber;
            if (replace > 0) {
                getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(replace - 1).id);
                images.set(replace - 1, image);
            } else {
                images.add(image);
            }
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
        if (getDialog() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }

    public void setResult() {
        dismiss();
        if (result)
            readingActivity.setPhotoResult(position);
    }

    public interface Callback {
        void setPhotoResult(int position);
    }
}