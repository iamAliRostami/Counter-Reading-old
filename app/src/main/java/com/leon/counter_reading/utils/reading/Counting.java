package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.enums.CompanyNames.ESF;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.NORMAL;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.CalendarTool.findDifferentDays;

import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;

public class Counting {

    public static double dailyAverage(int preNumber, int currentNumber, String preDate) {
        return (currentNumber - preNumber) / (double) findDifferentDays(preDate);
    }

    public static double monthlyAverage(int preNumber, int currentNumber, String preDate) {
        return dailyAverage(preNumber, currentNumber, preDate) * 30;
    }

    public static double monthlyAverage(int preNumber, int currentNumber, String preDate, int zarib) {
        return dailyAverage(preNumber, currentNumber, preDate) * 30 / zarib != 0 ? zarib : 1;
    }

    public static int checkHighLow(OnOffLoadDto onOffLoadDto, KarbariDto karbariDto,
                                   ReadingConfigDefaultDto readingConfigDefaultDto,
                                   int currentNumber) {
        double average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate);
        double preAverage = onOffLoadDto.preAverage;
        int difference = currentNumber - onOffLoadDto.preNumber;
        /*
          if (karbariDto.isMaskooni && karbariDto.isTejari) {
         // فرمول میانگین گیری عجیب احتمالا
         } else*/
        if (karbariDto.isMaskooni) {
            /*
              ضرایب
             */
            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate, onOffLoadDto.ahadMaskooniOrAsli);

            if (readingConfigDefaultDto.highConstBoundMaskooni < difference)
                return HIGH.getValue();
            else if (readingConfigDefaultDto.lowConstBoundMaskooni > difference)
                return LOW.getValue();
            double highBoundMaskooni = (preAverage + ((double) readingConfigDefaultDto.highPercentBoundMaskooni / 100) * preAverage);
            if (highBoundMaskooni < average)
                return HIGH.getValue();
            double lowBoundMaskooni = (preAverage - ((double) readingConfigDefaultDto.lowPercentBoundMaskooni / 100) * preAverage);
            if (lowBoundMaskooni > average)
                return LOW.getValue();
        } else if (karbariDto.isTejari) {
            /*
             * محاسبه فقط تجاری ساده با ظرفیت
             */
//            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate/*, onOffLoadDto.ahadTejariOrFari*/);
            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate,
                    getActiveCompanyName() == ESF ? onOffLoadDto.ahadTejariOrFari : onOffLoadDto.ahadMaskooniOrAsli);
            double lowBoundRate = onOffLoadDto.zarfiat -
                    ((double) readingConfigDefaultDto.lowPercentZarfiatBound / 100) * onOffLoadDto.zarfiat;

            if (average < lowBoundRate)
                return LOW.getValue();
            double highBoundRate = onOffLoadDto.zarfiat +
                    ((double) readingConfigDefaultDto.highPercentZarfiatBound / 100) * onOffLoadDto.zarfiat;

            if (average > highBoundRate)
                return HIGH.getValue();

            if (readingConfigDefaultDto.highConstZarfiatBound < difference)
                return HIGH.getValue();
            else if (readingConfigDefaultDto.lowConstZarfiatBound > difference)
                return LOW.getValue();
        } else if (karbariDto.isSaxt || onOffLoadDto.noeVagozariId == 4) {
            if (readingConfigDefaultDto.highConstBoundSaxt < difference)
                return HIGH.getValue();
            else if (readingConfigDefaultDto.lowConstBoundSaxt > difference)
                return LOW.getValue();
            else if ((preAverage + ((double) readingConfigDefaultDto.highPercentBoundSaxt / 100) * preAverage) < average)
                return HIGH.getValue();
            else if ((preAverage - ((double) readingConfigDefaultDto.lowPercentBoundSaxt / 100)) * preAverage > average)
                return LOW.getValue();
        } else {
            if ((preAverage + ((double) readingConfigDefaultDto.highPercentRateBoundNonMaskooni / 100)) * preAverage < average)
                return HIGH.getValue();
            else if ((preAverage - ((double) readingConfigDefaultDto.lowPercentRateBoundNonMaskooni / 100) * preAverage > average))
                return LOW.getValue();
        }
        return NORMAL.getValue();
    }

    public static int checkHighLowMakoos(OnOffLoadDto onOffLoadDto, KarbariDto karbariDto,
                                         ReadingConfigDefaultDto readingConfigDefaultDto,
                                         int currentNumber) {
        final int temp = onOffLoadDto.preNumber;
        onOffLoadDto.preNumber = currentNumber;
        currentNumber = temp;
        return checkHighLow(onOffLoadDto, karbariDto, readingConfigDefaultDto, currentNumber);
    }
}