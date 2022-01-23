package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowFragmentDialogOnce;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.ReadingReportCheckBoxViewHolder;
import com.leon.counter_reading.fragments.dialog.AhadFragment;
import com.leon.counter_reading.fragments.dialog.KarbariFragment;
import com.leon.counter_reading.fragments.dialog.TaviziFragment;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;

import java.util.ArrayList;

public class ReadingReportCustomAdapter extends BaseAdapter {
    private final ArrayList<CounterReportDto> counterReportDtos;
    private final ArrayList<OffLoadReport> offLoadReports;
    private final LayoutInflater inflater;
    private final Context context;
    private final int tracking;
    private final String uuid;

    public ReadingReportCustomAdapter(Context context, String uuid, int tracking,
                                      ArrayList<CounterReportDto> counterReportDtos,
                                      ArrayList<OffLoadReport> offLoadReports) {
        this.counterReportDtos = counterReportDtos;
        this.offLoadReports = offLoadReports;
        this.uuid = uuid;
        this.tracking = tracking;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReadingReportCheckBoxViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_public, null);
        }
        holder = new ReadingReportCheckBoxViewHolder(view);
        holder.checkBox.setText(counterReportDtos.get(position).title);
        holder.checkBox.setOnClickListener(view1 -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            if (holder.checkBox.isChecked()) {
                final OffLoadReport offLoadReport = new OffLoadReport(uuid, tracking,
                        counterReportDtos.get(position).id);
                getApplicationComponent().MyDatabase().offLoadReportDao()
                        .insertOffLoadReport(offLoadReport);
                offLoadReports.add(offLoadReport);
                if (counterReportDtos.get(position).isAhad) {
                    ShowFragmentDialogOnce(context, "AHAD_DIALOG", AhadFragment.newInstance(uuid));
                }
                if (counterReportDtos.get(position).isTavizi) {
                    ShowFragmentDialogOnce(context, "TAVIZ_DIALOG", TaviziFragment.newInstance(uuid));
                }
                if (counterReportDtos.get(position).isKarbari) {
                    ShowFragmentDialogOnce(context, "KARBARI_DIALOG", KarbariFragment.newInstance(uuid));
                }
            } else {
                for (int i = 0; i < offLoadReports.size(); i++) {
                    if (offLoadReports.get(i).reportId == counterReportDtos.get(position).id) {
                        getApplicationComponent().MyDatabase().offLoadReportDao().
                                deleteOffLoadReport(offLoadReports.get(i).reportId, tracking, uuid);
                        offLoadReports.remove(offLoadReports.get(i));
                    }
                }
            }
            counterReportDtos.get(position).isSelected = holder.checkBox.isChecked();
        });
        holder.checkBox.setChecked(counterReportDtos.get(position).isSelected);
        return view;
    }

    @Override
    public int getCount() {
        return counterReportDtos.size();
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