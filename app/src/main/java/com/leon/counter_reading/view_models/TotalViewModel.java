package com.leon.counter_reading.view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;

public class TotalViewModel extends BaseObservable {
    private int zero;
    private int normal;
    private int high;
    private int low;

    public TotalViewModel(int zero, int normal, int high, int low) {
        this.zero = zero;
        this.normal = normal;
        this.high = high;
        this.low = low;
    }

    @Bindable
    public int getZero() {
        return zero;
    }

    public void setZero(int zero) {
        this.zero = zero;
        notifyPropertyChanged(BR.zero);
    }

    @Bindable
    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
        notifyPropertyChanged(BR.normal);
    }

    @Bindable
    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
        notifyPropertyChanged(BR.high);
    }

    @Bindable
    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
        notifyPropertyChanged(BR.low);
    }

    @Bindable
    public int getTotal() {
        return getHigh() + getLow() + getNormal() + getZero();
    }
}
