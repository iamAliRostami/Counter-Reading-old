package com.leon.counter_reading.view_models;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.sardari.daterangepicker.utils.PersianCalendar;

public class PerformanceViewModel extends BaseObservable {
    private int overalCount;
    private int adiCount;
    private int faqedCount;
    private int maneCount;
    private int xarabCount;
    private int tavizCount;
    private int forbiddenCount;
    private int mediaCount;
    private int detailVisibility;
    private String fromDate;
    private String toDate;
    private String message;
    private String generationDateTime;
    private int status;
    private boolean isValid;

    public PerformanceViewModel() {
        setFromDate(new PersianCalendar().getPersianShortDate());
        setToDate(new PersianCalendar().getPersianShortDate());
        setDetailVisibility(View.GONE);
    }

    @Bindable
    public int getOveralCount() {
        return overalCount;
    }

    public void setOveralCount(int overalCount) {
        this.overalCount = overalCount;
        notifyPropertyChanged(BR.overalCount);
    }

    @Bindable
    public int getAdiCount() {
        return adiCount;
    }

    public void setAdiCount(int adiCount) {
        this.adiCount = adiCount;
        notifyPropertyChanged(BR.adiCount);
    }

    @Bindable
    public int getFaqedCount() {
        return faqedCount;
    }

    public void setFaqedCount(int faqedCount) {
        this.faqedCount = faqedCount;
        notifyPropertyChanged(BR.faqedCount);
    }

    @Bindable
    public int getManeCount() {
        return maneCount;
    }

    public void setManeCount(int maneCount) {
        this.maneCount = maneCount;
        notifyPropertyChanged(BR.maneCount);
    }

    @Bindable
    public int getXarabCount() {
        return xarabCount;
    }

    public void setXarabCount(int xarabCount) {
        this.xarabCount = xarabCount;
        notifyPropertyChanged(BR.xarabCount);
    }

    @Bindable
    public int getTavizCount() {
        return tavizCount;
    }

    public void setTavizCount(int tavizCount) {
        this.tavizCount = tavizCount;
        notifyPropertyChanged(BR.tavizCount);
    }

    @Bindable
    public int getForbiddenCount() {
        return forbiddenCount;
    }

    public void setForbiddenCount(int forbiddenCount) {
        this.forbiddenCount = forbiddenCount;
        notifyPropertyChanged(BR.forbiddenCount);
    }

    @Bindable
    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
        notifyPropertyChanged(BR.mediaCount);
    }

    @Bindable
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyPropertyChanged(BR.message);
    }


    @Bindable
    public int getDetailVisibility() {
        return detailVisibility;
    }

    public void setDetailVisibility(int detailVisibility) {
        this.detailVisibility = detailVisibility;
        notifyPropertyChanged(BR.detailVisibility);
    }

    @Bindable
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
        notifyPropertyChanged(BR.fromDate);
    }

    @Bindable
    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
        notifyPropertyChanged(BR.toDate);
    }

    @Bindable
    public String getGenerationDateTime() {
        return generationDateTime;
    }

    public void setGenerationDateTime(String generationDateTime) {
        this.generationDateTime = generationDateTime;
        notifyPropertyChanged(BR.generationDateTime);
    }

    @Bindable
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
    }
}
