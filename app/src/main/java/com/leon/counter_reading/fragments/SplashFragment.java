package com.leon.counter_reading.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.StartActivity;
import com.leon.counter_reading.databinding.FragmentSplashBinding;


public class SplashFragment extends Fragment {
    private FragmentSplashBinding binding;
    private Callback startActivity;
    private final Thread timerThread = new Thread() {
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                startActivity.splashLoaded();
            }
        }
    };

    public SplashFragment() {
    }

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        startSplash();
        return binding.getRoot();
    }
    private void startSplash() {
        binding.imageViewSplashScreen.setImageResource(R.drawable.img_splash);
        binding.shimmerViewContainer.startShimmer();
        timerThread.start();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof StartActivity)
            startActivity = (StartActivity) context;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            binding.shimmerViewContainer.setShimmer(null);
            binding.imageViewSplashScreen.setImageDrawable(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void splashLoaded();
    }
}