package com.example.saveplace;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    LatLng locdisplay;
    String address, previousaddress, title;
    Boolean firsttime, firsttimeforlocation;
    double lat, lon;
    Intent intent;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        previousaddress = "";
        firsttime = true;
        firsttimeforlocation = true;
        title = "";
        intent = new Intent(getApplicationContext(), MainActivity.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(final Location location) {
                if(firsttimeforlocation) {
                    locdisplay = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locdisplay, 10));
                    firsttimeforlocation = false;
                }
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        if (firsttime) {

                            lat = point.latitude;
                            lon = point.longitude;
                            MarkerOptions marker = new MarkerOptions().position(
                                    new LatLng(point.latitude, point.longitude)).title("New Marker");

                            mMap.addMarker(marker);

                            locdisplay = new LatLng(point.latitude, point.longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locdisplay, 11));
                            intent.putExtra("latitude", String.valueOf(lat));
                            intent.putExtra("longitude", String.valueOf(lon));

                        }
                    }

                });

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                address = "";
                try
                {
                    List<Address> listAddresses = geocoder.getFromLocation(lat,lon,1);
                    if(listAddresses!=null && listAddresses.size()>0){

                        if(listAddresses.get(0).getSubThoroughfare()!=null)
                            address = listAddresses.get(0).getSubThoroughfare();
                        if(listAddresses.get(0).getThoroughfare()!=null)
                            address = address + " " + listAddresses.get(0).getThoroughfare();
                        if(listAddresses.get(0).getLocality()!=null)
                            address = address + " " + listAddresses.get(0).getLocality();
                        if(listAddresses.get(0).getPostalCode()!= null)
                            address = address + " " + listAddresses.get(0).getPostalCode();
                        if(listAddresses.get(0).getCountryName()!= null)
                            address = address + " " + listAddresses.get(0).getCountryName();
                        title = address;
                        intent.putExtra("address", title);

                        if(firsttime) {
                            previousaddress = address;
                            Toast.makeText(MapsActivity.this, address,Toast.LENGTH_SHORT).show();
                            Log.i("Place info", address);
                            MainActivity.places.add(title);
                            MainActivity.latitude.add(String.valueOf(lat));
                            MainActivity.longitude.add(String.valueOf(lon));
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            MainActivity.function();
                            firsttime = false;
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if(!previousaddress.equals(address))
                {
                    mMap.clear();
                    locdisplay = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(locdisplay).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locdisplay, 10));
                    Toast.makeText(MapsActivity.this, address,Toast.LENGTH_LONG).show();
                    Log.i("Place info", address);
                    previousaddress = address;
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
            }

        };

        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // we have permission!
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

}