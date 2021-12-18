package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.Constants.CAMERA_REQUEST;
import static com.leon.counter_reading.helpers.Constants.GALLERY_REQUEST;
import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.Constants.LOCATION_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.PHOTO_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.PHOTO_URI;
import static com.leon.counter_reading.helpers.Constants.REQUEST_NETWORK_CODE;
import static com.leon.counter_reading.helpers.Constants.REQUEST_WIFI_CODE;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;
import static com.leon.counter_reading.utils.CustomFile.compressBitmap;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityReportForbidBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.fragments.dialog.HighQualityFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.forbid.PrepareForbid;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReportForbidActivity extends AppCompatActivity {
    private ActivityReportForbidBinding binding;
    private Activity activity;
    private ForbiddenDto forbiddenDto = new ForbiddenDto();
    private int zoneId;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.onActivitySetTheme(this, MyApplication.getApplicationComponent()
                        .SharedPreferenceModel().getIntData(SharedReferenceKeys.THEME_STABLE.getValue()),
                true);
        super.onCreate(savedInstanceState);
        binding = ActivityReportForbidBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        checkPermissions();
    }

    private void checkPermissions() {
        if (PermissionManager.gpsEnabled(this))
            if (!PermissionManager.checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (!PermissionManager.checkCameraPermission(getApplicationContext())) {
                askCameraPermission();
            } else {
                initialize();
            }
    }

    private void askLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(LOCATION_PERMISSIONS).check();
    }

    private void askCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(PHOTO_PERMISSIONS).check();
    }

    private void initialize() {
        if (getIntent().getExtras() != null) {
            zoneId = getIntent().getExtras().getInt(BundleEnum.ZONE_ID.getValue());
            getIntent().getExtras().clear();
        }
        binding.textViewHome.setText(getString(R.string.number).concat(DifferentCompanyManager
                .getAhad(DifferentCompanyManager.getActiveCompanyName())));

        binding.editTextNextAccount.setFilters(new InputFilter[]{new InputFilter
                .LengthFilter(DifferentCompanyManager.getEshterakMaxLength(DifferentCompanyManager
                .getActiveCompanyName()))});
        binding.editTextPreAccount.setFilters(new InputFilter[]{new InputFilter
                .LengthFilter(DifferentCompanyManager.getEshterakMaxLength(DifferentCompanyManager
                .getActiveCompanyName()))});

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
                if (s.toString().length() == DifferentCompanyManager.
                        getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName())) {
                    View view = binding.editTextNextAccount;
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
                if (s.toString().length() == DifferentCompanyManager.
                        getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName())) {
                    View view = binding.editTextPostalCode;
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
                    View view = binding.editTextAhadNumber;
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
            View view = null;
            boolean cancel = false;
            if (binding.editTextPreAccount.getText().length() < DifferentCompanyManager.
                    getEshterakMinLength(DifferentCompanyManager.getActiveCompanyName())) {
                binding.editTextPreAccount.setError(getString(R.string.error_format));
                view = binding.editTextPreAccount;
                cancel = true;
            } else if (binding.editTextNextAccount.getText().length() < DifferentCompanyManager.
                    getEshterakMinLength(DifferentCompanyManager.getActiveCompanyName())) {
                binding.editTextNextAccount.setError(getString(R.string.error_format));
                view = binding.editTextNextAccount;
                cancel = true;
            } else if (binding.editTextPostalCode.getText().length() > 0 &&
                    binding.editTextPostalCode.getText().length() < 10) {
                binding.editTextPostalCode.setError(getString(R.string.error_format));
                view = binding.editTextPostalCode;
                cancel = true;
            } else if (binding.editTextAhadNumber.getText().toString().isEmpty()) {
                binding.editTextAhadNumber.setError(getString(R.string.error_empty));
                view = binding.editTextAhadNumber;
                cancel = true;
            } else if (binding.editTextDescription.getText().toString().isEmpty()) {
                binding.editTextDescription.setError(getString(R.string.error_empty));
                view = binding.editTextDescription;
                cancel = true;
            }
            if (!cancel)
                sendForbid();
            else view.requestFocus();
        });
    }

    private void sendForbid() {
        double latitude = 0, longitude = 0, accuracy = 0;
        if (getLocationTracker(activity).getCurrentLocation() != null) {
            latitude = getLocationTracker(activity).getCurrentLocation().getLatitude();
            longitude = getLocationTracker(activity).getCurrentLocation().getLatitude();
            accuracy = getLocationTracker(activity).getCurrentLocation().getLatitude();
        }
        forbiddenDto.prepareToSend(accuracy, longitude, latitude,
                binding.editTextPostalCode.getText().toString(),
                binding.editTextDescription.getText().toString(),
                binding.editTextPreAccount.getText().toString(),
                binding.editTextNextAccount.getText().toString(),
                binding.editTextAhadNumber.getText().toString(), zoneId);
        new PrepareForbid(activity, forbiddenDto, zoneId).execute(activity);
    }

    private void setOnImageViewTakenClickListener() {
        binding.imageViewTaken.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HighQualityFragment highQualityFragment =
                    HighQualityFragment.newInstance(forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
            highQualityFragment.show(fragmentTransaction, "Image # 2");
        });
    }

    private void setOnButtonPhotoClickListener() {
        binding.buttonPhoto.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReportForbidActivity.this, R.style.AlertDialogCustom));
            builder.setTitle(R.string.choose_document);
            builder.setMessage(R.string.select_source);
            builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            });
            builder.setNegativeButton(R.string.camera, (dialog, which) -> {
                dialog.dismiss();
                openSomeActivityForResult();
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
//                    // Create the File where the photo should go
//                    File photoFile = null;
//                    try {
//                        photoFile = createImageFile(activity);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    // Continue only if the File was successfully created
//                    if (photoFile != null) {
//                        PHOTO_URI = FileProvider.getUriForFile(activity,
//                                BuildConfig.APPLICATION_ID.concat(".provider"),
//                                photoFile);
//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_URI);
//                        try {
//                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                        } catch (ActivityNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            });
            builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    public void openSomeActivityForResult() {
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                path = photoFile.getPath();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID.concat(".provider"), photoFile));
                someActivityResultLauncher.launch(cameraIntent);
            }
        }
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    final Bitmap bitmap = compressBitmap(BitmapFactory.decodeFile(path));
                    forbiddenDto.bitmaps.add(bitmap);
                    binding.relativeLayoutImage.setVisibility(View.VISIBLE);
                    binding.imageViewTaken.setImageBitmap(bitmap);
                    forbiddenDto.File.add(CustomFile.bitmapToFile(bitmap, activity));

                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == GPS_CODE)
                checkPermissions();
            if (requestCode == REQUEST_NETWORK_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.setMobileWifiEnabled(this);
            }
            if (requestCode == REQUEST_WIFI_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.enableNetwork(this);
            }
        }
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST && data != null) {
                try {
                    final InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    bitmap = compressBitmap(BitmapFactory.decodeStream(inputStream));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST) {
                try {
                    bitmap = compressBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), PHOTO_URI));
                    PHOTO_URI = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null) {
                forbiddenDto.bitmaps.add(bitmap);
                binding.relativeLayoutImage.setVisibility(View.VISIBLE);
                binding.imageViewTaken.setImageBitmap(bitmap);
                forbiddenDto.File.add(CustomFile.bitmapToFile(bitmap, activity));
            }
        }
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        forbiddenDto = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}