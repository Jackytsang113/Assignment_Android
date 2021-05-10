package com.example.assignment_300cem.GoogleMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment_300cem.MainActivity;
import com.example.assignment_300cem.PostsActivity;
import com.example.assignment_300cem.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public boolean isPermissionGranted;
    public GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationClient;
    SupportMapFragment supportMapFragment;
    EditText search_location;
    ImageView search_icon, fab, backpage;
    TextView next_txt;

    double mLatitude, mLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        mLatitude =  mLongitude = 0;

        fab = (ImageView) findViewById(R.id.fab);
        search_location = (EditText) findViewById(R.id.search_location);
        search_icon = (ImageView) findViewById(R.id.search_icon);
        backpage = (ImageView) findViewById(R.id.backpage);
        next_txt = (TextView) findViewById(R.id.next_txt);

        checkMyPermission(); // check the phone is allow permission
        initMap(); //Obtain the SupportMapFragment

        mLocationClient = new FusedLocationProviderClient(this);

        next_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLatitude != 0 && mLongitude != 0){
                    Intent intent = new Intent(GoogleMapActivity.this, PostsActivity.class);
                    intent.putExtra("Latitude", Double.toString(mLatitude)); //put Latitude value to PostActivity
                    intent.putExtra("Longitude", Double.toString(mLongitude)); //put Longitude value to PostActivity
                    startActivity(intent);
                }
            }
        });

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GoogleMapActivity.this, MainActivity.class));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrLocation();
            }
        });

        search_icon.setOnClickListener(this::geoLocate);
    }

    private void geoLocate(View view) {
        String locationName = search_location.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);

            if(addressList.size() > 0){
                Address address = addressList.get(0);

                gotoLocation(address.getLatitude(), address.getLongitude());

                //Clear the Previously Click position
                mGoogleMap.clear();
                //Add Marker On Map
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));

                mLatitude = address.getLatitude();
                mLongitude = address.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrLocation() {
        mLocationClient.getLastLocation().addOnCompleteListener(task ->
        {
            if(task.isSuccessful()){
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        System.out.println(latLng.latitude + " : " + latLng.longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private void initMap() {
        if(isPermissionGranted){
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
            supportMapFragment.getMapAsync(this);
        }
    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                System.out.println("Permission Granted");
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                //Creating Marker
                MarkerOptions markerOptions = new MarkerOptions();
                //Set Marker Position
                markerOptions.position(latLng);
                //Set Latitude And Longitude On Marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                //Clear the Previously Click position
                mGoogleMap.clear();
                //Zoom the Marker
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                //Add Marker On Map
                mGoogleMap.addMarker(markerOptions);

                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}