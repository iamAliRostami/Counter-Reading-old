package com.leon.counter_reading.adapters.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.leon.counter_reading.R;

public class PossibleViewHolder extends RecyclerView.ViewHolder {
    public final MaterialCheckBox checkBox;

    public PossibleViewHolder(@NonNull View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.check_box_possible);
    }
}
