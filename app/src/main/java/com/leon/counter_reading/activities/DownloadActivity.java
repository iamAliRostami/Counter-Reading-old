package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.DownloadType.RETRY;
import static com.leon.counter_reading.enums.DownloadType.SPECIAL;
import static com.leon.counter_reading.helpers.Constants.ALL_FILES_ACCESS_REQUEST;
import static com.leon.counter_reading.helpers.Constants.SETTING_REQUEST;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerTabAdapter;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityDownloadBinding;
import com.leon.counter_reading.enums.DownloadType;
import com.leon.counter_reading.fragments.download.DownloadFragment;
import com.leon.counter_reading.fragments.download.DownloadOfflineFragment;
import com.leon.counter_reading.utils.DepthPageTransformer;

public class DownloadActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {
    private ActivityDownloadBinding binding;
    private int currentState;
    private final ActivityResultLauncher<Intent> allFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> checkAllFilePermission());
    private final ActivityResultLauncher<Intent> settingResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> checkAllFilePermission());

    @Override
    protected void initialize() {
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        setupViewPager();
        initializeTextViews();
    }


    private void checkAllFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager() && Settings.System.canWrite(this)) {
                binding.textViewNoRight.setVisibility(View.GONE);
                binding.linearLayoutHeader.setVisibility(View.VISIBLE);
                setupViewPager();
                initializeTextViews();
            } else {
                initializeTextViewNoRight();
            }
        } else {
            setupViewPager();
            initializeTextViews();
        }
    }

    private void initializeTextViewNoRight() {
        binding.textViewNoRight.setVisibility(View.VISIBLE);
        binding.linearLayoutHeader.setVisibility(View.GONE);
        binding.textViewNoRight.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    askAllFilePermission();
                } else if (!Settings.System.canWrite(getApplicationContext())) {
                    askSettingPermission();
                }
            }
        });
    }

    private void askAllFilePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            allFileResultLauncher.launch(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        }
    }

    private void askSettingPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final Uri uri = Uri.fromParts("package", getPackageName(), null);
            settingResultLauncher.launch(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, uri));
        }
    }

    private void initializeTextViews() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        binding.textViewDownloadNormal.setOnClickListener(this);
        binding.textViewDownloadOff.setOnClickListener(this);
        binding.textViewDownloadSpecial.setOnClickListener(this);
        binding.textViewDownloadRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        setColor();
        final int id = view.getId();
        if (id == R.id.text_view_download_special) {
            binding.textViewDownloadSpecial.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(SPECIAL.getValue());
        } else if (id == R.id.text_view_download_off) {
            binding.textViewDownloadOff.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(DownloadType.OFFLINE.getValue());
        } else if (id == R.id.text_view_download_retry) {
            binding.textViewDownloadRetry.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(RETRY.getValue());
        } else if (id == R.id.text_view_download_normal) {
            binding.textViewDownloadNormal.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(DownloadType.NORMAL.getValue());
        }
        setPadding();
    }

    private void setColor() {
        binding.textViewDownloadOff.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadOff.setTextColor(ContextCompat.getColor(getApplicationContext(),
                R.color.text_color_light));
        binding.textViewDownloadNormal.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadNormal.setTextColor(ContextCompat.getColor(getApplicationContext(),
                R.color.text_color_light));
        binding.textViewDownloadRetry.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadRetry.setTextColor(ContextCompat.getColor(getApplicationContext(),
                R.color.text_color_light));
        binding.textViewDownloadSpecial.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadSpecial.setTextColor(ContextCompat.getColor(getApplicationContext(),
                R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewDownloadNormal.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDownloadOff.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDownloadRetry.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDownloadSpecial.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        adapter.addFragment(DownloadFragment.newInstance(DownloadType.NORMAL.getValue()));
        adapter.addFragment(DownloadFragment.newInstance(RETRY.getValue()));
        adapter.addFragment(DownloadOfflineFragment.newInstance());
        adapter.addFragment(DownloadFragment.newInstance(SPECIAL.getValue()));
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(this);
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            binding.textViewDownloadNormal.callOnClick();
        } else if (position == 1) {
            binding.textViewDownloadRetry.callOnClick();
        } else if (position == 2) {
            binding.textViewDownloadOff.callOnClick();
        } else if (position == 3) {
            binding.textViewDownloadSpecial.callOnClick();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        int currentPage = binding.viewPager.getCurrentItem();
        if (currentPage == 3 || currentPage == 0) {
            int previousState = currentState;
            currentState = state;
            if (previousState == 1 && currentState == 0) {
                binding.viewPager.setCurrentItem(currentPage == 0 ? 3 : 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALL_FILES_ACCESS_REQUEST) {
            if (!Settings.System.canWrite(getApplicationContext())) askSettingPermission();
            else checkAllFilePermission();
        } else if (requestCode == SETTING_REQUEST) {
            checkAllFilePermission();
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
        super.onDestroy();
    }

}