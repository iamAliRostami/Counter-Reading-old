package com.leon.counter_reading.activities;

import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;

import android.graphics.Color;
import android.os.Debug;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivitySettingBinding;
import com.leon.counter_reading.fragments.setting.SettingChangeAvatarFragment;
import com.leon.counter_reading.fragments.setting.SettingChangePasswordFragment;
import com.leon.counter_reading.fragments.setting.SettingChangeThemeFragment;
import com.leon.counter_reading.fragments.setting.SettingUpdateFragment;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.backup_restore.BackUp;
import com.leon.counter_reading.utils.backup_restore.Restore;

public class SettingActivity extends BaseActivity {
    private ActivitySettingBinding binding;
    private int previousState, currentState;

    @Override
    protected void initialize() {
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        final View childLayout = binding.getRoot();
        final ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        setupViewPager();
        initializeTextViews();
    }

    private void initializeTextViews() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        textViewChangeTheme();
        textViewChangePassword();
        textViewUpdate();
        textViewAvatar();
    }

    private void textViewChangeTheme() {
        binding.textViewChangeTheme.setOnClickListener(view -> {
            setColor();
            binding.textViewChangeTheme.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void textViewChangePassword() {
        binding.textViewChangePassword.setOnClickListener(view -> {
            setColor();
            binding.textViewChangePassword.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    private void textViewUpdate() {
        binding.textViewUpdate.setOnClickListener(view -> {
            setColor();
            binding.textViewUpdate.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    private void textViewAvatar() {
        binding.textViewChangeAvatar.setOnClickListener(view -> {
            setColor();
            binding.textViewChangeAvatar.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(3);
        });
    }

    private void setColor() {
        binding.textViewUpdate.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUpdate.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
        binding.textViewChangeTheme.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangeTheme.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
        binding.textViewChangePassword.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangePassword.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
        binding.textViewChangeAvatar.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangeAvatar.setTextColor(ContextCompat.getColor(this, R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewChangeTheme.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewUpdate.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewChangePassword.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewChangeAvatar.setPadding(0, (int) getResources().getDimension(R.dimen.medium_dp), 0, (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        final ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(new SettingChangeThemeFragment());
        adapter.addFragment(new SettingChangePasswordFragment());
        adapter.addFragment(new SettingUpdateFragment());
        adapter.addFragment(new SettingChangeAvatarFragment());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewChangeTheme.callOnClick();
                } else if (position == 1) {
                    binding.textViewChangePassword.callOnClick();
                } else if (position == 2) {
                    binding.textViewUpdate.callOnClick();
                } else if (position == 3) {
                    binding.textViewChangeAvatar.callOnClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int currentPage = binding.viewPager.getCurrentItem();
                if (currentPage == 3 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        binding.viewPager.setCurrentItem(currentPage == 0 ? 3 : 0);
                    }
                }
            }
        });
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_backup) {
            new BackUp(this).execute(this);
        } else if (id == R.id.menu_restore) {
            new Restore(this).execute(this);
        }
        return super.onOptionsItemSelected(item);
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
}