package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;

public class ReadingViewModel extends BaseObservable {
    private KarbariDto karbariDto;
    private OnOffLoadDto onOffLoadDto;
    private ReadingConfigDefaultDto readingConfigDefaultDto;
    private int rotation;
    private int position;

    public ReadingViewModel() {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()))
            setRotation(180);
    }

    @Bindable
    public OnOffLoadDto getOnOffLoadDto() {
        return onOffLoadDto;
    }

    public void setOnOffLoadDto(OnOffLoadDto onOffLoadDto) {
        this.onOffLoadDto = onOffLoadDto;
        notifyPropertyChanged(BR.onOffLoadDto);
    }

    @Bindable
    public KarbariDto getKarbariDto() {
        return karbariDto;
    }

    public void setKarbariDto(KarbariDto karbariDto) {
        this.karbariDto = karbariDto;
        notifyPropertyChanged(BR.karbariDto);
    }

    @Bindable
    public ReadingConfigDefaultDto getReadingConfigDefaultDto() {
        return readingConfigDefaultDto;
    }

    public void setReadingConfigDefaultDto(ReadingConfigDefaultDto readingConfigDefaultDto) {
        this.readingConfigDefaultDto = readingConfigDefaultDto;
        notifyPropertyChanged(BR.readingConfigDefaultDto);
    }

    @Bindable
    public String getCounterNumber() {
        return onOffLoadDto.counterNumber != null ? String.valueOf(onOffLoadDto.counterNumber) : "";
    }

    public void setCounterNumber(int counterNumber) {
        onOffLoadDto.counterNumber = counterNumber;
        notifyPropertyChanged(BR.onOffLoadDto);
    }

    @Bindable
    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        notifyPropertyChanged(BR.rotation);
    }

    @Bindable
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }
}
