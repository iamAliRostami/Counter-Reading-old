package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.utils.USBUtils.getMimetype;
import static com.leon.counter_reading.utils.USBUtils.loadMimeIcon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.github.mjdev.libaums.fs.UsbFile;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.USBViewHolder;
import com.leon.counter_reading.adapters.recycler_view.RecyclerUsbItemClickListener;
import com.leon.counter_reading.fragments.ExplorerFragment;
import com.leon.counter_reading.utils.USBUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.jahnen.libaums.fs.UsbFile;

public class UsbFilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<UsbFile> usbFiles = new ArrayList<>();
    private final RecyclerUsbItemClickListener itemClickListener;
    private final Context context;
    private UsbFile currentDir;

    public UsbFilesAdapter(Context context, UsbFile usbFile, RecyclerUsbItemClickListener itemClickListener) throws IOException {
        this.context = context;
        this.itemClickListener = itemClickListener;
        currentDir = usbFile;
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refresh();
    }

    public static boolean isText(UsbFile entry) {
        if (entry.isDirectory())
            return false;
        try {
            return isTextInner(entry.getName());
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isText(File entry) {
        return isTextInner(entry.getName());
    }

    private static boolean isTextInner(String name) {
        boolean result = false;
        int index = name.lastIndexOf(".");
        if (index > 0) {
            String ext = name.substring(index);
            if (ext.equalsIgnoreCase(".txt")) {
                result = true;
            }
        }
        return result;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh() throws IOException {
        usbFiles.clear();
        usbFiles.addAll(Arrays.asList(currentDir.listFiles()));
        Collections.sort(usbFiles, USBUtils.comparator);
        if (!ExplorerFragment.sortAsc)
            Collections.reverse(usbFiles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public USBViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore, parent, false);
        return new USBViewHolder(itemView, itemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof USBViewHolder) {
            final UsbFile file = usbFiles.get(position);
            final USBViewHolder holder = (USBViewHolder) viewHolder;
            if (file.isDirectory()) {
                holder.type.setImageResource(R.drawable.ic_folder_alpha);
            } else {
                final int index = file.getName().lastIndexOf(".");
                if (index > 0) {
                    String ext = file.getName().substring(index + 1);
                    holder.type.setImageResource(loadMimeIcon(getMimetype(ext.toLowerCase())));
                }
            }
            holder.title.setText(file.getName());
            final DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault());
            final String date = dateFormat.format(new Date(file.lastModified()));
            try {
                holder.summary.setText("Last modified: " + date + " - " +
                        Formatter.formatFileSize(context, file.getLength()));
            } catch (Exception e) {
                holder.summary.setText("Last modified: " + date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return usbFiles.size();
    }

    public UsbFile getItem(int position) {
        return usbFiles.get(position);
    }

    public UsbFile getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(UsbFile dir) {
        currentDir = dir;
    }
}
