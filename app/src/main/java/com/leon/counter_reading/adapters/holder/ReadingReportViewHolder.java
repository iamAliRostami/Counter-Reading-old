package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReadingReportViewHolder extends RecyclerView.ViewHolder {
    public final CheckedTextView checkBox;
    public ReadingReportViewHolder(@NonNull View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(android.R.id.text1);
    }
}
