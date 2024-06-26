package io.github.hmzi67.securezone.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import org.osmdroid.util.GeoPoint;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentSosBinding;


public class SosFragment extends Fragment {
    private FragmentSosBinding binding;
    private CountDownTimer countDownTimer;
    private LocationManager locationManager;
    private LocationListener locationListener;

    double latitude;
    double longitude;

    public SosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSosBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.helpNeeded.setOnClickListener(view -> {
            if (countDownTimer == null)
                startCountdown();
            else {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        });

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    private void startCountdown() {
        binding.icon.setVisibility(View.GONE);
        binding.successStatus.setVisibility(View.GONE);
        binding.time.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                binding.time.setText(String.valueOf(secondsRemaining + 1));
            }

            @Override
            public void onFinish() {
                binding.time.setVisibility(View.GONE);
                binding.icon.setImageResource(R.drawable.ic_check);
                binding.icon.setVisibility(View.VISIBLE);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                String address = sharedPreferences.getString("address", "");


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Emergency: SOS! Need immediate assistance. My Location is " + "https://www.google.com/maps?q=" + latitude + "," + longitude +  ". Urgent help required.");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");

                try {
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                }

                // binding.successStatus.setVisibility(View.VISIBLE);
                countDownTimer = null;
            }
        }.start();

    }
}