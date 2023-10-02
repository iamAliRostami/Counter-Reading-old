package com.leon.counter_reading.adapters.recycler_view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private final OnItemClickListener listener;
    private final GestureDetector gestureDetector;
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView,
                                     OnItemClickListener listener) {
        this.listener = listener;
        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(@NonNull MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && RecyclerItemClickListener.this.listener != null) {
                            RecyclerItemClickListener.this.listener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                        }
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongItemClick(View view, int position);
    }
}