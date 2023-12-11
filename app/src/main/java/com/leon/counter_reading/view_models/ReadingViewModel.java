package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.KEYBOARD_TYPE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_TEMPORARY;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.MakeNotification.ringNotification;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.leon.counter_reading.R;
import com.leon.counter_reading.helpers.DifferentCompanyManager;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.Guilds;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.utils.CustomToast;

public class ReadingViewModel extends BaseObservable {
    private KarbariDto karbariDto;
    private Guilds guilds;
    private OnOffLoadDto onOffLoadDto;
    private ReadingConfigDefaultDto readingConfigDefaultDto;
    private int rotation;
    private int position;
    private int counterStateCode;
    private int counterStatePosition;
    private int textViewId;
    private int buttonId;
    private int counterNumberColor;
    private Drawable addressBackground;
    private String debtNumber;
    private String ahadTotal;
    private String ahad1;
    private String ahad2;
    private String address;
    private String preDate;
    private String serial;
    private String name;
    private String karbariTitle;
    private String guildTitle;
    private String sifoonQotr;
    private String qotr;
    private String radifOrBillId;
    private String counterNumber;
    private boolean shouldEnterNumber;
    private boolean canLessThanPre;
    private boolean canEnterNumber;
    private boolean debtOrNumber;
    private boolean isMakoos;
    private boolean isMane;
    private int accuracy;

    public ReadingViewModel() {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()))
            setRotation(180);
        setButtonId(View.generateViewId());
        setTextViewId(View.generateViewId());
        setCounterNumberColor(ContextCompat.getColor(getContext(), R.color.red));
    }

    public boolean onTextViewLongClickListener() {
        setCounterNumber("");
        return false;
    }

    public final void onKeyboardClickListener(View view) {
        if (!getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue()))
            keyboardEvent(view);
    }

    public boolean onKeyboardTouchListener(View view, MotionEvent motionEvent) {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue()))
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                keyboardEvent(view);
        return false;
    }

    private void keyboardEvent(View view) {
        ringNotification();
        if (view.getId() == R.id.button_keyboard_backspace) {
            if (getCounterNumber() != null && getCounterNumber().length() > 0)
                setCounterNumber(getCounterNumber().substring(0, getCounterNumber().length() - 1));
        } else if (getCounterNumber() != null && getCounterNumber().length() < 9) {
            setCounterNumber((getCounterNumber() != null ? getCounterNumber() : "")
                    .concat(((Button) view).getText().toString()));
        }
    }

    @SuppressLint("DefaultLocale")
    public boolean switchDebtNumber() {
        if (isDebtOrNumber()) {
            setDebtOrNumber(!isDebtOrNumber());
            setDebtNumber(String.format("%,d", getOnOffLoadDto().balance) + " ریال");
            setCounterNumberColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            if (getOnOffLoadDto().hasPreNumber) {
                setDebtOrNumber(!isDebtOrNumber());
                setDebtNumber(String.valueOf(getOnOffLoadDto().preNumber));
                setCounterNumberColor(ContextCompat.getColor(getContext(),
                        getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()) ?
                                android.R.color.white : android.R.color.black));
                return true;
            } else new CustomToast().warning(getContext().getString(R.string.can_not_show_pre));
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    private void setOnOffLoad() {
        setCounterNumber(getOnOffLoadDto().counterNumber != null ? String.valueOf(getOnOffLoadDto().counterNumber) : "");
        setName(String.format("%s %s", getOnOffLoadDto().firstName, getOnOffLoadDto().sureName));
        setAhadTotal(String.valueOf(getOnOffLoadDto().ahadSaierOrAbBaha));
        setAhad1(String.valueOf(getOnOffLoadDto().ahadTejariOrFari));
        setAhad2(String.valueOf(getOnOffLoadDto().ahadMaskooniOrAsli));

        setDebtNumber(String.format("%,d", getOnOffLoadDto().balance) + " ریال");
        setSerial(getOnOffLoadDto().counterSerial);
        setPreDate(getOnOffLoadDto().preDate);

        setAddressBackground(ContextCompat.getDrawable(getContext(), getOnOffLoadDto().mobile != null ?
                R.drawable.border_gray_3 : R.drawable.border_red_2));

        setAddress(getOnOffLoadDto().address);
        if (getKarbariDto().title == null)
            new CustomToast().warning(String.format("کاربری اشتراک %s به درستی بارگیری نشده است.", getOnOffLoadDto().eshterak));
        else setKarbariTitle(getKarbariDto().title);

        //TODO
        //        setGuildTitle(onOffLoadDto.preGuildCode);

        if (getOnOffLoadDto().qotr == null)
            new CustomToast().warning(String.format("قطر اشتراک %s به درستی بارگیری نشده است.", getOnOffLoadDto().eshterak));
        else
            setQotr(getOnOffLoadDto().qotr.equals("مشخص نشده") ? "-" : getOnOffLoadDto().qotr);
        if (getOnOffLoadDto().sifoonQotr == null)
            new CustomToast().warning(String.format("قطر سیفون اشتراک %s به درستی بارگیری نشده است.", getOnOffLoadDto().eshterak));
        else
            setSifoonQotr(getOnOffLoadDto().sifoonQotr.equals("مشخص نشده") ? "-" : getOnOffLoadDto().sifoonQotr);
        if (getOnOffLoadDto().displayRadif)
            setRadifOrBillId(String.valueOf(getOnOffLoadDto().radif));
        else if (getOnOffLoadDto().displayBillId)
            setRadifOrBillId(String.valueOf(getOnOffLoadDto().billId));
    }

    public void setCounterStateField(CounterStateDto counterStateDto, int i) {
        setCounterStatePosition(i);
        setCounterStateCode(counterStateDto.id);
        setMane(counterStateDto.isMane);
        setShouldEnterNumber(counterStateDto.shouldEnterNumber);
        setCanEnterNumber(counterStateDto.canEnterNumber);
        setCanLessThanPre(counterStateDto.canNumberBeLessThanPre);
        setMakoos(counterStateDto.title.equals("معکوس"));
        if (!isCanEnterNumber() && !isShouldEnterNumber())
            setCounterNumber("");
    }

    @Bindable
    public String getCode() {
        return getReadingConfigDefaultDto().isOnQeraatCode ? getOnOffLoadDto().qeraatCode : getOnOffLoadDto().eshterak;
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

    @Bindable
    public KarbariDto getKarbariDto() {
        return karbariDto;
    }

    public void setKarbariDto(KarbariDto karbariDto) {
        this.karbariDto = karbariDto;
        notifyPropertyChanged(BR.karbariDto);
    }

    public Guilds getGuilds() {
        return guilds;
    }

    public void setGuilds(Guilds guilds) {
        this.guilds = guilds;
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

    @Bindable
    public int getCounterNumberColor() {
        return counterNumberColor;
    }

    public void setCounterNumberColor(int counterNumberColor) {
        this.counterNumberColor = counterNumberColor;
        notifyPropertyChanged(BR.counterNumberColor);
    }

    @Bindable
    public Drawable getAddressBackground() {
        return addressBackground;
    }

    public void setAddressBackground(Drawable addressBackground) {
        this.addressBackground = addressBackground;
        notifyPropertyChanged(BR.addressBackground);
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
    public String getDebtNumber() {
        return debtNumber;
    }

    public void setDebtNumber(String debtNumber) {
        this.debtNumber = debtNumber;
        notifyPropertyChanged(BR.debtNumber);
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
        return String.format("%s : ", DifferentCompanyManager.getAhad2());
    }

    @Bindable
    public String getAhad2Title() {
        return String.format("%s : ", DifferentCompanyManager.getAhad1());
    }

    @Bindable
    public String getAhadTotalTitle() {
        return String.format("%s : ", DifferentCompanyManager.getAhadTotal());
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

//    public String getGuildTitle() {
//        return guildTitle;
//    }
//
//    public void setGuildTitle(String guildTitle) {
//        this.guildTitle = guildTitle;
//    }

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
    public String getKarbariTitle() {
//         return karbariTitle;
        //TODO
        return getGuilds().title != null ? karbariTitle.concat(String.format(" (%s)",
                getGuilds().title)) : karbariTitle;
    }

    public void setKarbariTitle(String karbariTitle) {
        this.karbariTitle = karbariTitle;
        notifyPropertyChanged(BR.karbariTitle);
    }

    @Bindable
    public String getSifoonQotr() {
        return sifoonQotr;
    }

    public void setSifoonQotr(String sifoonQotr) {
        this.sifoonQotr = sifoonQotr;
        notifyPropertyChanged(BR.sifoonQotr);
    }

    @Bindable
    public String getQotr() {
        return qotr;
    }

    public void setQotr(String qotr) {
        this.qotr = qotr;
        notifyPropertyChanged(BR.qotr);
    }

    @Bindable
    public String getRadifOrBillId() {
        return radifOrBillId;
    }

    public void setRadifOrBillId(String radifOrBillId) {
        this.radifOrBillId = radifOrBillId;
        notifyPropertyChanged(BR.radifOrBillId);
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    @SuppressLint("DefaultLocale")
    @Bindable
    public String getSubmitTextLocation() {
        return String.format("ثبت (%d)", accuracy);
    }
}
