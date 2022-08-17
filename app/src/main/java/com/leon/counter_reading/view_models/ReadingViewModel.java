package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.KEYBOARD_TYPE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;

import android.content.Context;
import android.media.AudioManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.utils.DifferentCompanyManager;

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

    private String ahadTotal;
    private String ahad1;
    private String ahad2;
    private String address;
    private String preDate;
    private String serial;
    private String name;

    private String counterNumber;
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
    public String getAhad1() {
        return ahad1;
    }

    public void setAhad1(String ahad1) {
        this.ahad1 = ahad1;
        notifyPropertyChanged(BR.ahad1);
    }

    @Bindable
    public String getAhad2() {
        return ahad2;
    }

    public void setAhad2(String ahad2) {
        this.ahad2 = ahad2;
        notifyPropertyChanged(BR.ahad2);
    }

    @Bindable
    public String getAhadTotal() {
        return ahadTotal;
    }

    public void setAhadTotal(String ahadTotal) {
        this.ahadTotal = ahadTotal;
        notifyPropertyChanged(BR.ahadTotal);
    }

    @Bindable
    public String getAhad1Title() {
        return String.format("%s : ", DifferentCompanyManager.getAhad2(getActiveCompanyName()));
    }

    @Bindable
    public String getAhad2Title() {
        return String.format("%s : ", DifferentCompanyManager.getAhad1(getActiveCompanyName()));
    }

    @Bindable
    public String getAhadTotalTitle() {
        return String.format("%s : ", DifferentCompanyManager.getAhadTotal(getActiveCompanyName()));
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPreDate() {
        return preDate;
    }

    public void setPreDate(String preDate) {
        this.preDate = preDate;
        notifyPropertyChanged(BR.preDate);
    }

    @Bindable
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
        notifyPropertyChanged(BR.serial);
    }

    @Bindable
    public String getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(String counterNumber) {
        this.counterNumber = counterNumber;
        notifyPropertyChanged(BR.counterNumber);
        getOnOffLoadDto().counterNumber = (counterNumber == null || counterNumber.isEmpty()) ? null :
                Integer.parseInt(counterNumber);
        notifyPropertyChanged(BR.onOffLoadDto);
    }

    @Bindable
    public OnOffLoadDto getOnOffLoadDto() {
        return onOffLoadDto;
    }

    public void setOnOffLoadDto(OnOffLoadDto onOffLoadDto) {
        this.onOffLoadDto = onOffLoadDto;
        notifyPropertyChanged(BR.onOffLoadDto);
        setOnOffLoad();
    }

    private void setOnOffLoad() {
        setCounterNumber(getOnOffLoadDto().counterNumber != null ? String.valueOf(getOnOffLoadDto().counterNumber) : null);
        setName(String.format("%s %s", getOnOffLoadDto().firstName, getOnOffLoadDto().sureName));
        setAhadTotal(String.valueOf(getOnOffLoadDto().ahadSaierOrAbBaha));
        setAhad1(String.valueOf(getOnOffLoadDto().ahadMaskooniOrAsli));
        setAhad2(String.valueOf(getOnOffLoadDto().ahadTejariOrFari));
        setSerial(getOnOffLoadDto().counterSerial);
        setPreDate(getOnOffLoadDto().preDate);
        setAddress(getOnOffLoadDto().address);
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
    public String getCode() {
        return getReadingConfigDefaultDto().isOnQeraatCode ? getOnOffLoadDto().qeraatCode : getOnOffLoadDto().eshterak;
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

    public boolean onTextViewLongClickListener() {
        setCounterNumber(null);
        return false;
    }

    public final void onKeyboardClickListener(View view) {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue()))
            keyboardEvent(view);
//        {
//            if (view.getId() == R.id.button_keyboard_backspace) {
//                if (getCounterNumber().length() > 0)
//                    setCounterNumber(getCounterNumber().substring(0, getCounterNumber().length() - 1));
//            } else if (getCounterNumber() != null && getCounterNumber().length() < 9) {
//                setCounterNumber((getCounterNumber() != null ? getCounterNumber() : "")
//                        .concat(((Button) view).getText().toString()));
//            }
//        }
    }

    public boolean onKeyboardTouchListener(View view, MotionEvent motionEvent) {
//        ringNotification();
        if (!getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue())) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                keyboardEvent(view);
//                {if (view.getId() == R.id.button_keyboard_backspace) {
//                    if (getCounterNumber().length() > 0)
//                        setCounterNumber(getCounterNumber().substring(0, getCounterNumber().length() - 1));
//                } else if (getCounterNumber() != null && getCounterNumber().length() < 9)
//                    setCounterNumber((getCounterNumber() != null ? getCounterNumber() : "")
//                            .concat(((Button) view).getText().toString()));
            }
//        }
        return false;
    }

    private void keyboardEvent(View view) {
        ringNotification();
        if (view.getId() == R.id.button_keyboard_backspace) {
            if (getCounterNumber().length() > 0)
                setCounterNumber(getCounterNumber().substring(0, getCounterNumber().length() - 1));
        } else if (getCounterNumber() != null && getCounterNumber().length() < 9) {
            setCounterNumber((getCounterNumber() != null ? getCounterNumber() : "")
                    .concat(((Button) view).getText().toString()));
        }
    }

    public void ringNotification() {
        try {
            final AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(AudioManager.FX_KEY_CLICK, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
