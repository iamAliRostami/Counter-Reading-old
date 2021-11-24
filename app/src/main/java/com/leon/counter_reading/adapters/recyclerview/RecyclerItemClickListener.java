package com.leon.counter_reading.adapters.recyclerview;

import static com.leon.counter_reading.utils.USBUtils.isConfirmButton;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private final OnItemClickListener listener;
    private final RecyclerView recyclerView;
    private final GestureDetector gestureDetector;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongItemClick(View view, int position);
    }

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        this.listener = listener;
        this.recyclerView = recyclerView;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = RecyclerItemClickListener.this.recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && RecyclerItemClickListener.this.listener != null) {
                    RecyclerItemClickListener.this.listener.onLongItemClick(child, RecyclerItemClickListener.this.recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        Log.e("onInterceptTouchEvent ", e.toString());
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(childView, view.getChildAdapterPosition(childView));
            Log.e("onInterceptTouchEvent: ", "singleClick!");
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView view, MotionEvent motionEvent) {
        Log.d("onTouchEvent ", motionEvent.toString());
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public boolean handleDPad(View child, int keyCode, KeyEvent keyEvent) {
        int position = recyclerView.getChildLayoutPosition(child);
        // Return false if scrolled to the bounds and allow focus to move off the list
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
}
