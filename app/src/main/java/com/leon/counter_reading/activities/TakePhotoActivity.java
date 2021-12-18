package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.Constants.PHOTO_PERMISSIONS;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;
import static com.leon.counter_reading.utils.CustomFile.compressBitmap;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.utils.PermissionManager.checkCameraPermission;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.ImageViewAdapter;
import com.leon.counter_reading.databinding.ActivityTakePhotoBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.photo.PrepareMultimedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TakePhotoActivity extends AppCompatActivity {
    public static int replace = 0;
    private boolean result;
    private String uuid;
    private int position, trackNumber;
    private Activity activity;
    private ActivityTakePhotoBinding binding;
    private ImageViewAdapter imageViewAdapter;
//    private Uri photoUri;
    private String path;
    private final ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onActivitySetTheme(this, getApplicationComponent()
                        .SharedPreferenceModel().getIntData(SharedReferenceKeys.THEME_STABLE.getValue()),
                true);
        super.onCreate(savedInstanceState);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));

        activity = this;
        if (checkCameraPermission(getApplicationContext()))
            initialize();
        else askCameraPermission();
    }

    private void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
            trackNumber = getIntent().getExtras().getInt(BundleEnum.TRACKING.getValue());
            result = getIntent().getExtras().getBoolean(BundleEnum.IMAGE.getValue());
            binding.textViewNotSent.setVisibility(getIntent().getExtras()
                    .getBoolean(BundleEnum.SENT.getValue()) ? View.GONE : View.VISIBLE);
            getIntent().getExtras().clear();
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
                images.get(i).bitmap = CustomFile.loadImage(activity, images.get(i).address);
            }
        }
        imageViewAdapter = new ImageViewAdapter(activity, images);
        binding.gridViewImage.setAdapter(imageViewAdapter);
    }

    private void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v -> {
            binding.buttonSaveSend.setEnabled(false);
            new PrepareMultimedia(activity, position, result,
                    binding.editTextDescription.getText().toString(), images).execute(activity);
        });
    }

    private void askCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                initialize();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    public void openSomeActivityForResult() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
//                photoUri = FileProvider.getUriForFile(activity,
//                        BuildConfig.APPLICATION_ID.concat(".provider"), photoFile);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                path = photoFile.getPath();
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID.concat(".provider"), photoFile));
                someActivityResultLauncher.launch(cameraIntent);
            }
        }

//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_URI);
//        someActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
//                        prepareImage((Bitmap) result.getData().getExtras().get("data"));
                        prepareImage(result.getData());
                        result.getData().getExtras().clear();
                    } else {
                        prepareImage();
                    }
                    imageViewAdapter.notifyDataSetChanged();
                    binding.buttonSaveSend.setEnabled(true);
                }
            });


    private void prepareImage() {
        final Image image = new Image();
        try {
            image.bitmap = compressBitmap(BitmapFactory.decodeFile(path));
//            image.bitmap = compressBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri));
//            image.bitmap = compressBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(path)));
            image.OnOffLoadId = uuid;
            image.trackNumber = trackNumber;
            if (replace > 0) {
                getApplicationComponent().MyDatabase().imageDao()
                        .deleteImage(images.get(replace - 1).id);
                images.set(replace - 1, image);
            } else {
                images.add(image);
            }
//            photoUri = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }
    }

    private void prepareImage(final Intent data) {
        final Image image = new Image();
        try {
            image.bitmap = compressBitmap(BitmapFactory.decodeStream(this.getContentResolver().
                    openInputStream(data.getData())));
            image.OnOffLoadId = uuid;
            image.trackNumber = trackNumber;
            if (replace > 0) {
                getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(replace - 1).id);
                images.set(replace - 1, image);
            } else {
                images.add(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void prepareImage(final Bitmap bitmap) {
        final Image image = new Image();
        image.bitmap = bitmap;
        image.OnOffLoadId = uuid;
        image.trackNumber = trackNumber;
        if (replace > 0) {
            getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(replace - 1).id);
            images.set(replace - 1, image);
        } else {
            images.add(image);
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
        images.clear();
        binding = null;
        super.onDestroy();
    }
}