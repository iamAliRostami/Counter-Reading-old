package com.leon.counter_reading.adapters.holder;

import static com.leon.counter_reading.activities.TakePhotoActivity.replace;
import static com.leon.counter_reading.helpers.Constants.CAMERA_REQUEST;
import static com.leon.counter_reading.helpers.Constants.PHOTO_URI;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.TakePhotoActivity;
import com.leon.counter_reading.adapters.ImageViewHolder;
import com.leon.counter_reading.fragments.dialog.HighQualityFragment;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageViewAdapter extends BaseAdapter {
    private final ArrayList<Image> images;
    private final LayoutInflater inflater;
    private final Context context;

    public ImageViewAdapter(Context c, ArrayList<Image> images) {
        this.images = images;
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        holder.imageView.setOnClickListener(view1 -> {
            replace = position < images.size() ? position + 1 : 0;
            imagePicker();
        });
        if (position < images.size()) {
            final Bitmap[] bitmap;
            bitmap = new Bitmap[]{images.get(position).bitmap};
            holder.imageView.setImageBitmap(bitmap[0]);
            holder.imageView.setOnLongClickListener(v -> {
                if (bitmap[0] != null) {
                    FragmentTransaction fragmentTransaction =
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    HighQualityFragment highQualityFragment =
                            HighQualityFragment.newInstance(bitmap[0]);
                    highQualityFragment.show(fragmentTransaction, "Image # 1");
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
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                PHOTO_URI = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID.concat(".provider"), photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_URI);
                try {
                    ((TakePhotoActivity) (context)).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
//        builder.setTitle(R.string.choose_document);
//        builder.setMessage(R.string.select_source);
//        builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
//            dialog.dismiss();
//            Intent intent = new Intent("android.intent.action.PICK");
//            intent.setType("image/*");
//            ((TakePhotoActivity) (context)).startActivityForResult(intent, GALLERY_REQUEST);
//        });
//        builder.setNegativeButton(R.string.camera, (dialog, which) -> {
//            dialog.dismiss();
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
//                // Create the File where the photo should go
//                File photoFile = null;
//                try {
//                    photoFile = createImageFile(context);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                // Continue only if the File was successfully created
//                if (photoFile != null) {
//                    PHOTO_URI = FileProvider.getUriForFile(context,
//                            BuildConfig.APPLICATION_ID.concat(".provider"),
//                            photoFile);
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_URI);
//                    try {
//                        ((TakePhotoActivity) (context)).startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
////        builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
//        builder.create().show();
    }
}
