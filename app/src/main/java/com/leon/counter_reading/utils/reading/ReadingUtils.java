package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.enums.CompanyNames.ZONE6;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.ReadingData;

public class ReadingUtils {
    /**
     * Create above image icons resource
     **/
    static public int[] setAboveIcons() {
        final int[] imageSrc = new int[15];
        imageSrc[0] = R.drawable.img_default_level;
        imageSrc[1] = R.drawable.img_normal_level;
        imageSrc[2] = R.drawable.img_low_level;
        imageSrc[3] = R.drawable.img_high_level;
        imageSrc[4] = R.drawable.img_low_level;
        imageSrc[5] = R.drawable.img_visit_default;
        imageSrc[6] = R.drawable.img_visit;
        imageSrc[7] = R.drawable.img_writing;
        imageSrc[8] = R.drawable.img_successful_default;
        imageSrc[9] = R.drawable.img_successful;
        imageSrc[10] = R.drawable.img_mistake;
        imageSrc[11] = R.drawable.img_failure;
        imageSrc[12] = R.drawable.img_delete_temp;
        imageSrc[13] = R.drawable.img_construction;
        imageSrc[14] = R.drawable.img_broken_pipe;
        return imageSrc;
    }

    static public int setExceptionImage(ReadingData readingData, int position) {
        if (readingData.onOffLoadDtos.get(position).displayIcons) {
            try {
                for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
                    if (readingData.counterStateDtos.get(i).isXarab &&
                            readingData.counterStateDtos.get(i).moshtarakinId ==
                                    readingData.onOffLoadDtos.get(position).preCounterStateCode) {
                        //TODO remove is xarab in zone 6
                        return BuildConfig.COMPANY_NAME == ZONE6 ? -1 : 14;
                    }
                }

                if (readingData.onOffLoadDtos.get(position).hazf > 0) {
                    return 12;
                }
                for (int i = 0; i < readingData.karbariDtos.size(); i++) {
                    if (readingData.karbariDtos.get(i).isSaxt &&
                            readingData.karbariDtos.get(i).moshtarakinId ==
                                    readingData.onOffLoadDtos.get(position).karbariCode) {
                        return 13;
                    }
                }
                if (readingData.onOffLoadDtos.get(position).noeVagozariId == 4) {
                    return 13;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}