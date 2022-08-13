package com.leon.counter_reading.view_models;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.leon.counter_reading.BR;

public class ReportInspectionViewModel extends BaseObservable {
    private int totalReport;
    private int sentReports;
    private int unsentReports;

    @Bindable
    public int getTotalReport() {
        return totalReport;
    }

    public void setTotalReport(int totalReport) {
        this.totalReport = totalReport;
        notifyPropertyChanged(BR.totalReport);
    }

    @Bindable
    public int getSentReports() {
        return sentReports;
    }

    public void setSentReports(int sentReports) {
        this.sentReports = sentReports;
        notifyPropertyChanged(BR.sentReports);
    }

    @Bindable
    public int getUnsentReports() {
        return unsentReports;
    }

    public void setUnsentReports(int unsentReports) {
        this.unsentReports = unsentReports;
        notifyPropertyChanged(BR.unsentReports);
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("android:text")
    public static void setText(TextView view, int value) {
        view.setText(Integer.toString(value));
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static int getText(TextView view) {
        return Integer.parseInt(view.getText().toString());
    }
}
