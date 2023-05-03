package com.example.sample;


import static java.lang.Math.acos;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import androidx.appcompat.app.AppCompatActivity;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView AddressText;
    private Button LocationButton;
    private Button LocationButton1;
    private LocationRequest locationRequest;

    private double lat1;
    private double lon1;
    private List<Double> positions = new ArrayList<>();





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddressText = findViewById(R.id.addressText);

        LocationButton1 = findViewById(R.id.quack);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        getCurrentLocation(); //only to display at start

        LocationButton1.setOnClickListener(new View.OnClickListener() {
            private View view;


            @Override
            public void onClick(View v) {
                Button fred = findViewById(R.id.quack);
                TextView t = findViewById(R.id.coordinates);
                TextView arc = findViewById(R.id.boh);


                if(fred.isPressed())
                {
                    getCurrentLocation();
                    AddressText.setText(positions.size()+"");
                    //t.setText("Latitude: "+ positions.get(0) + "\n" + "Longitude: "+ positions.get(1));
                    /*
                        if(positions.size() == 2)
                        AddressText.setText(positions.get(0)+"");

                    if(positions.size() == 4)
                        AddressText.setText(positions.get(1)+"");

                    if(positions.size() == 6)
                        AddressText.setText(positions.get(2)+"");

                        if(positions.size() == 4)
                        t.setText(getDistance()+"");
                     */

                    if(positions.size() == 2)
                    {
                        t.setText("lon1= " +positions.get(1)+"");
                        AddressText.setText("lat1= " +positions.get(0)+"");
                    }


                    if(positions.size() == 4)
                    {
                        t.setText("lon2= " +positions.get(3)+"");
                        AddressText.setText("lat2= " +positions.get(2)+"");
                    }

                    if(positions.size() == 4)
                        arc.setText(getDistance()+"");





                }


            }
        });
    }





    private double getDistance() {
        final int R = 6371;
        //Double latDistance = toRadians(positions.get(2) - positions.get(0));
        //Double lonDistance = toRadians(positions.get(3) - positions.get(1));
        //double distance = acos(sin(positions.get(0))*sin(positions.get(2)) + cos(positions.get(0))*cos(positions.get(2))*cos(positions.get(3)-positions.get(1)))*R;

        double f=sin((positions.get(2)-positions.get(0))/2);
        double k=sin((positions.get(3)-positions.get(1))/2);

        double a = f*f + cos(positions.get(0))*cos(positions.get(2))*k*k;
        double c = 2*atan2(sqrt(a),sqrt(1-a));

        double distance = R*c;
        return distance;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled())
                {
                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        lat1 = locationResult.getLocations().get(index).getLatitude();
                                        lon1= locationResult.getLocations().get(index).getLongitude();
                                        positions.add(lat1); //lat1
                                        positions.add(lon1); //lon1

                                        //AddressText.setText(lat1+"");
                                        //AddressText.setText(positions.get(0)+"");
                                    }
                                }
                            }, Looper.getMainLooper());

                }
                else
                {
                    turnOnGPS();
                }

            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }




















}