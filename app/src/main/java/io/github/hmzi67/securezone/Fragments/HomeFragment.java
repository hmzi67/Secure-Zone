package io.github.hmzi67.securezone.Fragments;

import static android.content.Context.MODE_PRIVATE;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.github.hmzi67.securezone.Modals.LocationModel;
import io.github.hmzi67.securezone.Widgets.MyAlertDialog;
import io.github.hmzi67.securezone.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private IMapController mapController;
    private SharedPreferences pref;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isLiveAlert = false;

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
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("address", addressText);
                editor.apply(); // Apply changes asynchronously
            }
        });

        // Build the API URL
        String apiUrl = "https://api.opencagedata.com/geocode/v1/json?q=" +
                latitude + "," + longitude + "&key=" + "226ee3b17c684ed095df16f25e2c7e19";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Get the confidence value
                                    JSONArray resultsArray = response.getJSONArray("results");
                                    if (resultsArray.length() > 0) {
                                        JSONObject firstResult = resultsArray.getJSONObject(0);
                                        int confidence = firstResult.getInt("confidence");
                                        Log.d("TAG", "Confidence value: " + confidence);
                                        pref = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
                                        if (confidence < 5 && pref.getBoolean("LA", false)) {
                                            MyAlertDialog.showAlertDialog(getContext(), "Alert", "You are in area where there is less safety. Please get out of here.\n"
                                                    + "Reliably: " + (confidence * 10) + "%\n"
                                                    + "Location: " + addressText + "\n"
                                            );
                                        }
                                        Log.d("TAG", "Confidence value: " + confidence);
                                        // Update UI or perform further operations with confidence value
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("TAG", "Error retrieving JSON response: " + error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

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