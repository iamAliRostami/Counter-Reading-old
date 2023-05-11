package com.leon.counter_reading.adapters.recycler_view;

import static com.leon.counter_reading.utils.USBUtils.isConfirmButton;

import android.content.Context;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerUsbItemClickListener implements RecyclerView.OnItemTouchListener {
    private final OnItemClickListener listener;
    private final RecyclerView recyclerView;
    private final GestureDetector gestureDetector;
    public RecyclerUsbItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        this.listener = listener;
        this.recyclerView = recyclerView;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                final View child = RecyclerUsbItemClickListener.this.recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && RecyclerUsbItemClickListener.this.listener != null) {
                    RecyclerUsbItemClickListener.this.listener.onLongItemClick(child, RecyclerUsbItemClickListener.this.recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        final View childView = view.findChildViewUnder(e.getX(), e.getY());
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

    public boolean handleDPad(View child, KeyEvent keyEvent) {
        final int position = recyclerView.getChildLayoutPosition(child);
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (isConfirmButton(keyEvent)) {
                if ((keyEvent.getFlags() & KeyEvent.FLAG_LONG_PRESS) == KeyEvent.FLAG_LONG_PRESS) {
                    listener.onLongItemClick(child, position);
                } else {
                    keyEvent.startTracking();
                }
                return true;
            }
        } else if (keyEvent.getAction() == KeyEvent.ACTION_UP && isConfirmButton(keyEvent)
                && ((keyEvent.getFlags() & KeyEvent.FLAG_LONG_PRESS) != KeyEvent.FLAG_LONG_PRESS)) {
            listener.onItemClick(child, position);
            return true;
        }
        return false;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongItemClick(View view, int position);
    }
}
