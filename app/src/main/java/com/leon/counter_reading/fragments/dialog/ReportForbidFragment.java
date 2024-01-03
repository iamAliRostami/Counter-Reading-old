package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.ZONE_ID;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getEshterakMaxLength;
import static com.leon.counter_reading.helpers.MyApplication.getDigits;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.Converters.bitmapToFile;
import static com.leon.counter_reading.utils.CustomFile.compressBitmap;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentReportForbidBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.utils.forbid.PrepareForbid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReportForbidFragment extends DialogFragment implements TextWatcher, View.OnClickListener {
    private FragmentReportForbidBinding binding;
    private final ForbiddenDto forbiddenDto = new ForbiddenDto();
    private String path;
    private int zoneId;
    private final ActivityResultLauncher<Intent> galleryResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null
                                && result.getData().getData() != null) {
                            try {
                                if (isAdded() && getContext() != null) {
                                    InputStream inputStream = getContext().getContentResolver()
                                            .openInputStream(result.getData().getData());
                                    addImage(compressBitmap(BitmapFactory.decodeStream(inputStream)));
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
    private final ActivityResultLauncher<Intent> cameraResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            addImage(compressBitmap(BitmapFactory.decodeFile(path)));
                        }
                    });

    public ReportForbidFragment() {
    }

    public static ReportForbidFragment newInstance(int zone) {
        ReportForbidFragment fragment = new ReportForbidFragment();
        Bundle args = new Bundle();
        args.putInt(ZONE_ID.getValue(), zone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            zoneId = getArguments().getInt(ZONE_ID.getValue());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportForbidBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.textViewHome.setText(getString(R.string.number).concat(getAhad()));
        binding.editTextNextAccount.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getEshterakMaxLength())});
        binding.editTextPreAccount.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getEshterakMaxLength())});
        forbiddenDto.File = new ArrayList<>();
        forbiddenDto.bitmaps = new ArrayList<>();
        setOnClickListener();
        setOnEditTextChangeListener();
    }

    private void setOnClickListener() {
        binding.buttonPhoto.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
        binding.imageViewDelete.setOnClickListener(this);
        binding.imageViewTaken.setOnClickListener(this);
    }

    private void setOnEditTextChangeListener() {
        binding.editTextPreAccount.addTextChangedListener(this);
        binding.editTextNextAccount.addTextChangedListener(this);
        binding.editTextPostalCode.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == binding.editTextPreAccount.getEditableText()) {
            if (s.toString().length() == getEshterakMaxLength()) {
                binding.editTextNextAccount.requestFocus();
            }
        } else if (s == binding.editTextNextAccount.getEditableText()) {
            if (s.toString().length() == getEshterakMaxLength()) {
                binding.editTextPostalCode.requestFocus();
            }
        } else if (s == binding.editTextPostalCode.getEditableText()) {
            if (s.toString().length() == 10) {
                binding.editTextAhadNumber.requestFocus();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.image_view_delete) {
            deleteImage();
        } else if (id == R.id.button_submit) {
            if (checkInputs()) sendForbid();
        } else if (id == R.id.button_photo) {
            pickImage();
        } else if (id == R.id.image_view_taken) {
            if (isAdded() && getContext() != null)
                ShowDialogOnce(getContext(), "Image # 1", HighQualityFragment
                        .newInstance(forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1)));
        }
    }

    private void deleteImage() {
        forbiddenDto.File.remove(forbiddenDto.File.size() - 1);
        forbiddenDto.bitmaps.remove(forbiddenDto.bitmaps.size() - 1);
        if (forbiddenDto.File.size() > 0) {
            binding.imageViewTaken.setImageBitmap(forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
        } else {
            binding.relativeLayoutImage.setVisibility(View.GONE);
        }
    }

    private boolean checkInputs() {
        if (binding.radioButtonActivate.isChecked()) {
            if (binding.editTextPreAccount.getText().toString().isEmpty()) {
                binding.editTextPreAccount.setError(getString(R.string.error_empty));
                binding.editTextPreAccount.requestFocus();
                return false;
            } else if (binding.editTextNextAccount.getText().toString().isEmpty()) {
                binding.editTextNextAccount.setError(getString(R.string.error_empty));
                binding.editTextNextAccount.requestFocus();
                return false;
            }
        }
        if (binding.editTextPostalCode.getText().length() > 0 &&
                binding.editTextPostalCode.getText().length() < 10) {
            binding.editTextPostalCode.setError(getString(R.string.error_format));
            binding.editTextPostalCode.requestFocus();
            return false;
        } else if (binding.editTextAhadNumber.getText().toString().isEmpty()) {
            binding.editTextAhadNumber.setError(getString(R.string.error_empty));
            binding.editTextAhadNumber.requestFocus();
            return false;
        } else if (binding.editTextDescription.getText().toString().isEmpty()) {
            binding.editTextDescription.setError(getString(R.string.error_empty));
            binding.editTextDescription.requestFocus();
            return false;
        }
        return true;
    }

    private void sendForbid() {
        double latitude = 0, longitude = 0, accuracy = 0;
        if (isAdded() && getContext() != null) {
            try {
                if (getLocationTracker(requireActivity()).getCurrentLocation() != null) {
                    Location location = getLocationTracker(requireActivity()).getCurrentLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    accuracy = location.getAccuracy();
                }
                forbiddenDto.prepareToSend(accuracy, longitude, latitude,
                        binding.editTextPostalCode.getText().toString(),
                        binding.editTextDescription.getText().toString(),
                        binding.editTextPreAccount.getText().toString(),
                        binding.editTextNextAccount.getText().toString(),
                        getDigits(binding.editTextAhadNumber.getText().toString()),
                        zoneId, binding.radioButtonActivate.isChecked());
                new PrepareForbid(getContext(), forbiddenDto, zoneId).execute(requireActivity());
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            dismiss();
        }
    }

    private void pickImage() {
        if (isAdded() && getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),
                    R.style.AlertDialogCustom));
            builder.setTitle(R.string.choose_document);
            builder.setMessage(R.string.select_source);
            builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
                dialog.dismiss();
                openGalleryForResult();
            });
            builder.setNegativeButton(R.string.camera, (dialog, which) -> {
                dialog.dismiss();
                openCameraForResult();
            });
            builder.create().show();
        }
    }

    private void openCameraForResult() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (isAdded() && getContext() != null &&
                cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            if (isAdded() && getContext() != null) {
                try {
                    try {
                        photoFile = createImageFile(getContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        path = photoFile.getPath();
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(),
                                BuildConfig.APPLICATION_ID.concat(".provider"), photoFile));
                        cameraResultLauncher.launch(cameraIntent);
                    }

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openGalleryForResult() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryResultLauncher.launch(galleryIntent);
    }

    private void addImage(final Bitmap bitmap) {
        forbiddenDto.bitmaps.add(bitmap);
        binding.relativeLayoutImage.setVisibility(View.VISIBLE);
        binding.imageViewTaken.setImageBitmap(bitmap);
        if (isAdded() && getContext() != null)
            forbiddenDto.File.add(bitmapToFile(bitmap, getContext()));
    }

    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            new CustomDialogModel(Red, requireContext(), R.string.refresh_page, R.string.dear_user,
                    R.string.take_screen_shot, R.string.accepted);
        }
        super.onResume();
    }
}