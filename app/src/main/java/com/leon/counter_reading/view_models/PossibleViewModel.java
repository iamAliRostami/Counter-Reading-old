package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.ACCOUNT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.ADDRESS;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_1;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_2;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_EMPTY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_TOTAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.DESCRIPTION;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KARBARI;
import static com.leon.counter_reading.enums.SharedReferenceKeys.MOBILE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.READING_REPORT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SERIAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SHOW_AHAD_TITLE;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad1;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad2;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhadTotal;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.helpers.DifferentCompanyManager;

import java.util.ArrayList;

public class PossibleViewModel extends BaseObservable {
    private ArrayList<CounterReportDto> counterReports = new ArrayList<>();
    private ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    private ArrayList<KarbariDto> karbari = new ArrayList<>();
    private OnOffLoadDto onOffLoadDto;
    private String name;


    private String balance;
    private String oldRadif;
    private String oldEshterak;
    private String fatherName;
    private String description;

    private String possibleAddress;
    private String possibleEshterak;
    private String possibleCounterSerial;
    private String possibleAhadMaskooniOrAsli;
    private String possibleAhadSaierOrAbBaha;
    private String possibleAhadTejariOrFari;
    private String possibleEmpty;
    private String possibleMobile;

    private String ahadMaskooniOrAsli;
    private String ahadTejariOrFari;
    private String ahadSaierOrAbBaha;

    private String mobiles;
    private String mobile;

    private boolean justMobile;
    private int position;

    public PossibleViewModel(boolean justMobile, int position, OnOffLoadDto onOffLoadDto) {
        setJustMobile(justMobile);
        setPosition(position);
        setOnOffLoadDto(onOffLoadDto);
        setOnOffLoad();
        setCounterReports(new ArrayList<>(getApplicationComponent().MyDatabase().counterReportDao()
                .getAllCounterReportByZone(getOnOffLoadDto().zoneId)));
        setOffLoadReports(new ArrayList<>(getApplicationComponent().MyDatabase().offLoadReportDao()
                .getAllOffLoadReportById(getOnOffLoadDto().id, getOnOffLoadDto().trackNumber)));
        setKarbari(new ArrayList<>(getApplicationComponent().MyDatabase().karbariDao()
                .getAllKarbariDto()));
    }

    private void setOnOffLoad() {
        setName(String.format("%s %s", getOnOffLoadDto().firstName, getOnOffLoadDto().sureName));
        setBalance(String.valueOf(getOnOffLoadDto().balance));
        setOldRadif(getOnOffLoadDto().oldRadif != null ? getOnOffLoadDto().oldRadif : "-");
        setOldEshterak(getOnOffLoadDto().oldEshterak != null ? getOnOffLoadDto().oldEshterak : "-");
        setFatherName(getOnOffLoadDto().fatherName != null ? getOnOffLoadDto().fatherName : "-");
        setPossibleMobile(getOnOffLoadDto().possibleMobile);
        setMobile(getOnOffLoadDto().mobile != null ? getOnOffLoadDto().mobile : "-");
        if (getOnOffLoadDto().mobiles != null) {
            final String[] mobiles = getOnOffLoadDto().mobiles.split(",");
            String mobile = "";
            for (String mobileTemp : mobiles) {
                mobile = mobile.concat(mobileTemp.trim().concat("\n"));
            }
            setMobiles(mobile.substring(0, mobile.length() - 1));
        }
        if (getOnOffLoadDto().possibleAddress != null)
            setPossibleAddress(getOnOffLoadDto().possibleAddress);
        if (getOnOffLoadDto().possibleEshterak != null)
            setPossibleEshterak(getOnOffLoadDto().possibleEshterak);
        if (getOnOffLoadDto().possibleCounterSerial != null)
            setPossibleCounterSerial(getOnOffLoadDto().possibleCounterSerial);
        if (getOnOffLoadDto().possibleEmpty != null)
            setPossibleEmpty(String.valueOf(getOnOffLoadDto().possibleEmpty));

        setAhadMaskooniOrAsli(String.valueOf(getOnOffLoadDto().ahadMaskooniOrAsli));
        setAhadTejariOrFari(String.valueOf(getOnOffLoadDto().ahadTejariOrFari));
        setAhadSaierOrAbBaha(String.valueOf(getOnOffLoadDto().ahadSaierOrAbBaha));
        if (getOnOffLoadDto().possibleAhadMaskooniOrAsli != null)
            setPossibleAhadMaskooniOrAsli(String.valueOf(getOnOffLoadDto().possibleAhadMaskooniOrAsli));
        if (getOnOffLoadDto().possibleAhadTejariOrFari != null)
            setPossibleAhadTejariOrFari(String.valueOf(getOnOffLoadDto().possibleAhadTejariOrFari));
        if (getOnOffLoadDto().possibleAhadSaierOrAbBaha != null)
            setPossibleAhadSaierOrAbBaha(String.valueOf(getOnOffLoadDto().possibleAhadSaierOrAbBaha));

        if (getOnOffLoadDto().description != null)
            setDescription(getOnOffLoadDto().description);
    }

    public void updateOnOffLoadDto() {
        if (getPossibleMobile() != null)
            getOnOffLoadDto().possibleMobile = getPossibleMobile();
        if (getPossibleAddress() != null)
            getOnOffLoadDto().possibleAddress = getPossibleAddress();
        if (getPossibleEshterak() != null)
            getOnOffLoadDto().possibleEshterak = getPossibleEshterak();
        if (getPossibleCounterSerial() != null)
            getOnOffLoadDto().possibleCounterSerial = getPossibleCounterSerial();
        if (getPossibleAhadMaskooniOrAsli() != null && !getPossibleAhadMaskooniOrAsli().isEmpty())
            getOnOffLoadDto().possibleAhadMaskooniOrAsli = Integer.valueOf(getPossibleAhadMaskooniOrAsli());
        if (getPossibleAhadTejariOrFari() != null && !getPossibleAhadTejariOrFari().isEmpty())
            getOnOffLoadDto().possibleAhadTejariOrFari = Integer.valueOf(getPossibleAhadTejariOrFari());
        if (getPossibleAhadSaierOrAbBaha() != null && !getPossibleAhadSaierOrAbBaha().isEmpty())
            getOnOffLoadDto().possibleAhadSaierOrAbBaha = Integer.valueOf(getPossibleAhadSaierOrAbBaha());
        if (getDescription() != null)
            getOnOffLoadDto().description = getDescription();

        notifyPropertyChanged(BR.onOffLoadDto);
    }

    @Bindable
    public String getAhad1Title() {
        return String.format("%s:", getAhad1(getActiveCompanyName()));
    }

    @Bindable
    public String getAhad2Title() {
        return String.format("%s:", getAhad2(getActiveCompanyName()).replaceFirst("آحاد ", "")
                .replaceFirst("واحد", ""));
    }

    @Bindable
    public String getAhadTotalTitle() {
        return String.format("%s:", getAhadTotal(getActiveCompanyName()).replaceFirst("آحاد ", "")
                .replaceFirst("واحد", ""));
    }

    @Bindable
    public String getAhad1Hint() {
        return getAhad1(getActiveCompanyName());
    }

    @Bindable
    public String getAhad2Hint() {
        return getAhad2(getActiveCompanyName());
    }

    @Bindable
    public String getAhadTotalHint() {
        return getAhadTotal(getActiveCompanyName());
    }

    @Bindable
    public String getAhadEmptyHint() {
        return getAhad(getActiveCompanyName()).replaceFirst("آحاد ", "").replaceFirst("واحد", "")
                .concat(getContext().getString(R.string.empty));
    }

    @Bindable
    public int getEshterakMaxLength() {
        return DifferentCompanyManager.getEshterakMaxLength(getActiveCompanyName());
    }

    @Bindable
    public ArrayList<CounterReportDto> getCounterReports() {
        return counterReports;
    }

    public void setCounterReports(ArrayList<CounterReportDto> counterReports) {
        this.counterReports = counterReports;
        notifyPropertyChanged(BR.counterReports);
    }

    @Bindable
    public ArrayList<OffLoadReport> getOffLoadReports() {
        return offLoadReports;
    }

    public void setOffLoadReports(ArrayList<OffLoadReport> offLoadReports) {
        this.offLoadReports = offLoadReports;
        notifyPropertyChanged(BR.offLoadReports);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Bindable
    public ArrayList<KarbariDto> getKarbari() {
        return karbari;
    }

    public void setKarbari(ArrayList<KarbariDto> karbari) {
        this.karbari = karbari;
        notifyPropertyChanged(BR.karbariDto);
    }

    @Bindable
    public OnOffLoadDto getOnOffLoadDto() {
        return onOffLoadDto;
    }

    public void setOnOffLoadDto(OnOffLoadDto onOffLoadDto) {
        this.onOffLoadDto = onOffLoadDto;
        notifyPropertyChanged(BR.onOffLoadDto);
    }

    @Bindable
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
        notifyPropertyChanged(BR.balance);
    }

    @Bindable
    public String getOldRadif() {
        return oldRadif;
    }

    public void setOldRadif(String oldRadif) {
        this.oldRadif = oldRadif;
        notifyPropertyChanged(BR.oldRadif);
    }

    @Bindable
    public String getOldEshterak() {
        return oldEshterak;
    }

    public void setOldEshterak(String oldEshterak) {
        this.oldEshterak = oldEshterak;
        notifyPropertyChanged(BR.oldEshterak);
    }

    @Bindable
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
        notifyPropertyChanged(BR.fatherName);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
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
    public String getPossibleEshterak() {
        return possibleEshterak;
    }

    public void setPossibleEshterak(String possibleEshterak) {
        this.possibleEshterak = possibleEshterak;
        notifyPropertyChanged(BR.possibleEshterak);
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
    public String getPossibleAhadMaskooniOrAsli() {
        return possibleAhadMaskooniOrAsli;
    }

    public void setPossibleAhadMaskooniOrAsli(String possibleAhadMaskooniOrAsli) {
        this.possibleAhadMaskooniOrAsli = possibleAhadMaskooniOrAsli;
        notifyPropertyChanged(BR.possibleAhadMaskooniOrAsli);
    }

    @Bindable
    public String getPossibleAhadSaierOrAbBaha() {
        return possibleAhadSaierOrAbBaha;
    }

    public void setPossibleAhadSaierOrAbBaha(String possibleAhadSaierOrAbBaha) {
        this.possibleAhadSaierOrAbBaha = possibleAhadSaierOrAbBaha;
        notifyPropertyChanged(BR.possibleAhadSaierOrAbBaha);
    }

    @Bindable
    public String getPossibleAhadTejariOrFari() {
        return possibleAhadTejariOrFari;
    }

    public void setPossibleAhadTejariOrFari(String possibleAhadTejariOrFari) {
        this.possibleAhadTejariOrFari = possibleAhadTejariOrFari;
        notifyPropertyChanged(BR.possibleAhadTejariOrFari);
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
    public String getPossibleMobile() {
        return possibleMobile;
    }

    public void setPossibleMobile(String possibleMobile) {
        this.possibleMobile = possibleMobile;
        notifyPropertyChanged(BR.possibleMobile);
    }

    @Bindable
    public String getAhadMaskooniOrAsli() {
        return ahadMaskooniOrAsli;
    }

    public void setAhadMaskooniOrAsli(String ahadMaskooniOrAsli) {
        this.ahadMaskooniOrAsli = ahadMaskooniOrAsli;
        notifyPropertyChanged(BR.ahadMaskooniOrAsli);
    }

    @Bindable
    public String getAhadTejariOrFari() {
        return ahadTejariOrFari;
    }

    public void setAhadTejariOrFari(String ahadTejariOrFari) {
        this.ahadTejariOrFari = ahadTejariOrFari;
        notifyPropertyChanged(BR.ahadTejariOrFari);
    }

    @Bindable
    public String getAhadSaierOrAbBaha() {
        return ahadSaierOrAbBaha;
    }

    public void setAhadSaierOrAbBaha(String ahadSaierOrAbBaha) {
        this.ahadSaierOrAbBaha = ahadSaierOrAbBaha;
        notifyPropertyChanged(BR.ahadSaierOrAbBaha);
    }

    @Bindable
    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
        notifyPropertyChanged(BR.mobiles);
    }

    @Bindable
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        notifyPropertyChanged(BR.mobile);
    }

    public boolean isJustMobile() {
        return justMobile;
    }

    public void setJustMobile(boolean justMobile) {
        this.justMobile = justMobile;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Bindable
    public int getSerialVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(SERIAL.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAddressVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(ADDRESS.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAccountVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel().
                getBoolData(ACCOUNT.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAhadEmptyVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(AHAD_EMPTY.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getDescriptionVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(DESCRIPTION.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAhadVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(SHOW_AHAD_TITLE.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAhad1Visibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(AHAD_1.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAhad2Visibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(AHAD_2.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getAhadTotalVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(AHAD_TOTAL.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getMobileVisibility() {
        return isJustMobile() ? View.VISIBLE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(MOBILE.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getDebtVisibility() {
        return isJustMobile() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getFatherNameVisibility() {
        return isJustMobile() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getOldRadifVisibility() {
        return isJustMobile() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getOldEshterakVisibility() {
        return isJustMobile() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getReadingReportVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(READING_REPORT.getValue()) ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public int getKarbariVisibility() {
        return isJustMobile() ? View.GONE : getApplicationComponent().SharedPreferenceModel()
                .getBoolData(KARBARI.getValue()) ? View.VISIBLE : View.GONE;
    }
}
