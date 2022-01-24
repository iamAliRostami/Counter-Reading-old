package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.activities.TakePhotoActivity.replace;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.TakePhotoActivity;
import com.leon.counter_reading.adapters.holder.ImageViewHolder;
import com.leon.counter_reading.fragments.dialog.HighQualityFragment;
import com.leon.counter_reading.fragments.dialog.ShowFragmentDialog;
import com.leon.counter_reading.fragments.dialog.TakePhotoFragment;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.util.ArrayList;

public class ImageViewAdapterOld extends BaseAdapter {
    private final ArrayList<Image> images;
    private final LayoutInflater inflater;
    private final Context context;

    public ImageViewAdapterOld(Context context, ArrayList<Image> images) {
        this.images = images;
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return DifferentCompanyManager.getImageNumber(DifferentCompanyManager.getActiveCompanyName());
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

        holder.imageView.setEnabled(position >= images.size() || !images.get(position).isSent);
        holder.imageView.setOnClickListener(view1 -> {
            holder.imageView.setEnabled(false);
            replace = position < images.size() ? position + 1 : 0;
            imagePicker();
        });
        if (position < images.size()) {
            final Bitmap[] bitmap;
            bitmap = new Bitmap[]{images.get(position).bitmap};
            holder.imageView.setImageBitmap(bitmap[0]);
            holder.textViewSize.setText(String.valueOf(images.get(position).size / 1024).concat(" کیلوبایت"));
            holder.imageView.setOnLongClickListener(v -> {
                if (bitmap[0] != null) {
                    ShowFragmentDialog.ShowFragmentDialogOnce(context, "Image # 1",
                            HighQualityFragment.newInstance(bitmap[0]));
                }
                return false;
            });
            holder.imageViewDelete.setOnClickListener(v -> {
                getApplicationComponent().MyDatabase()
                        .imageDao().deleteImage(images.get(position).id);
                images.remove(position);
                notifyDataSetChanged();
                bitmap[0] = null;
            });
        } else {
            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.img_camera));
        }
        return view;
    }

    private void imagePicker() {
        ((TakePhotoActivity) (context)).openSomeActivityForResult();
    }
}

