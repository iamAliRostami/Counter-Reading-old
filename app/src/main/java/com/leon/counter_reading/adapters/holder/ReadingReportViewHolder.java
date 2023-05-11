package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.CheckedTextView;

public class ReadingReportViewHolder {
    public final CheckedTextView checkBox;

    public ReadingReportViewHolder(View view) {
        checkBox = view.findViewById(android.R.id.text1);
    }
}
