package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.helpers.DifferentCompanyManager;

public class NavigationViewModel extends BaseObservable {
    private OnOffLoadDto onOffLoadDto;
    private String possibleEmpty;
    private String possibleAddress;
    private String possibleCounterSerial;
    private String possibleEshterak;
    private String possibleMobile;
    private String possiblePhoneNumber;
    private int position;

    public NavigationViewModel(OnOffLoadDto onOffLoadDto, int position) {
        this.onOffLoadDto = onOffLoadDto;
        this.position = position;
        setOnOffLoad();
    }

    private void setOnOffLoad() {
        setPossibleEshterak(getOnOffLoadDto().possibleEshterak != null ? getOnOffLoadDto().possibleEshterak : "");
        setPossiblePhoneNumber(getOnOffLoadDto().possiblePhoneNumber != null ? getOnOffLoadDto().possiblePhoneNumber : "");
        setPossibleMobile(getOnOffLoadDto().possibleMobile != null ? getOnOffLoadDto().possibleMobile : "");
        setPossibleCounterSerial(getOnOffLoadDto().possibleCounterSerial != null ? getOnOffLoadDto().possibleCounterSerial : "");
        setPossibleAddress(getOnOffLoadDto().possibleAddress != null ? getOnOffLoadDto().possibleAddress : "");
        setPossibleEmpty(getOnOffLoadDto().possibleEmpty != null ? String.valueOf(getOnOffLoadDto().possibleEmpty) : "");
    }

    public void updateOnOffLoadDto() {
        if (getPossibleEshterak() != null && !getPossibleEshterak().isEmpty())
            getOnOffLoadDto().possibleEshterak = getPossibleEshterak();
        if (getPossiblePhoneNumber() != null && !getPossiblePhoneNumber().isEmpty())
            getOnOffLoadDto().possiblePhoneNumber = getPossiblePhoneNumber();
        if (getPossibleMobile() != null && !getPossibleMobile().isEmpty())
            getOnOffLoadDto().possibleMobile = getPossibleMobile();
        if (getPossibleCounterSerial() != null && !getPossibleCounterSerial().isEmpty())
            getOnOffLoadDto().possibleCounterSerial = getPossibleCounterSerial();
        if (getPossibleAddress() != null && !getPossibleAddress().isEmpty())
            getOnOffLoadDto().possibleAddress = getPossibleAddress();
        if (getPossibleEmpty() != null && !getPossibleEmpty().isEmpty())
            getOnOffLoadDto().possibleEmpty = Integer.valueOf(getPossibleEmpty());
        notifyPropertyChanged(BR.onOffLoadDto);
    }

    @Bindable
    public String getPossibleEmpty() {
        return possibleEmpty;
    }

    public void setPossibleEmpty(String possibleEmpty) {
        this.possibleEmpty = possibleEmpty;
        notifyPropertyChanged(BR.possibleEmpty);
    }

    @Bindable
    public String getPossibleAddress() {
        return possibleAddress;
    }

    public void setPossibleAddress(String possibleAddress) {
        this.possibleAddress = possibleAddress;
        notifyPropertyChanged(BR.possibleAddress);
    }

    @Bindable
    public String getPossibleCounterSerial() {
        return possibleCounterSerial;
    }

    public void setPossibleCounterSerial(String possibleCounterSerial) {
        this.possibleCounterSerial = possibleCounterSerial;
        notifyPropertyChanged(BR.possibleCounterSerial);
    }

    @Bindable
    public String getPossibleEshterak() {
        return possibleEshterak;
    }

    public void setPossibleEshterak(String possibleEshterak) {
        this.possibleEshterak = possibleEshterak;
        notifyPropertyChanged(BR.possibleEshterak);
    }

    @Bindable
    public String getPossibleMobile() {
        return possibleMobile;
    }

    public void setPossibleMobile(String possibleMobile) {
        this.possibleMobile = possibleMobile;
        notifyPropertyChanged(BR.possibleMobile);
    }

    @Bindable
    public String getPossiblePhoneNumber() {
        return possiblePhoneNumber;
    }

    public void setPossiblePhoneNumber(String possiblePhoneNumber) {
        this.possiblePhoneNumber = possiblePhoneNumber;
        notifyPropertyChanged(BR.possiblePhoneNumber);
    }

    @Bindable
    public OnOffLoadDto getOnOffLoadDto() {
        return onOffLoadDto;
    }

    public void setOnOffLoadDto(OnOffLoadDto onOffLoadDto) {
        this.onOffLoadDto = onOffLoadDto;
        notifyPropertyChanged(BR.onOffLoadDto);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public int getEshterakMaxLength() {
        return DifferentCompanyManager.getEshterakMaxLength();
    }

    @Bindable
    public String getAhadEmptyTitle() {
        return getAhad().concat(getContext().getString(R.string.empty));
    }
}
