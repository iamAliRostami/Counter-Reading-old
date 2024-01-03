package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.LATITUDE;
import static com.leon.counter_reading.enums.BundleEnum.LONGITUDE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.helpers.MyApplication.getLocationTracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentRoadMapBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class RoadMapFragment extends DialogFragment {
    private FragmentRoadMapBinding binding;
    private double latitude;
    private double longitude;

    public static RoadMapFragment newInstance(String param1, String param2) {
        final RoadMapFragment fragment = new RoadMapFragment();
        final Bundle args = new Bundle();
        args.putDouble(LONGITUDE.getValue(), Double.parseDouble(param1));
        args.putDouble(LATITUDE.getValue(), Double.parseDouble(param2));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(LATITUDE.getValue());
            longitude = getArguments().getDouble(LONGITUDE.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoadMapBinding.inflate(inflater, container, false);
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
            final MyLocationNewOverlay locationOverlay =
                    new MyLocationNewOverlay(new GpsMyLocationProvider(requireActivity()), binding.mapView);
            locationOverlay.enableMyLocation();
            binding.mapView.getOverlays().add(locationOverlay);
            addRouteOverlay(startPoint, endPoint);
        });
        binding.progressBar.setVisibility(View.GONE);
    }

    private void addRouteOverlay(GeoPoint startPoint, GeoPoint endPoint) {
        requireActivity().runOnUiThread(() -> {
            final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            final RoadManager roadManager = new OSRMRoadManager(requireActivity(), Configuration.getInstance().getUserAgentValue());
            final ArrayList<GeoPoint> wayPoints = new ArrayList<>();
            wayPoints.add(startPoint);
            wayPoints.add(endPoint);
            final Road road = roadManager.getRoad(wayPoints);
            final Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

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
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            new CustomDialogModel(Red, requireContext(), R.string.refresh_page, R.string.dear_user,
                    R.string.take_screen_shot, R.string.accepted);
        }
        super.onResume();
    }
}