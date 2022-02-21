package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.databinding.FragmentRoadMapBinding;
import com.leon.counter_reading.enums.BundleEnum;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Objects;

public class RoadMapFragment extends DialogFragment {
    private FragmentRoadMapBinding binding;
    private double latitude;
    private double longitude;

    public static RoadMapFragment newInstance(String param1, String param2) {
        RoadMapFragment fragment = new RoadMapFragment();
        Bundle args = new Bundle();
        args.putDouble(BundleEnum.LONGITUDE.getValue(), Double.parseDouble(param1));
        args.putDouble(BundleEnum.LATITUDE.getValue(), Double.parseDouble(param2));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("here", "onCreate");
        if (getArguments() != null) {
            latitude = getArguments().getDouble(BundleEnum.LATITUDE.getValue());
            longitude = getArguments().getDouble(BundleEnum.LONGITUDE.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoadMapBinding.inflate(inflater, container, false);
        Log.e("here", "onCreateView");
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeMapView();
    }

    private void initializeMapView() {
        requireActivity().runOnUiThread(() -> {
            binding.mapView.getZoomController().
                    setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
            binding.mapView.setMultiTouchControls(true);
            IMapController mapController = binding.mapView.getController();
            mapController.setZoom(19.0);
            final GeoPoint startPoint;
            if (getLocationTracker(requireActivity()).getCurrentLocation() != null) {
                startPoint = new GeoPoint(getLocationTracker(requireActivity()).getCurrentLocation().getLatitude(),
                        getLocationTracker(requireActivity()).getCurrentLocation().getLongitude());
            } else {
                startPoint = new GeoPoint(latitude, longitude);
            }
            final GeoPoint endPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(startPoint);
            MyLocationNewOverlay locationOverlay =
                    new MyLocationNewOverlay(new GpsMyLocationProvider(requireActivity()), binding.mapView);
            locationOverlay.enableMyLocation();
            binding.mapView.getOverlays().add(locationOverlay);
            addRouteOverlay(startPoint, endPoint);
        });
        binding.progressBar.setVisibility(View.GONE);
    }

    private void addRouteOverlay(GeoPoint startPoint, GeoPoint endPoint) {
        requireActivity().runOnUiThread(() -> {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            RoadManager roadManager = new OSRMRoadManager(requireActivity());
            final ArrayList<GeoPoint> wayPoints = new ArrayList<>();
            wayPoints.add(startPoint);
            wayPoints.add(endPoint);
            Road road = roadManager.getRoad(wayPoints);
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

            roadOverlay.setWidth(12);
            roadOverlay.setColor(Color.RED);

            String distance = String.valueOf(roadOverlay.getDistance());
            distance = distance.substring(0, distance.indexOf("."));
            binding.textViewDistance.setText(distance);
            binding.mapView.getOverlays().add(roadOverlay);
            binding.mapView.invalidate();
        });
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }
}