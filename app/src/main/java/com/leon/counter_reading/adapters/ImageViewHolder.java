package com.leon.counter_reading.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.counter_reading.R;

public class ImageViewHolder {
    public final ImageView imageView;
    public final ImageView imageViewDelete;
    public final ImageView imageViewSent;
    public final TextView textViewSize;

    public ImageViewHolder(View view) {
        imageView = view.findViewById(R.id.image_view);
        imageViewSent = view.findViewById(R.id.image_View_sent);
        imageViewDelete = view.findViewById(R.id.image_View_delete);
        textViewSize = view.findViewById(R.id.text_view_size);
    }
}
