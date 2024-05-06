package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.ReadingSettingViewHolder;
import com.leon.counter_reading.fragments.dialog.RoadMapFragment;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class ReadingSettingAdapter extends BaseAdapter {
    private final ArrayList<TrackingDto> trackingDtos;
    private final LayoutInflater inflater;
    private final Context context;
    private int zoneId;

    public ReadingSettingAdapter(Context context, ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos = trackingDtos;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final ReadingSettingViewHolder holder = new ReadingSettingViewHolder(convertView);
        holder.textViewTrackNumber.setText(String.valueOf(trackingDto.trackNumber));
        holder.textViewZoneTitle.setText(trackingDto.zoneTitle);
        holder.textViewStartEshterak.setText(trackingDto.fromEshterak);
        holder.textViewEndEshterak.setText(trackingDto.toEshterak);
        holder.textViewStartDate.setText(trackingDto.fromDate);
        holder.textViewEndDate.setText(trackingDto.toDate);
        holder.textViewNumber.setText(String.valueOf(trackingDto.itemQuantity));
        if (zoneId > 0 && trackingDtos.get(position).zoneId != zoneId && trackingDtos.get(position).isActive) {
            getApplicationComponent().MyDatabase().trackingDao()
                    .updateTrackingDtoByStatus(trackingDtos.get(position).id, false);
            trackingDtos.get(position).isActive = false;
        } else if (trackingDtos.get(position).isActive)
            zoneId = trackingDtos.get(position).zoneId;
        holder.linearLayout.setOnClickListener(view1 -> {
            if (zoneId > 0 && zoneId != trackingDtos.get(position).zoneId && !holder.checkBox.isChecked()) {
                new CustomToast().warning(MyApplication.getContext().getString(R.string.single_zone));
            } else {
                trackingDtos.get(position).isActive = !holder.checkBox.isChecked();
                zoneId = 0;
                getApplicationComponent().MyDatabase().trackingDao()
                        .updateTrackingDtoByStatus(trackingDtos.get(position).id,
                                trackingDtos.get(position).isActive);
                notifyDataSetChanged();
            }
        });
        holder.imageViewMap1.setOnClickListener(v -> {
            if (checkLocation(trackingDto)) {
                ShowDialogOnce(context, "ROAD_MAP_DIALOG", RoadMapFragment
                        .newInstance(trackingDtos.get(position).x, trackingDtos.get(position).y));
            }
        });
        holder.imageViewMap2.setOnClickListener(v -> {
            if (checkLocation(trackingDto)) {
                try {
                    final String uriString = "geo:" + trackingDtos.get(position).y + "," + trackingDtos.get(position).x + "?q=" +
                            Uri.encode(trackingDtos.get(position).y + "," + trackingDtos.get(position).x + "(label)") + "&z=19";
                    final Uri uri = Uri.parse(uriString);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } catch (Exception e) {
                    new CustomToast().warning("دستگاه شما مجهز به مسیریاب نیست.");
                }
            }
        });
        holder.checkBox.setChecked(trackingDtos.get(position).isActive);
        return convertView;
    }

    private boolean checkLocation(TrackingDto trackingDto) {
        if (trackingDto.y != null && !trackingDto.y.isEmpty() && !trackingDto.y.equals("0"))
            return true;
        new CustomToast().warning(context.getString(R.string.location_not_found));
        return false;
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

}