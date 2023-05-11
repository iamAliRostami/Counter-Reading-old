package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.PossibleViewHolder;
import com.leon.counter_reading.tables.DynamicTraverse;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class PossibleAdapter extends RecyclerView.Adapter<PossibleViewHolder> {
    private final ArrayList<DynamicTraverse> dynamicTraverses = new ArrayList<>();
    private final LayoutInflater inflater;

    public PossibleAdapter(Context context) {
        this.dynamicTraverses.addAll(getApplicationComponent().MyDatabase().dynamicTraverseDao().getDynamicTraverses());
//        this.inflater = LayoutInflater.from(context);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public PossibleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = inflater.inflate(R.layout.item_possible, viewGroup, false);
        return new PossibleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PossibleViewHolder possibleViewHolder, int i) {
        possibleViewHolder.checkBox.setText(dynamicTraverses.get(i).title);
        possibleViewHolder.checkBox.setChecked(getApplicationComponent().SharedPreferenceModel().getBoolData(dynamicTraverses.get(i).storageTitle));
        possibleViewHolder.checkBox.setClickable(dynamicTraverses.get(i).isChangeable);
        possibleViewHolder.checkBox.setOnClickListener(v -> {
            if (dynamicTraverses.get(i).isChangeable) {
                getApplicationComponent().SharedPreferenceModel().putData(dynamicTraverses.get(i).storageTitle,
                        possibleViewHolder.checkBox.isChecked());
            } else {
                new CustomToast().warning("این آیتم قابل تغییر نیست");
                possibleViewHolder.checkBox.setChecked(getApplicationComponent().SharedPreferenceModel().getBoolData(dynamicTraverses.get(i).storageTitle));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dynamicTraverses.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
