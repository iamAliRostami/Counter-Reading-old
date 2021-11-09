package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.dialog.RoadMapFragment;
import com.leon.counter_reading.fragments.dialog.ShowFragmentDialog;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class ReadingSettingCustomAdapter extends BaseAdapter {
    private final ArrayList<TrackingDto> trackingDtos;
    private final LayoutInflater inflater;
    private int zoneId;
    private final Context context;

    public ReadingSettingCustomAdapter(Context context, ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos = trackingDtos;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return trackingDtos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (position % 2 == 1)
                convertView = inflater.inflate(R.layout.item_reading_setting_1, null);
            else
                convertView = inflater.inflate(R.layout.item_reading_setting_2, null);
        }
        final TrackingDto trackingDto = trackingDtos.get(position);
        ReadingSettingCheckBoxViewHolder holder = new ReadingSettingCheckBoxViewHolder(convertView);
        holder.textViewTrackNumber.setText(String.valueOf(trackingDto.trackNumber));
        holder.textViewZoneTitle.setText(trackingDto.zoneTitle);
        holder.textViewStartEshterak.setText(trackingDto.fromEshterak);
        holder.textViewEndEshterak.setText(trackingDto.toEshterak);
        holder.textViewStartDate.setText(trackingDto.fromDate);
        holder.textViewEndDate.setText(trackingDto.toDate);
        holder.textViewNumber.setText(String.valueOf(trackingDto.itemQuantity));

        //TODO
        if (zoneId > 0 && trackingDtos.get(position).zoneId != zoneId && trackingDtos.get(position).isActive) {
            MyApplication.getApplicationComponent().MyDatabase().
                    trackingDao().updateTrackingDtoByStatus(trackingDtos.get(position).id, false);
            trackingDtos.get(position).isActive = false;
        } else if (trackingDtos.get(position).isActive)
            zoneId = trackingDtos.get(position).zoneId;

        holder.linearLayout.setOnClickListener(view1 -> {
            if (zoneId > 0 && zoneId != trackingDtos.get(position).zoneId && !holder.checkBox.isChecked()) {
                new CustomToast().warning(MyApplication.getContext().getString(R.string.single_zone));
            } else {
                trackingDtos.get(position).isActive = !holder.checkBox.isChecked();
                zoneId = 0;
                MyApplication.getApplicationComponent().MyDatabase().
                        trackingDao().updateTrackingDtoByStatus(trackingDtos.get(position).id,
                        trackingDtos.get(position).isActive);
                notifyDataSetChanged();
            }
        });
        holder.imageViewMap1.setOnClickListener(v -> {
            if (checkLocation(trackingDto)) {
                ShowFragmentDialog.ShowFragmentDialogOnce(context, "ROAD_MAP_DIALOG", RoadMapFragment
                        .newInstance(trackingDtos.get(position).x, trackingDtos.get(position).y));
            }
        });
        holder.imageViewMap2.setOnClickListener(v -> {
            if (checkLocation(trackingDto)) {
                String uriString = "geo:" + trackingDtos.get(position).y + "," + trackingDtos.get(position).x + "?q=" +
                        Uri.encode(trackingDtos.get(position).y + "," + trackingDtos.get(position).x + "(label)") + "&z=19";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        holder.checkBox.setChecked(trackingDtos.get(position).isActive);
        return convertView;
    }

    private boolean checkLocation(TrackingDto trackingDto) {
        if (trackingDto.y != null && trackingDto.y.length() > 0 && !trackingDto.y.equals("0"))
            return true;
        new CustomToast().warning(context.getString(R.string.location_not_found));
        return false;
    }
}

class ReadingSettingCheckBoxViewHolder {
    final CheckedTextView checkBox;
    final LinearLayout linearLayout;
    final TextView textViewTrackNumber;
    final TextView textViewZoneTitle;
    final TextView textViewStartDate;
    final TextView textViewEndDate;
    final TextView textViewStartEshterak;
    final TextView textViewEndEshterak;
    final TextView textViewNumber;
    final ImageView imageViewMap1;
    final ImageView imageViewMap2;

    ReadingSettingCheckBoxViewHolder(View view) {
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