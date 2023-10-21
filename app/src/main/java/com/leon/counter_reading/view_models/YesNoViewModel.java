package com.leon.counter_reading.view_models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;


public class YesNoViewModel extends BaseObservable {
    private String title;
    private String question;
    private String yes;
    private String no;
    private int drawable;

    public YesNoViewModel(String title, String question, String yes, String no, int drawable) {
        setTitle(title);
        setQuestion(question);
        setDrawable(drawable);
        setYes(yes);
        setNo(no);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
        notifyPropertyChanged(BR.question);
    }

    @Bindable
    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
        notifyPropertyChanged(BR.drawable);
    }

    @Bindable
    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
        notifyPropertyChanged(BR.yes);
    }

    @Bindable
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
        notifyPropertyChanged(BR.no);
    }
}

