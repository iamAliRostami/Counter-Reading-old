package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.CheckedTextView;

public class ReadingReport1ViewHolder {
    public final CheckedTextView checkBox;

    public ReadingReport1ViewHolder(View view) {
        checkBox = view.findViewById(android.R.id.text1);
    }
}
