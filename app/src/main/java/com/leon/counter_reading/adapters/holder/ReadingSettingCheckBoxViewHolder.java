package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leon.counter_reading.R;

public class ReadingSettingCheckBoxViewHolder {
    public final CheckedTextView checkBox;
    public final LinearLayout linearLayout;
    public final TextView textViewTrackNumber;
    public final TextView textViewZoneTitle;
    public final TextView textViewStartDate;
    public final TextView textViewEndDate;
    public final TextView textViewStartEshterak;
    public final TextView textViewEndEshterak;
    public final TextView textViewNumber;
    public final ImageView imageViewMap1;
    public final ImageView imageViewMap2;

    public ReadingSettingCheckBoxViewHolder(View view) {
        checkBox = view.findViewById(android.R.id.text1);
        linearLayout = view.findViewById(R.id.linear_layout);
        textViewEndDate = view.findViewById(R.id.text_view_end_date);
        textViewStartDate = view.findViewById(R.id.text_view_start_date);
        textViewEndEshterak = view.findViewById(R.id.text_view_end_eshterak);
        textViewStartEshterak = view.findViewById(R.id.text_view_start_eshterak);
        textViewTrackNumber = view.findViewById(R.id.text_view_track_number);
        textViewZoneTitle = view.findViewById(R.id.text_view_zone_title);
        textViewNumber = view.findViewById(R.id.text_view_number);
        imageViewMap1 = view.findViewById(R.id.image_view_map_1);
        imageViewMap2 = view.findViewById(R.id.image_view_map_2);
    }
}
