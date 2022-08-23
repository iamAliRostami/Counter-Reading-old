package com.leon.counter_reading.view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;

public class NotReadingViewModel extends BaseObservable {
    private int unread;
    private int total;

    public NotReadingViewModel(int unread, int total) {
        setUnread(unread);
        setTotal(total);
    }

    @Bindable
    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
        notifyPropertyChanged(BR.unread);
    }

    @Bindable
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        notifyPropertyChanged(BR.total);
    }
}
