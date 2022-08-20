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
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.leon.counter_reading.BR;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.util.ArrayList;

public class PossibleViewModel extends BaseObservable {
    private ArrayList<CounterReportDto> counterReportDtos = new ArrayList<>();
    private ArrayList<OffLoadReport> offLoadReports = new ArrayList<>();
    private ArrayList<KarbariDto> karbariDtosTemp = new ArrayList<>();
    private ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    private OnOffLoadDto onOffLoadDto;
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


    private String ahadTotal;
    private String ahad1;
    private String ahad2;

    boolean justMobile;

    public PossibleViewModel(boolean justMobile) {
        this.justMobile = justMobile;
    }

    @Bindable
    public String getAhad1Title() {
        return String.format("%s:", DifferentCompanyManager.getAhad1(getActiveCompanyName()));
    }

    @Bindable
    public String getAhad2Title() {
        return String.format("%s:", DifferentCompanyManager.getAhad2(getActiveCompanyName())
                .replaceFirst("آحاد ", "").replaceFirst("واحد", ""));
    }

    @Bindable
    public String getAhadTotalTitle() {
        return String.format("%s:", DifferentCompanyManager.getAhadTotal(getActiveCompanyName())
                .replaceFirst("آحاد ", "").replaceFirst("واحد", ""));
    }

    @Bindable
    public String getAhadEmptyTitle() {
        return DifferentCompanyManager.getAhad(getActiveCompanyName()).replaceFirst("آحاد ", "")
                .replaceFirst("واحد", "");
    }

    @Bindable
    public String getAhad1Hint() {
        return DifferentCompanyManager.getAhad1(getActiveCompanyName());
    }

    @Bindable
    public String getAhad2Hint() {
        return DifferentCompanyManager.getAhad2(getActiveCompanyName());
    }

    @Bindable
    public String getAhadTotalHint() {
        return DifferentCompanyManager.getAhadTotal(getActiveCompanyName());
    }

    @Bindable
    public String getAhadEmptyHint() {
        return DifferentCompanyManager.getAhad(getActiveCompanyName())
                .concat(getContext().getString(R.string.empty));
    }

    @Bindable
    public int getEshterakMaxLength() {
        return DifferentCompanyManager.getEshterakMaxLength(getActiveCompanyName());
    }

    @Bindable
    public ArrayList<CounterReportDto> getCounterReportDtos() {
        return counterReportDtos;
    }

    public void setCounterReportDtos(ArrayList<CounterReportDto> counterReportDtos) {
        this.counterReportDtos = counterReportDtos;
        notifyPropertyChanged(BR.counterReportDtos);
    }

    @Bindable
    public ArrayList<OffLoadReport> getOffLoadReports() {
        return offLoadReports;
    }

    public void setOffLoadReports(ArrayList<OffLoadReport> offLoadReports) {
        this.offLoadReports = offLoadReports;
        notifyPropertyChanged(BR.offLoadReports);
    }

    @Bindable
    public ArrayList<KarbariDto> getKarbariDtosTemp() {
        return karbariDtosTemp;
    }

    public void setKarbariDtosTemp(ArrayList<KarbariDto> karbariDtosTemp) {
        this.karbariDtosTemp = karbariDtosTemp;
        notifyPropertyChanged(BR.karbariDtosTemp);
    }

    @Bindable
    public ArrayList<KarbariDto> getKarbariDtos() {
        return karbariDtos;
    }

    public void setKarbariDtos(ArrayList<KarbariDto> karbariDtos) {
        this.karbariDtos = karbariDtos;
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

    @Bindable
    public String getAhadTotal() {
        return ahadTotal;
    }

    public void setAhadTotal(String ahadTotal) {
        this.ahadTotal = ahadTotal;
        notifyPropertyChanged(BR.ahadTotal);
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

    public boolean isJustMobile() {
        return justMobile;
    }

    public void setJustMobile(boolean justMobile) {
        this.justMobile = justMobile;
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
