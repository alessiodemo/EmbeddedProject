package com.example.sample;

import static java.lang.Math.toRadians;

import androidx.appcompat.app.AppCompatActivity;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import android.Manifest;
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
//https://github.com/alessiodemo/EmbeddedProject.git
//the classes AlarmHandler and ExecutableService are designed for the operation that is repeated in background
public class MainActivity extends AppCompatActivity
{
    //VARIABILI PRIVATE
    private TextView AddressText;
    private Button LocationButton;
    private Button LocationButton1;
    private LocationRequest locationRequest;
    private int distance;
    private int steps;
    private List<Double> positions = new ArrayList<Double>();
    //------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        AddressText = findViewById(R.id.addressText);
        LocationButton = findViewById(R.id.locationButton);
        LocationButton1 = findViewById(R.id.quack);
        Button x = findViewById(R.id.clear);
        TextView t = findViewById(R.id.coordinates);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        //if i click on the "Get Current Location" button, it will call the method to get the coordinates of your position
        LocationButton.setOnClickListener(new View.OnClickListener()
        {
            private View view;

            @Override
            public void onClick(View v)
            {
                getCurrentLocation();
            }
        });

        //if i click on the "GET_DISTANCE" button it will replace the text of an invisible textview with the value returned with the method getDistance
        LocationButton1.setOnClickListener(new View.OnClickListener()
        {
            private View view;

            @Override
            public void onClick(View v)
            {
                t.setText(getDistance()+"");
            }
        });

        //if i click on the "clear" button it will reset the textviews at their default values and it will remove all elements from the list of coordinates
        x.setOnClickListener(new View.OnClickListener()
        {
            private View view1;
            @Override
            public void onClick(View v)
            {
                positions.clear();
                AddressText.setText("User Address");
                t.setText("");
            }
        });

        //scheduled task
        AlarmHandler alarmHandler = new AlarmHandler(this);
        //cancel the previous scheduled alarms
        alarmHandler.cancelAlarmManager();
        //set the new alarm after one hour
        alarmHandler.setAlarmManager();

        Toast.makeText(this, "Position set!",Toast.LENGTH_SHORT).show();
        //------------------------------------------------------------------------------------------------
    }



    //this method calculate the distance in meters and then dividing by 0.65 the value returned is converted in steps
    //if the coordinate are less than 4 (2 latitudes and 2 longitude) the method return -1
    private String getDistance()
    {
        final int R = 6371;

        if(positions.size()>=4)
        {
            Double latDistance = toRadians(positions.get(2) - positions.get(0));
            Double lonDistance = toRadians(positions.get(3) - positions.get(1));
            Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(toRadians(positions.get(0))) * Math.cos(toRadians(positions.get(2))) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = (int) (R * c*1000/0.65 *0.8);
            steps=steps+distance;
            return distance+"";
        }

        return "-1";
    }

    //this method provide us the current values of latitude and longitude
    public void getCurrentLocation()
    {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
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

                                    positions.add(locationResult.getLocations().get(index).getLatitude());
                                    positions.add(locationResult.getLocations().get(index).getLongitude());

                                    AddressText.setText("Latitude: "+ positions.get(0) + "\n" + "Longitude: "+ positions.get(1));
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

    //the following methods are the helper methods of the getCurrentLocation method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (isGPSEnabled())
                {
                    getCurrentLocation();
                }
                else
                {
                    turnOnGPS();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                getCurrentLocation();
            }
        }
    }

    private void turnOnGPS()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>()
        {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task)
            {
                try
                {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();
                }
                catch (ApiException e)
                {
                    switch (e.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try
                            {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            }
                            catch (IntentSender.SendIntentException ex)
                            {
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

    private boolean isGPSEnabled()
    {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
    //------------------------------------------------------------------------------------------------
}