package io.github.hmzi67.securezone.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.github.hmzi67.securezone.Modals.LocationModel;
import io.github.hmzi67.securezone.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private IMapController mapController;

    private LocationManager locationManager;
    private LocationListener locationListener;

    double latitude;
    double longitude;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    String addressText;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        // Ready the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        if (Build.VERSION.SDK_INT >= 23) {
            if (isStoragePermissionGranted()) {

            }
        }

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.setBuiltInZoomControls(true);
        binding.mapView.setMultiTouchControls(true);
        mapController = binding.mapView.getController();
        mapController.setZoom(15);


        //locationManager = getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                GeoPoint startPoint = new GeoPoint(latitude, longitude);
                mapController.setCenter(startPoint);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }

        // on track me
       binding.trackMe.setOnClickListener(view -> {
           showMyLocation();
       });

       // showOthersLocations
//       binding.showOthersLocations.setOnClickListener(view -> {
//           firebaseDatabase.getReference().child("Users").child("ENdLvhP6TwQnJrH8eqN0OweuOgp1").child("Location").addValueEventListener(new ValueEventListener() {
//               @Override
//               public void onDataChange(@NonNull DataSnapshot snapshot) {
//                   if (snapshot.exists()) {
//                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                           LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
//                           latitude = locationModel.getLatitude();
//                           longitude = locationModel.getLongitude();
//                           showMyLocation();
//                       }
//                   }
//               }
//
//               @Override
//               public void onCancelled(@NonNull DatabaseError error) {}
//           });
//       });
    }

    public void onResume() {
        super.onResume();
        if (binding.mapView != null)
            binding.mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        if (binding.mapView != null)
            binding.mapView.onPause();
    }


    private void showMyLocation() {
        binding.mapView.getOverlays().clear();
        Marker startMarker = new Marker(binding.mapView);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        binding.mapView.getOverlays().add(startMarker);
        binding.mapView.invalidate();

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = address.getAddressLine(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LocationModel locationModel = new LocationModel(latitude, longitude, addressText);
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Location").push().setValue(locationModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("address", addressText);
                editor.apply(); // Apply changes asynchronously
            }
        });
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                // Request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // Request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                startLocationUpdates();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }
}