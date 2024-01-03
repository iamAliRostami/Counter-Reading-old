package com.leon.counter_reading.view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;

public class NotReadingViewModel extends BaseObservable {
    private int continueRead;
    private int unread;
    private int beforeRead;
    private int total;

    public NotReadingViewModel(int total, int unread, int beforeRead, int continueRead) {
        setTotal(total);
        setContinueRead(continueRead);
        setBeforeRead(beforeRead);
        setUnread(unread);
    }

    @Bindable
    public int getContinueRead() {
        return continueRead;
    }

    public void setContinueRead(int continueRead) {
        this.continueRead = continueRead;
        notifyPropertyChanged(BR.continueRead);
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
    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
        notifyPropertyChanged(BR.unread);
    }

    @Bindable
    public int getBeforeRead() {
        return beforeRead;
    }

    public void setBeforeRead(int beforeRead) {
        this.beforeRead = beforeRead;
        notifyPropertyChanged(BR.beforeRead);
    }
}
