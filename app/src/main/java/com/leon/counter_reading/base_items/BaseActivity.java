package com.leon.counter_reading.base_items;

import static com.leon.counter_reading.di.view_model.MyDatabaseClientModel.migration;
import static com.leon.counter_reading.enums.BundleEnum.THEME;
import static com.leon.counter_reading.enums.SharedReferenceKeys.DISPLAY_NAME;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_STABLE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USER_CODE;
import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.Constants.LOCATION_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.PHOTO_PERMISSIONS;
import static com.leon.counter_reading.helpers.Constants.POSITION;
import static com.leon.counter_reading.helpers.Constants.REQUEST_NETWORK_CODE;
import static com.leon.counter_reading.helpers.Constants.REQUEST_WIFI_CODE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;
import static com.leon.counter_reading.helpers.MyApplication.setActivityComponent;
import static com.leon.counter_reading.utils.PermissionManager.checkCameraPermission;
import static com.leon.counter_reading.utils.PermissionManager.checkLocationPermission;
import static com.leon.counter_reading.utils.PermissionManager.enableGpsForResult;
import static com.leon.counter_reading.utils.PermissionManager.enableMobileWifi;
import static com.leon.counter_reading.utils.PermissionManager.enableNetwork;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.DownloadActivity;
import com.leon.counter_reading.activities.HelpActivity;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.activities.LocationActivity;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.activities.ReadingSettingActivity;
import com.leon.counter_reading.activities.ReportActivity;
import com.leon.counter_reading.activities.SettingActivity;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.adapters.NavigationDrawerAdapter;
import com.leon.counter_reading.adapters.RecyclerItemClickListener;
import com.leon.counter_reading.adapters.items.DrawerItem;
import com.leon.counter_reading.databinding.ActivityBaseBinding;
import com.leon.counter_reading.di.view_model.LocationTrackingGoogle;
import com.leon.counter_reading.di.view_model.LocationTrackingGps;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;
    private Toolbar toolbar;
    private ActivityBaseBinding binding;
    private ISharedPreferenceManager sharedPreferenceManager;
    private boolean exit = false;

    protected abstract void initialize();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        int theme;
        if (getIntent().getExtras() != null) {
            theme = getIntent().getExtras().getInt(THEME.getValue());
            if (theme == 0)
                theme = sharedPreferenceManager.getIntData(THEME_STABLE.getValue());
            getIntent().getExtras().clear();
        } else {
            theme = sharedPreferenceManager.getIntData(THEME_STABLE.getValue());
        }
        onActivitySetTheme(this, theme, false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeBase();
        if (isNetworkAvailable(getApplicationContext()))
            checkPermissions();
        else enableNetwork(this);
    }

    private void checkPermissions() {
        if (enableGpsForResult(this))
            if (!checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (!checkCameraPermission(getApplicationContext())) {
                askStoragePermission();
            } else {
                initialize();
            }
    }

    private void askStoragePermission() {
        final PermissionListener permissionlistener = new PermissionListener() {
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

    private void askLocationPermission() {
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                LocationTrackingGps.setInstance(null);
                LocationTrackingGoogle.setInstance(null);
                setActivityComponent(activity);
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

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new CustomToast().info(getString(R.string.how_to_exit));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setOnDrawerItemClick() {
        binding.imageViewHeader.setOnClickListener(v -> {
            if (POSITION != -1) {
                POSITION = -1;
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else
                binding.drawerLayout.closeDrawer(GravityCompat.START);
        });
        binding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(),
                        binding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                        if (position == 8) {
                            POSITION = -1;
                            exit = true;
                            finishAffinity();
                        } else if (POSITION != position) {
                            POSITION = position;
                            Intent intent = new Intent();
                            if (position == 0) {
                                intent = new Intent(getApplicationContext(), DownloadActivity.class);
                            } else if (position == 1) {
                                intent = new Intent(getApplicationContext(), ReadingActivity.class);
                            } else if (position == 2) {
                                intent = new Intent(getApplicationContext(), UploadActivity.class);
                            } else if (position == 3) {
                                intent = new Intent(getApplicationContext(), ReportActivity.class);
                            } else if (position == 4) {
                                intent = new Intent(getApplicationContext(), LocationActivity.class);
                            } else if (position == 5) {
                                intent = new Intent(getApplicationContext(), ReadingSettingActivity.class);
                            } else if (position == 6) {
                                intent = new Intent(getApplicationContext(), SettingActivity.class);
                            } else if (position == 7) {
                                intent = new Intent(getApplicationContext(), HelpActivity.class);
                            }
                            startActivity(intent);
                            finish();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        if (position == 1) {
                            POSITION = position;
                            Intent intent = new Intent(getApplicationContext(), ReadingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
        );
    }

    private void initializeBase() {
        activity = this;
        setActivityComponent(activity);
        migration(activity);
        final TextView textView = findViewById(R.id.text_view_title);
        textView.setText(sharedPreferenceManager.getStringData(DISPLAY_NAME.getValue()).
                concat(" (").concat(sharedPreferenceManager.getStringData(USER_CODE.getValue()))
                .concat(")"));
        binding.textViewVersion.setText(getString(R.string.version).concat(" ").concat(BuildConfig.VERSION_NAME));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (POSITION == 1) {
            final View view = getLayoutInflater().inflate(R.layout.reading_header, null);
            toolbar.addView(view);
        }
        fillDrawerListView();
        setOnDrawerItemClick();
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        binding.drawerLayout.addDrawerListener(toggle);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(view1 -> binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    private void fillDrawerListView() {
        final List<DrawerItem> dataList = DrawerItem.createItemList(
                getResources().getStringArray(R.array.menu),
                getResources().obtainTypedArray(R.array.icons));
        final NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this, dataList);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerView.setNestedScrollingEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == GPS_CODE) checkPermissions();
            if (requestCode == REQUEST_NETWORK_CODE) {
                if (isNetworkAvailable(getApplicationContext())) checkPermissions();
                else enableMobileWifi(this);
            }
            if (requestCode == REQUEST_WIFI_CODE) {
                if (isNetworkAvailable(getApplicationContext())) checkPermissions();
                else enableNetwork(this);
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
        if (exit)
            android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}