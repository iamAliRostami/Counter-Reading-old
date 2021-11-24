package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.utils.USBUtils.getMimetype;
import static com.leon.counter_reading.utils.USBUtils.loadMimeIcon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mjdev.libaums.fs.UsbFile;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.recyclerview.RecyclerItemClickListener;
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

public class UsbFilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UsbFile> mFiles;
    private UsbFile mCurrentDir;
    private final RecyclerItemClickListener mRecyclerItemClickListener;
    private final Context mContext;

    public class USBViewHolder extends RecyclerView.ViewHolder {
        final View globalView;
        final TextView title, summary;
        final ImageView type;

        public USBViewHolder(View view) {
            super(view);
            globalView = view;
            title = view.findViewById(android.R.id.title);
            summary = view.findViewById(android.R.id.summary);
            type = view.findViewById(android.R.id.icon);

            view.setOnKeyListener((view1, keyCode, keyEvent) -> {
                boolean handled;
                handled = mRecyclerItemClickListener.handleDPad(view1, keyCode, keyEvent);
                return handled;
            });
        }
    }

    public UsbFilesAdapter(Context context, UsbFile dir, RecyclerItemClickListener itemClickListener) throws IOException {
        mContext = context;
        mCurrentDir = dir;
        mRecyclerItemClickListener = itemClickListener;
        mFiles = new ArrayList<>();
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refresh();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh() throws IOException {
        mFiles = Arrays.asList(mCurrentDir.listFiles());
        Collections.sort(mFiles, USBUtils.comparator);

        if (!ExplorerFragment.mSortAsc)
            Collections.reverse(mFiles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public USBViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new USBViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof USBViewHolder) {
            UsbFile file = mFiles.get(position);

            USBViewHolder holder = (USBViewHolder) viewHolder;

            if (file.isDirectory()) {
                holder.type.setImageResource(R.drawable.ic_folder_alpha);
            } else {
                int index = file.getName().lastIndexOf(".");
                if (index > 0) {
//                    String prefix = file.getName().substring(0, index);
                    String ext = file.getName().substring(index + 1);
                    holder.type.setImageResource(loadMimeIcon(getMimetype(ext.toLowerCase())));
                }
            }
            holder.title.setText(file.getName());
            DateFormat date_format = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault());
            String date = date_format.format(new Date(file.lastModified()));

            // If it's a directory, we can't get size info
            try {
                holder.summary.setText("Last modified: " + date + " - " +
                        Formatter.formatFileSize(mContext, file.getLength()));
            } catch (Exception e) {
                holder.summary.setText("Last modified: " + date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public UsbFile getItem(int position) {
        return mFiles.get(position);
    }

    public UsbFile getCurrentDir() {
        return mCurrentDir;
    }

    public void setCurrentDir(UsbFile dir) {
        mCurrentDir = dir;
    }

    public List<UsbFile> getFiles() {
        return mFiles;
    }

    public ArrayList<UsbFile> getImageFiles() {
        ArrayList<UsbFile> result = new ArrayList<>();
        for (UsbFile file : mFiles) {
            if (isText(file))
                result.add(file);
        }
        return result;
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
}