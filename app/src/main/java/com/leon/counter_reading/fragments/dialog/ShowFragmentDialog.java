package com.leon.counter_reading.fragments.dialog;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class ShowFragmentDialog {
    public static void ShowFragmentDialogOnce(Context context,String tag, DialogFragment dialogFragment) {
        final FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        final Fragment prev = ((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag(tag);
        if (prev == null) {
            ft.addToBackStack(null);
            dialogFragment.show(ft, tag);
        }
    }
}
