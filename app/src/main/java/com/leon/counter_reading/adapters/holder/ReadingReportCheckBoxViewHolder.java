package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.CheckedTextView;

public class ReadingReportCheckBoxViewHolder {
    public final CheckedTextView checkBox;

    public ReadingReportCheckBoxViewHolder(View view) {
        checkBox = view.findViewById(android.R.id.text1);
    }
}
