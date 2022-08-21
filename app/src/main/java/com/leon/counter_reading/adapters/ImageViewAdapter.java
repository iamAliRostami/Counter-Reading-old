package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getContext;
import static com.leon.counter_reading.utils.CustomFile.loadImage;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getImageNumber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.ImageViewHolder;
import com.leon.counter_reading.fragments.dialog.HighQualityFragment;
import com.leon.counter_reading.tables.Image;

import java.util.ArrayList;

public class ImageViewAdapter extends BaseAdapter {
    private final ArrayList<Image> images;
    private final LayoutInflater inflater;
    private final Context context;


    public ImageViewAdapter(Context context, ArrayList<Image> images) {
        this.images = images;
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return getImageNumber(getActiveCompanyName());
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return images.size();
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image, null);
        }
        final ImageViewHolder holder = new ImageViewHolder(view);
        holder.imageViewDelete.setVisibility(position < images.size() && !images.get(position).isSent ?
                View.VISIBLE : View.GONE);
        holder.imageViewSent.setVisibility(position < images.size() && images.get(position).isSent ?
                View.VISIBLE : View.GONE);
        if (position < images.size()) {
            holder.imageView.setImageBitmap(loadImage(getContext(), images.get(position).address));
            holder.textViewSize.setText(String.valueOf(images.get(position).size / 1024).concat(" کیلوبایت"));
            holder.imageViewDelete.setOnClickListener(v -> {
                getApplicationComponent().MyDatabase().imageDao().deleteImage(images.get(position).id);
                images.remove(position);
                notifyDataSetChanged();
            });
        } else {
            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.img_camera));
            holder.textViewSize.setText("");
        }
        return view;
    }

    public void showImageHighQuality(int position) {
        if (position < images.size() && images.get(position).address != null) {
            ShowDialogOnce(context, "Image # 1", HighQualityFragment
                    .newInstance(loadImage(getContext(), images.get(position).address)));
        }
    }

    public int setReplace(int position) {
        if (getImageNumber(BuildConfig.COMPANY_NAME) == images.size()) {
            boolean unsent = false;
            int i = 0;
            while (i < images.size() && !unsent) {
                if (!images.get(i).isSent)
                    unsent = true;
                i++;
            }
            if (!unsent)
                return -1;
        }
        if (position >= images.size() || !images.get(position).isSent)
            return position < images.size() ? position + 1 : 0;
        return 0;
    }
}