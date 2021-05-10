package com.example.assignment_300cem.GoogleMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.example.assignment_300cem.MainActivity;
import com.example.assignment_300cem.R;
import com.example.assignment_300cem.ViewPostActivity;
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

public class ViewPostMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    public boolean isPermissionGranted;
    public GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationClient;
    SupportMapFragment supportMapFragment;
    ImageView backpage, fab;

    String LocationName;
    double postLatitude, postLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_map);

        backpage = (ImageView) findViewById(R.id.backpage);
        fab = (ImageView) findViewById(R.id.fab);

        Intent intent = getIntent();
        LocationName = intent.getStringExtra("LocationName");
        postLatitude = Double.parseDouble(intent.getStringExtra("Latitude"));
        postLongitude = Double.parseDouble(intent.getStringExtra("Longitude"));

        mLocationClient = new FusedLocationProviderClient(this);

        checkMyPermission(); // check the phone is allow permission
        initMap(); //Obtain the SupportMapFragment

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrLocation();
            }
        });
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

        LatLng sydney = new LatLng(postLatitude , postLongitude); // go to select location
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(LocationName));
    }
}