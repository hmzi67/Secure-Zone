package io.github.hmzi67.securezone.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.hmzi67.securezone.Modals.FakeCallModel;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentSosBinding;


public class SosFragment extends Fragment {
    private FragmentSosBinding binding;
    private CountDownTimer countDownTimer;
    private LocationManager locationManager;
    private LocationListener locationListener;

    double latitude;
    double longitude;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    private ArrayList contactNumbers;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    public SosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSosBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        // ready the firebase and contact numbers list.
        contactNumbers = new ArrayList();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // getting all contacts from the user.
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FakeCallModel fakeCallModel = dataSnapshot.getValue(FakeCallModel.class);
                    String number = fakeCallModel.getCallNumber();
                    contactNumbers.add(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // on SOS button clicked
        binding.helpNeeded.setOnClickListener(view -> {
            if (countDownTimer == null)
                startCountdown();
            else {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        });

        // setting up location manager
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

        startLocationUpdates();
    }

    // start location updates
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    // handle count down
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
                // send sms
                sendSMS();
                countDownTimer = null;
            }
        }.start();

    }

    // sending SMS to all contacts
    private void sendSMS() {
        for (int i = 0; i < contactNumbers.size(); i++) {
            String phoneNumber = contactNumbers.get(i).toString(); // "03143288112";
            String message = "Emergency: SOS! \nNeed immediate assistance. My Location is " + "https://www.google.com/maps?q=" + latitude + "," + longitude +  ". Urgent help required.";

            if (checkPermission()) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(requireContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                requestPermission();
            }
        }
    }

    // check permissions
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "SMS permission granted", Toast.LENGTH_SHORT).show();
                sendSMS();
            } else {
                Toast.makeText(requireContext(), "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}