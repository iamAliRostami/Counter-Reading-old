package com.leon.counter_reading.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    List<String> rank;
    List<String> country;
    List<String> population;
    List<Integer> flag = new ArrayList<>();
    LayoutInflater inflater;

    public ViewPagerAdapter(Context context, String[] rank, String[] country, String[] population,
                            int[] flag) {
        this.context = context;
        this.rank = new ArrayList<>(Arrays.asList(rank));
        this.country = new ArrayList<>(Arrays.asList(country));
        this.population = new ArrayList<>(Arrays.asList(population));
        for (int i : flag) {
            this.flag.add(i);
        }

    }

    @Override
    public int getCount() {
        return rank.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // Declare Variables
        TextView txtrank;
        TextView txtcountry;
        TextView txtpopulation;
        ImageView imgflag;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        // Locate the TextViews in viewpager_item.xml
        txtrank = (TextView) itemView.findViewById(R.id.rank);
        txtcountry = (TextView) itemView.findViewById(R.id.country);
        txtpopulation = (TextView) itemView.findViewById(R.id.population);
        Button buttonSubmit = (Button) itemView.findViewById(R.id.button1);

        final int delPosition = position;

        buttonSubmit.setOnClickListener(v -> {
            rank.remove(delPosition);
            country.remove(delPosition);
            population.remove(delPosition);
            flag.remove(delPosition);
            notifyDataSetChanged();
        });
        // Capture position and set to the TextViews
        txtrank.setText(rank.get(position));
        txtcountry.setText(country.get(position));
        txtpopulation.setText(population.get(position));

        // Locate the ImageView in viewpager_item.xml
        imgflag = (ImageView) itemView.findViewById(R.id.flag);
        // Capture position and set to the ImageView
        imgflag.setImageResource(flag.get(position));

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}