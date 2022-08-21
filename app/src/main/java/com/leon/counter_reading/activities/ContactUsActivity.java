package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.SharedReferenceKeys.DATE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_STABLE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME_TEMP;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;

import android.os.Bundle;
import android.os.Debug;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityContactUsBinding;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

public class ContactUsActivity extends AppCompatActivity {
    private ActivityContactUsBinding binding;
    private ISharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        onActivitySetTheme(this, sharedPreferenceManager.getIntData(THEME_STABLE.getValue()), true);
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    void initialize() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));
        if (sharedPreferenceManager.checkIsNotEmpty(USERNAME_TEMP.getValue()))
            binding.textViewDate.setText(sharedPreferenceManager.getStringData(DATE.getValue()));
        binding.textViewVersion.setText(BuildConfig.VERSION_NAME);
        binding.textViewSite.setText(R.string.site);
        binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_logo));
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
        binding.imageViewLogo.setImageDrawable(null);
        binding = null;
        super.onDestroy();
    }
}