package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TRACKING;
import static com.leon.counter_reading.enums.BundleEnum.ZONE_ID;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_STABLE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.onActivitySetTheme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ReadingReportAdapter;
import com.leon.counter_reading.databinding.ActivityReadingReportBinding;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.reporting.GetReadingReportDBDataOld;

import java.util.ArrayList;

public class ReadingReportActivity extends AppCompatActivity {
    private ActivityReadingReportBinding binding;
    private Activity activity;
    private String uuid;
    private int position, trackNumber, zoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onActivitySetTheme(this, getApplicationComponent()
                .SharedPreferenceModel().getIntData(THEME_STABLE.getValue()), true);
        super.onCreate(savedInstanceState);
        binding = ActivityReadingReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        initialize();
    }

    private void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BILL_ID.getValue());
            trackNumber = getIntent().getExtras().getInt(TRACKING.getValue());
            position = getIntent().getExtras().getInt(POSITION.getValue());
            zoneId = getIntent().getExtras().getInt(ZONE_ID.getValue());
            getIntent().getExtras().clear();
        }
        new GetReadingReportDBDataOld(activity, trackNumber, zoneId, uuid).execute(activity);
        binding.buttonSubmit.setOnClickListener(v -> {
            final Intent intent = new Intent();
            intent.putExtra(POSITION.getValue(), position);
            intent.putExtra(BILL_ID.getValue(), uuid);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    public void setupRecyclerView(ArrayList<CounterReportDto> counterReportDtos,
                                  ArrayList<OffLoadReport> offLoadReports) {
        final ReadingReportAdapter adapter =
                new ReadingReportAdapter(activity, uuid, trackNumber,counterReportDtos, offLoadReports);
        activity.runOnUiThread(() -> binding.listViewReports.setAdapter(adapter));
    }

    @Override
    public void onBackPressed() {
        new CustomToast().warning(getString(R.string.submit_for_back));
    }

    @Override
    protected void onDestroy() {
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