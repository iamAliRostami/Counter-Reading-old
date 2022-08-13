package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.ZONE_ID;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.Converters.bitmapToFile;
import static com.leon.counter_reading.utils.CustomFile.compressBitmap;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getEshterakMaxLength;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
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

public class ReportForbidFragment extends DialogFragment {
    private FragmentReportForbidBinding binding;
    private final ForbiddenDto forbiddenDto = new ForbiddenDto();
    private String path;
    private int zoneId;
    private final ActivityResultLauncher<Intent> galleryResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null &&
                                result.getData().getData() != null) {
                            try {
                                final InputStream inputStream = requireContext().getContentResolver()
                                        .openInputStream(result.getData().getData());
                                addImage(compressBitmap(BitmapFactory.decodeStream(inputStream)));
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
        final ReportForbidFragment fragment = new ReportForbidFragment();
        final Bundle args = new Bundle();
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
        binding.textViewHome.setText(getString(R.string.number).concat(getAhad(getActiveCompanyName())));
        binding.editTextNextAccount.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getEshterakMaxLength(getActiveCompanyName()))});
        binding.editTextPreAccount.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getEshterakMaxLength(getActiveCompanyName()))});
        forbiddenDto.File = new ArrayList<>();
        forbiddenDto.bitmaps = new ArrayList<>();
        setOnButtonPhotoClickListener();
        setOnButtonSubmitClickListener();
        setOnImageViewDeleteClickListener();
        setOnImageViewTakenClickListener();
        setOnEditTextChangeListener();
    }

    private void setOnEditTextChangeListener() {
        binding.editTextPreAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == getEshterakMaxLength(getActiveCompanyName())) {
                    final View view = binding.editTextNextAccount;
                    view.requestFocus();
                }
            }
        });
        binding.editTextNextAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == getEshterakMaxLength(getActiveCompanyName())) {
                    final View view = binding.editTextPostalCode;
                    view.requestFocus();
                }
            }
        });
        binding.editTextPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 10) {
                    final View view = binding.editTextAhadNumber;
                    view.requestFocus();
                }
            }
        });
    }

    private void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete.setOnClickListener(v -> {
            forbiddenDto.File.remove(forbiddenDto.File.size() - 1);
            forbiddenDto.bitmaps.remove(forbiddenDto.bitmaps.size() - 1);
            if (forbiddenDto.File.size() > 0) {
                binding.imageViewTaken.setImageBitmap(
                        forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
            } else {
                binding.relativeLayoutImage.setVisibility(View.GONE);
            }
        });
    }

    private void setOnButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            boolean cancel = false;
            if (binding.radioButtonActivate.isChecked()) {
                if (binding.editTextPreAccount.getText().toString().isEmpty()) {
                    binding.editTextPreAccount.setError(getString(R.string.error_empty));
                    binding.editTextPreAccount.requestFocus();
                    cancel = true;
                } else if (binding.editTextNextAccount.getText().toString().isEmpty()) {
                    binding.editTextNextAccount.setError(getString(R.string.error_empty));
                    binding.editTextNextAccount.requestFocus();
                    cancel = true;
                }
            }
            if (!cancel && binding.editTextPostalCode.getText().length() > 0 &&
                    binding.editTextPostalCode.getText().length() < 10) {
                binding.editTextPostalCode.setError(getString(R.string.error_format));
                binding.editTextPostalCode.requestFocus();
                cancel = true;
            } else if (!cancel && binding.editTextAhadNumber.getText().toString().isEmpty()) {
                binding.editTextAhadNumber.setError(getString(R.string.error_empty));
                binding.editTextAhadNumber.requestFocus();
                cancel = true;
            } else if (!cancel && binding.editTextDescription.getText().toString().isEmpty()) {
                binding.editTextDescription.setError(getString(R.string.error_empty));
                binding.editTextDescription.requestFocus();
                cancel = true;
            }
            if (!cancel)
                sendForbid();
        });
    }

    private int getDigits(String number) {
        if (!TextUtils.isEmpty(number) && TextUtils.isDigitsOnly(number)) {
            return Integer.parseInt(number);
        } else {
            return 0;
        }
    }

    private void sendForbid() {
        double latitude = 0, longitude = 0, accuracy = 0;
        if (getLocationTracker(requireActivity()).getCurrentLocation() != null) {
            latitude = getLocationTracker(requireActivity()).getCurrentLocation().getLatitude();
            longitude = getLocationTracker(requireActivity()).getCurrentLocation().getLongitude();
            accuracy = getLocationTracker(requireActivity()).getCurrentLocation().getAccuracy();
        }
        forbiddenDto.prepareToSend(accuracy, longitude, latitude,
                binding.editTextPostalCode.getText().toString(),
                binding.editTextDescription.getText().toString(),
                binding.editTextPreAccount.getText().toString(),
                binding.editTextNextAccount.getText().toString(),
                getDigits(binding.editTextAhadNumber.getText().toString()),
                zoneId, binding.radioButtonActivate.isChecked());
        new PrepareForbid(requireActivity(), forbiddenDto, zoneId).execute(requireActivity());
        dismiss();
    }

    private void setOnImageViewTakenClickListener() {
        binding.imageViewTaken.setOnClickListener(v ->
                ShowDialogOnce(requireContext(), "Image # 1",
                        HighQualityFragment.newInstance(forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1))));
    }

    private void setOnButtonPhotoClickListener() {
        binding.buttonPhoto.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom));
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
        });
    }

    private void openCameraForResult() {
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(requireContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                path = photoFile.getPath();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(requireContext(),
                        BuildConfig.APPLICATION_ID.concat(".provider"), photoFile));
                cameraResultLauncher.launch(cameraIntent);
            }
        }
    }

    private void openGalleryForResult() {
        final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryResultLauncher.launch(galleryIntent);
    }

    private void addImage(final Bitmap bitmap) {
        forbiddenDto.bitmaps.add(bitmap);
        binding.relativeLayoutImage.setVisibility(View.VISIBLE);
        binding.imageViewTaken.setImageBitmap(bitmap);
        forbiddenDto.File.add(bitmapToFile(bitmap, requireContext()));
    }

    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            new CustomDialogModel(Red, requireContext(), getString(R.string.refresh_page),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted));
        }
        super.onResume();
    }
}