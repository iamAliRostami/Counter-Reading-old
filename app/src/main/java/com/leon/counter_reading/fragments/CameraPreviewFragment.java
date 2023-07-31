package com.leon.counter_reading.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.databinding.FragmentCameraPreviewBinding;
import com.leon.counter_reading.databinding.FragmentTakePhotoBinding;

import java.io.File;

public class CameraPreviewFragment extends Fragment {
    private FragmentCameraPreviewBinding binding;

    public CameraPreviewFragment() {
    }

    public static CameraPreviewFragment newInstance() {
        return new CameraPreviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCameraPreviewBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {

    }
}