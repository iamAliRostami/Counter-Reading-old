package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "OnOffLoadDto", indices = @Index(value = {"customId"}, unique = true))
public class OnOffLoadDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public String id;
    public String billId;
    public int radif;
    public String oldRadif;
    public String eshterak;
    public String oldEshterak;
    public String qeraatCode;
    public String firstName;
    public String sureName;
    public String fatherName;
    public String address;
    public String pelak;
    public int karbariCode;
    public int ahadMaskooniOrAsli;
    public int ahadTejariOrFari;
    public int ahadSaierOrAbBaha;
    public int qotrCode;
    public int sifoonQotrCode;

    public String postalCode;
    public int preNumber;
    public String preDate;
    public String preDateMiladi;
    public double preAverage;
    public int preCounterStateCode;//TODO join counter state is xarab
    public String counterSerial;
    public String counterInstallDate;
    public String tavizDate;
    public String tavizNumber;
    public String trackingId;
    public int trackNumber;
    public int zarfiat;
    public String mobile;
    public String mobiles;
    public int hazf;//TODO 0 <  hazf movaqat
    public int noeVagozariId;//TODO 4: sax o saz or karbari isSaxt
    public Integer counterNumber;
    public int counterStateId;
    public int possibleKarbariCode;
    public Integer possibleAhadMaskooniOrAsli;
    public Integer possibleAhadTejariOrFari;
    public Integer possibleAhadSaierOrAbBaha;
    public Integer possibleEmpty;
    public String possibleAddress;
    public String possibleCounterSerial;
    public String possibleEshterak;
    public String possibleMobile;
    public String possiblePhoneNumber;
    public String description;
    public String phoneDateTime;
    public String locationDateTime;
    public String d1;
    public String d2;
    public int offLoadStateId;
    public int zoneId;
    public double gisAccuracy;
    public double x;
    public double y;
    public boolean counterNumberShown;

    public int attemptCount;
    public boolean isLocked;
    public boolean isDeleted;

    public int highLowStateId;
    public boolean isBazdid;
    public Integer counterStatePosition;
    public Integer guildId;
    public long balance;
    public int preGuildCode;

    @Ignore
    public String qotr;
    @Ignore
    public String sifoonQotr;

    @Ignore
    public boolean hasImage;
    @Ignore
    public boolean displayPreNumber;
    @Ignore
    public boolean displayDebt;
    @Ignore
    public boolean displayMobile;
    @Ignore
    public boolean displayBillId;
    @Ignore
    public boolean displayRadif;
    @Ignore
    public boolean displayPreDate;
    @Ignore
    public boolean displayIcons;

    public static class OffLoad {
        public String id;
        public Integer counterNumber;
        public int counterStateId;
        public String possibleAddress;
        public String possibleCounterSerial;
        public String possibleEshterak;
        public String possibleMobile;
        public String possiblePhoneNumber;

        public int highLowStateId;


        public Integer possibleAhadMaskooniOrAsli;
        public Integer possibleAhadTejariOrFari;
        public Integer possibleAhadSaierOrAbBaha;
        public Integer possibleEmpty;
        public Integer guildId;
        public int possibleKarbariCode;
        public String description;
        public boolean counterNumberShown;
        public boolean isLocked;
        public double gisAccuracy;
        public double x;
        public double y;
        public String d1;
        public String d2;
        public int attemptCount;
        public String phoneDateTime;
        public String locationDateTime;

        public OffLoad() {
        }

        public OffLoad(OnOffLoadDto onOffLoadDto) {
            id = onOffLoadDto.id;
            counterNumber = onOffLoadDto.counterNumber;
            counterStateId = onOffLoadDto.counterStateId;
            highLowStateId = onOffLoadDto.highLowStateId;
            possibleAddress = onOffLoadDto.possibleAddress;
            possibleCounterSerial = onOffLoadDto.possibleCounterSerial;
            possibleEshterak = onOffLoadDto.possibleEshterak;
            possibleMobile = onOffLoadDto.possibleMobile;
            possiblePhoneNumber = onOffLoadDto.possiblePhoneNumber;
            possibleAhadMaskooniOrAsli = onOffLoadDto.possibleAhadMaskooniOrAsli;
            possibleAhadSaierOrAbBaha = onOffLoadDto.possibleAhadSaierOrAbBaha;
            possibleAhadTejariOrFari = onOffLoadDto.possibleAhadTejariOrFari;
            possibleEmpty = onOffLoadDto.possibleEmpty;
            possibleKarbariCode = onOffLoadDto.possibleKarbariCode;
            description = onOffLoadDto.description;
            counterNumberShown = onOffLoadDto.counterNumberShown;
            guildId = onOffLoadDto.guildId;
            x = onOffLoadDto.x;
            y = onOffLoadDto.y;
            d1 = onOffLoadDto.d1;
            d2 = onOffLoadDto.d2;
            gisAccuracy = onOffLoadDto.gisAccuracy;
            attemptCount = onOffLoadDto.attemptCount;
            isLocked = onOffLoadDto.isLocked;

            phoneDateTime = onOffLoadDto.phoneDateTime;
            locationDateTime = onOffLoadDto.locationDateTime;
        }
    }

    public void updateIgnore(final OnOffLoadDto onOffLoadDto) {
        displayBillId = onOffLoadDto.displayBillId;
        displayRadif = onOffLoadDto.displayRadif;
        displayPreNumber = onOffLoadDto.displayPreNumber;
        displayDebt = onOffLoadDto.displayDebt;
        hasImage = onOffLoadDto.hasImage;
        displayMobile = onOffLoadDto.displayMobile;
        displayPreDate = onOffLoadDto.displayPreDate;
        displayIcons = onOffLoadDto.displayIcons;
        qotr = onOffLoadDto.qotr;
        sifoonQotr = onOffLoadDto.sifoonQotr;
    }

    public static class OffLoadData {
        public final ArrayList<OffLoadReport> offLoadReports;
        public final ArrayList<OffLoad> offLoads;
        public int finalTrackNumber;
        public boolean isFinal;

        public OffLoadData() {
            offLoadReports = new ArrayList<>();
            offLoads = new ArrayList<>();
        }
    }

    public static class OffLoadResponses {
        public int status;
        public String message;
        public String generationDateTime;
        public boolean isValid;
        public String[] targetObject;
    }
}
