package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.R;

public class DrawerViewHolder extends RecyclerView.ViewHolder {
    public final TextView textViewTitle;
    public final ImageView imageViewIcon;
    public final LinearLayout linearLayout;

    public DrawerViewHolder(View viewItem) {
        super(viewItem);
        this.textViewTitle = viewItem.findViewById(R.id.text_view_title);
        this.imageViewIcon = viewItem.findViewById(R.id.image_view_icon);
        this.linearLayout = viewItem.findViewById(R.id.linear_layout_background);
    }
}
