package io.github.hmzi67.securezone.Fragments;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import io.github.hmzi67.securezone.Activities.SettingsActivity;
import io.github.hmzi67.securezone.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private IMapController mapController;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (isStoragePermissionGranted()){
//
//            }
//        }


        // map = findViewById(R.id.mapView);
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.setBuiltInZoomControls(true);
        binding.mapView.setMultiTouchControls(true);
        mapController = binding.mapView.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(51496994, -134733);
        mapController.setCenter(startPoint);
    }

//    private boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED ; checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG, "Permission is granted");
//                return true;
//            } else {
//
//                Log.v(TAG, "Permission is revoked");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                return false;
//            }
//        } else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG, "Permission is granted");
//            return true;
//        }
//    }
}