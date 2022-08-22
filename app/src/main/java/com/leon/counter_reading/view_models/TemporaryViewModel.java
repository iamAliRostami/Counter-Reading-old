package com.leon.counter_reading.view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;

public class TemporaryViewModel extends BaseObservable {
    private boolean isFirst = true;
    private String[] items;
    private int total;
    private int mane;

    @Bindable
    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
        notifyPropertyChanged(BR.items);
    }

    @Bindable
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        notifyPropertyChanged(BR.total);
    }

    @Bindable
    public int getMane() {
        return mane;
    }

    public void setMane(int mane) {
        this.mane = mane;
        notifyPropertyChanged(BR.mane);
    }

    @Bindable
    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
        notifyPropertyChanged(BR.first);
    }
}
