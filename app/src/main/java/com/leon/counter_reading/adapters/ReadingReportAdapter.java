package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.enums.FragmentTags.AHAD;
import static com.leon.counter_reading.enums.FragmentTags.KARBARI;
import static com.leon.counter_reading.enums.FragmentTags.TAVIZ;
import static com.leon.counter_reading.fragments.dialog.ShowFragmentDialog.ShowDialogOnce;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.ReadingReportViewHolder;
import com.leon.counter_reading.fragments.dialog.AhadFragment;
import com.leon.counter_reading.fragments.dialog.KarbariFragment;
import com.leon.counter_reading.fragments.dialog.TaviziFragment;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;

import java.util.ArrayList;

public class ReadingReportAdapter extends RecyclerView.Adapter<ReadingReportViewHolder> {

    private final ArrayList<CounterReportDto> counterReportDtos;
    private final ArrayList<OffLoadReport> offLoadReports;
    private final Context context;
    private final int tracking;
    private final int page;
    private final String uuid;

    public ReadingReportAdapter(Context context, String uuid, int tracking, int position,
                                ArrayList<CounterReportDto> counterReportDtos,
                                ArrayList<OffLoadReport> offLoadReports) {
        this.counterReportDtos = counterReportDtos;
        this.offLoadReports = offLoadReports;
        this.uuid = uuid;
        this.tracking = tracking;
        this.page = position;
        this.context = context;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ReadingReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View drawerView = inflater.inflate(R.layout.item_public, parent, false);
        return new ReadingReportViewHolder(drawerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingReportViewHolder holder, int position) {
        holder.checkBox.setText(counterReportDtos.get(position).title);
        holder.checkBox.setChecked(counterReportDtos.get(position).isSelected);
    }

    @Override
    public int getItemCount() {
        return counterReportDtos.size();
    }

    public void update(int position) {
        counterReportDtos.get(position).isSelected = !counterReportDtos.get(position).isSelected;
        CounterReportDto counterReportDto = counterReportDtos.get(position);
//        holder.checkBox.setChecked(!holder.checkBox.isChecked());
        if (counterReportDtos.get(position).isSelected) {
            OffLoadReport offLoadReport = new OffLoadReport(uuid, tracking,
                    counterReportDto.id, counterReportDto.hasImage);
            getApplicationComponent().MyDatabase().offLoadReportDao().insertOffLoadReport(offLoadReport);
            offLoadReports.add(offLoadReport);
            if (counterReportDto.isAhad) {
                ShowDialogOnce(context, AHAD.getValue(), AhadFragment.newInstance(uuid, page));
            }
            if (counterReportDto.isTavizi) {
                ShowDialogOnce(context, TAVIZ.getValue(), TaviziFragment.newInstance(uuid, page));
            }
            if (counterReportDto.isKarbari) {
                ShowDialogOnce(context, KARBARI.getValue(), KarbariFragment.newInstance(uuid, page));
            }
        } else {
            for (int i = 0; i < offLoadReports.size(); i++) {
                if (offLoadReports.get(i).reportId == counterReportDto.id) {
                    getApplicationComponent().MyDatabase().offLoadReportDao().
                            deleteOffLoadReport(offLoadReports.get(i).reportId, tracking, uuid);
                    offLoadReports.remove(offLoadReports.get(i));
                }
            }
        }
        notifyItemChanged(position);
    }

    public int selectedCount() {
        return offLoadReports.size();
    }
}
