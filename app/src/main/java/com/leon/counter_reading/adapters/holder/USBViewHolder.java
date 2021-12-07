package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.adapters.recyclerview.RecyclerItemClickListener;

public class USBViewHolder extends RecyclerView.ViewHolder {
    public final View globalView;
    public final TextView title, summary;
    public final ImageView type;

    public USBViewHolder(View view, RecyclerItemClickListener itemClickListener) {
        super(view);
        globalView = view;
        title = view.findViewById(android.R.id.title);
        summary = view.findViewById(android.R.id.summary);
        type = view.findViewById(android.R.id.icon);
        view.setOnKeyListener((view1, keyCode, keyEvent) ->
                itemClickListener.handleDPad(view1, keyEvent));
    }
}
