package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_STABLE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;

import android.os.Bundle;
import android.os.Debug;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityDistributionBillBinding;

public class DistributionBillActivity extends AppCompatActivity {
    private ActivityDistributionBillBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onActivitySetTheme(this, getApplicationComponent().SharedPreferenceModel()
                .getIntData(THEME_STABLE.getValue()), true);
        super.onCreate(savedInstanceState);
        binding = ActivityDistributionBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    void initialize() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));
        binding.imageViewBill.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.img_temporary));
        startAnimationOnTextViewCounter();
    }

    private void startAnimationOnTextViewCounter() {
        final Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        binding.textViewCounter.startAnimation(anim);
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
        binding.imageViewBill.setImageDrawable(null);
        super.onDestroy();
    }
}