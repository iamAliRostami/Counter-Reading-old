package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.helpers.Constants.counterStateDtos;
import static com.leon.counter_reading.helpers.Constants.karbariDtos;
import static com.leon.counter_reading.helpers.Constants.onOffLoadDtos;
import static com.leon.counter_reading.helpers.Constants.readingConfigDefaultDtos;
import static com.leon.counter_reading.helpers.MyApplication.getContext;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.ReadingFragment;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomToast;

public class ViewPagerStateAdapter2 extends FragmentStateAdapter {

    public ViewPagerStateAdapter2(@NonNull FragmentActivity fragmentActivity, ReadingData readingData) {
        super(fragmentActivity);
        karbariDtos.clear();
        onOffLoadDtos.clear();
        readingConfigDefaultDtos.clear();
        counterStateDtos.clear();

        counterStateDtos.addAll(readingData.counterStateDtos);
        onOffLoadDtos.addAll(readingData.onOffLoadDtos);
        for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
            int k = 0;
            boolean found = false;
            while (!found && k < readingData.readingConfigDefaultDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).zoneId == readingData.readingConfigDefaultDtos.get(k).zoneId) {
                    readingConfigDefaultDtos.add(readingData.readingConfigDefaultDtos.get(k));
                    found = true;
                }
                k++;
            }
            k = 0;
            found = false;
            while (!found && k < readingData.karbariDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).karbariCode == readingData.karbariDtos.get(k).moshtarakinId) {
                    karbariDtos.add(readingData.karbariDtos.get(k));
                    found = true;
                }
                k++;
            }
            if (!found)
                karbariDtos.add(new KarbariDto());
            k = 0;
            found = false;
            while (!found && k < readingData.trackingDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).trackNumber == readingData.trackingDtos.get(k).trackNumber) {
                    readingData.onOffLoadDtos.get(i).hasPreNumber = readingData.trackingDtos.get(k).hasPreNumber;
                    readingData.onOffLoadDtos.get(i).displayBillId = readingData.trackingDtos.get(k).displayBillId;
                    readingData.onOffLoadDtos.get(i).displayRadif = readingData.trackingDtos.get(k).displayRadif;
                    found = true;
                }
                k++;
            }
            for (int j = 0; j < readingData.qotrDictionary.size(); j++) {
                if (readingData.onOffLoadDtos.get(i).qotrCode == readingData.qotrDictionary.get(j).id)
                    readingData.onOffLoadDtos.get(i).qotr = readingData.qotrDictionary.get(j).title;
                if (readingData.onOffLoadDtos.get(i).sifoonQotrCode == readingData.qotrDictionary.get(j).id)
                    readingData.onOffLoadDtos.get(i).sifoonQotr = readingData.qotrDictionary.get(j).title;
            }
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        try {
            if (counterStateDtos.isEmpty()) {
                new CustomToast().error(getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
                return ReadingFragment.newInstance(i);
            }
            return ReadingFragment.newInstance(i);
        } catch (Exception e) {
            new CustomToast().error(getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
            return ReadingFragment.newInstance(i);
        }
    }

    @Override
    public int getItemCount() {
        return onOffLoadDtos.size();
    }
}