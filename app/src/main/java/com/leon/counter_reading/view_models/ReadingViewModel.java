package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.view.View;

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
    private int counterStateCode;
    private int counterStatePosition;
    private int textViewId;
    private int buttonId;
    private boolean shouldEnterNumber;
    private boolean canLessThanPre;
    private boolean canEnterNumber;
    private boolean debtOrNumber;
    private boolean isMakoos;
    private boolean isMane;

    public ReadingViewModel() {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()))
            setRotation(180);
        setButtonId(View.generateViewId());
        setTextViewId(View.generateViewId());
    }

    @Bindable
    public int getCounterStateCode() {
        return counterStateCode;
    }

    public void setCounterStateCode(int counterStateCode) {
        this.counterStateCode = counterStateCode;
        notifyPropertyChanged(BR.counterStateCode);
    }

    @Bindable
    public int getCounterStatePosition() {
        return counterStatePosition;
    }

    public void setCounterStatePosition(int counterStatePosition) {
        this.counterStatePosition = counterStatePosition;
        notifyPropertyChanged(BR.counterStatePosition);
    }

    @Bindable
    public boolean isShouldEnterNumber() {
        return shouldEnterNumber;
    }

    public void setShouldEnterNumber(boolean shouldEnterNumber) {
        this.shouldEnterNumber = shouldEnterNumber;
        notifyPropertyChanged(BR.shouldEnterNumber);
    }

    @Bindable
    public boolean isCanLessThanPre() {
        return canLessThanPre;
    }

    public void setCanLessThanPre(boolean canLessThanPre) {
        this.canLessThanPre = canLessThanPre;
        notifyPropertyChanged(BR.canLessThanPre);
    }

    @Bindable
    public boolean isCanEnterNumber() {
        return canEnterNumber;
    }

    public void setCanEnterNumber(boolean canEnterNumber) {
        this.canEnterNumber = canEnterNumber;
        notifyPropertyChanged(BR.canEnterNumber);
    }

    @Bindable
    public boolean isDebtOrNumber() {
        return debtOrNumber;
    }

    public void setDebtOrNumber(boolean debtOrNumber) {
        this.debtOrNumber = debtOrNumber;
        notifyPropertyChanged(BR.debtOrNumber);
    }

    @Bindable
    public boolean isMakoos() {
        return isMakoos;
    }

    public void setMakoos(boolean makoos) {
        isMakoos = makoos;
        notifyPropertyChanged(BR.makoos);
    }

    @Bindable
    public boolean isMane() {
        return isMane;
    }

    public void setMane(boolean mane) {
        isMane = mane;
        notifyPropertyChanged(BR.mane);
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

    @Bindable
    public int getTextViewId() {
        return textViewId;
    }

    public void setTextViewId(int textViewId) {
        this.textViewId = textViewId;
        notifyPropertyChanged(BR.textViewId);
    }

    @Bindable
    public int getButtonId() {
        return buttonId;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
        notifyPropertyChanged(BR.buttonId);
    }
}
