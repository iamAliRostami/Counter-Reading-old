package com.leon.counter_reading.adapters.holder;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.R;

public class ReadingViewHolder extends RecyclerView.ViewHolder {
    public final Spinner spinner;
    public final EditText editTextNumber;
    public final TextView textViewAhad1Title;
    public final TextView textViewAhad2Title;
    public final TextView textViewAhadTotalTitle;
    public final TextView textViewRadif;
    public final TextView textViewAhad1;
    public final TextView textViewAhad2;
    public final TextView textViewAhadTotal;
    public final TextView textViewName;
    public final TextView textViewAddress;
    public final TextView textViewPreDate;
    public final TextView textViewPreNumber;
    public final TextView textViewCode;
    public final TextView textViewKarbari;
    public final TextView textViewBranch;
    public final TextView textViewSiphon;
    public final TextView textViewSerial;
    public final Button buttonSubmit;

    public ReadingViewHolder(@NonNull View itemView) {
        super(itemView);
        spinner = itemView.findViewById(R.id.spinner);
        editTextNumber = itemView.findViewById(R.id.edit_text_number);
        textViewAhad1Title = itemView.findViewById(R.id.text_view_ahad_1_title);
        textViewAhad2Title = itemView.findViewById(R.id.text_view_ahad_2_title);
        textViewAhadTotalTitle = itemView.findViewById(R.id.text_view_ahad_total_title);
        textViewPreNumber = itemView.findViewById(R.id.text_view_pre_number);
        textViewPreDate = itemView.findViewById(R.id.text_view_pre_date);

        textViewAddress = itemView.findViewById(R.id.text_view_address);
        textViewRadif = itemView.findViewById(R.id.text_view_radif);
        textViewAhad1 = itemView.findViewById(R.id.text_view_ahad_1);
        textViewAhad2 = itemView.findViewById(R.id.text_view_ahad_2);
        textViewAhadTotal = itemView.findViewById(R.id.text_view_ahad_total);

        textViewCode = itemView.findViewById(R.id.text_view_code);
        textViewKarbari = itemView.findViewById(R.id.text_view_karbari);
        textViewBranch = itemView.findViewById(R.id.text_view_branch);
        textViewSiphon = itemView.findViewById(R.id.text_view_siphon);
        textViewName = itemView.findViewById(R.id.text_view_name);
        textViewSerial = itemView.findViewById(R.id.edit_text_serial);

        buttonSubmit = itemView.findViewById(R.id.button_submit);
    }

    public ReadingViewHolder(@NonNull View itemView, int position) {
        super(itemView);
        spinner = itemView.findViewById(R.id.spinner);
        editTextNumber = itemView.findViewById(R.id.edit_text_number);
        editTextNumber.setTag("editTextNumber".concat(String.valueOf(position)));
        textViewAhad1Title = itemView.findViewById(R.id.text_view_ahad_1_title);
        textViewAhad2Title = itemView.findViewById(R.id.text_view_ahad_2_title);
        textViewAhadTotalTitle = itemView.findViewById(R.id.text_view_ahad_total_title);
        textViewPreNumber = itemView.findViewById(R.id.text_view_pre_number);
        textViewPreDate = itemView.findViewById(R.id.text_view_pre_date);

        textViewAddress = itemView.findViewById(R.id.text_view_address);
        textViewRadif = itemView.findViewById(R.id.text_view_radif);
        textViewAhad1 = itemView.findViewById(R.id.text_view_ahad_1);
        textViewAhad2 = itemView.findViewById(R.id.text_view_ahad_2);
        textViewAhadTotal = itemView.findViewById(R.id.text_view_ahad_total);

        textViewCode = itemView.findViewById(R.id.text_view_code);
        textViewKarbari = itemView.findViewById(R.id.text_view_karbari);
        textViewBranch = itemView.findViewById(R.id.text_view_branch);
        textViewSiphon = itemView.findViewById(R.id.text_view_siphon);
        textViewName = itemView.findViewById(R.id.text_view_name);
        textViewSerial = itemView.findViewById(R.id.edit_text_serial);

        buttonSubmit = itemView.findViewById(R.id.button_submit);
    }
}
