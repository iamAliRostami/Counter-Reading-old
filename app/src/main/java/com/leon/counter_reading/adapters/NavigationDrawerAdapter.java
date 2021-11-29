package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.helpers.Constants.POSITION;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.holder.DrawerItemHolder;
import com.leon.counter_reading.adapters.items.DrawerItem;

import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<DrawerItemHolder> {
    private final List<DrawerItem> drawerItemList;
    private final Context context;

    public NavigationDrawerAdapter(Context context, List<DrawerItem> listItems) {
        this.context = context;
        this.drawerItemList = listItems;
    }

    @NonNull
    @Override
    public DrawerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View drawerView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);
        return new DrawerItemHolder(drawerView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerItemHolder holder, int position) {
        final DrawerItem drawerItem = drawerItemList.get(position);
        if (position == 8) {
            holder.textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (position == POSITION) {
            final TypedValue typedValue = new TypedValue();
            final Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
            holder.textViewTitle.setTextColor(typedValue.data);
            holder.linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.border_red_3));
        }
        holder.imageViewIcon.setImageDrawable(drawerItem.getDrawable());
        holder.textViewTitle.setText(drawerItem.getItemName());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return drawerItemList.size();
    }
}