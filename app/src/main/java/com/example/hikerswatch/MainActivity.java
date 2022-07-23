package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                startListening();
            }
        }
    }
    public void startListening()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }
    public void updateLoc(Location loc)
    {
        TextView lattext=(TextView)findViewById(R.id.latitudetext);
        TextView lontext=(TextView)findViewById(R.id.longitudetext);
        TextView acctext=(TextView)findViewById(R.id.accuracytext);
        TextView altitudetext=(TextView)findViewById(R.id.altitudetext);
        TextView addresstext=(TextView)findViewById(R.id.addresstext);
        lattext.setText("Latitude: "+Double.toString(loc.getLatitude()));
        lontext.setText("Longitude: "+Double.toString(loc.getLongitude()));
        acctext.setText("Accuracy: "+Double.toString(loc.getAccuracy()));
        altitudetext.setText("Altitude: "+Double.toString(loc.getAltitude()));
        String address="Could not find address!";
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            Log.i("Location Info:",addresses.toString());
            if(addresses!=null && addresses.size()>0)
            {
                address="Address:\n";
                if(addresses.get(0).getThoroughfare()!=null)
                {
                    address+=addresses.get(0).getThoroughfare()+"\n";
                }
                if(addresses.get(0).getLocality()!=null)
                {
                    address+=addresses.get(0).getLocality()+" ";
                }
                if(addresses.get(0).getPostalCode()!=null)
                {
                    address+=addresses.get(0).getPostalCode()+" ";
                }
                if(addresses.get(0).getAdminArea()!=null)
                {
                    address+=addresses.get(0).getAdminArea()+"\n";
                }
                if(addresses.get(0).getAddressLine(0)!=null)
                {
                    address+=addresses.get(0).getAddressLine(0)+".";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        addresstext.setText(address);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLoc(location);
            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation!=null)
            {
                updateLoc(lastKnownLocation);
            }
        }
    }
}