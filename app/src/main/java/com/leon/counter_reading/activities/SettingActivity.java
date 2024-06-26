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
import androidx.viewpager2.widget.ViewPager2;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerTabAdapter;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivitySettingBinding;
import com.leon.counter_reading.fragments.setting.SettingChangeAvatarFragment;
import com.leon.counter_reading.fragments.setting.SettingChangePasswordFragment;
import com.leon.counter_reading.fragments.setting.SettingChangeThemeFragment;
import com.leon.counter_reading.fragments.setting.SettingUpdateFragment;
import com.leon.counter_reading.utils.DepthPageTransformer2;
import com.leon.counter_reading.utils.backup_restore.BackUp;
import com.leon.counter_reading.utils.backup_restore.Restore;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySettingBinding binding;
    private int currentState;

    @Override
    protected void initialize() {
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        setupViewPager();
        initializeTextViews();
    }

    private void initializeTextViews() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        binding.textViewChangeTheme.setOnClickListener(this);
        binding.textViewChangePassword.setOnClickListener(this);
        binding.textViewUpdate.setOnClickListener(this);
        binding.textViewChangeAvatar.setOnClickListener(this);
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
        final ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(this);
        adapter.addFragment(new SettingChangeThemeFragment());
        adapter.addFragment(new SettingChangePasswordFragment());
        adapter.addFragment(new SettingUpdateFragment());
        adapter.addFragment(new SettingChangeAvatarFragment());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.registerOnPageChangeCallback(pageChangeListener);
        binding.viewPager.setPageTransformer(new DepthPageTransformer2());
    }

    private final ViewPager2.OnPageChangeCallback pageChangeListener = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
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
        public void onPageSelected(int position) {
            super.onPageSelected(position);
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
    };

    @Override
    public void onClick(View v) {
        setColor();
        int id = v.getId();
        if (id == R.id.text_view_update) {
            binding.textViewUpdate.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(2);
        } else if (id == R.id.text_view_change_password) {
            binding.textViewChangePassword.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(1);
        } else if (id == R.id.text_view_change_theme) {
            binding.textViewChangeTheme.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(0);
        } else if (id == R.id.text_view_change_avatar) {
            binding.textViewChangeAvatar.setBackground(ContextCompat.getDrawable(this, R.drawable.border_white_2));
            binding.viewPager.setCurrentItem(3);
        }
        setPadding();
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