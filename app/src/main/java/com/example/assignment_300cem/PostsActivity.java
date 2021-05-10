package com.example.assignment_300cem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.Model.Posts;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class PostsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public boolean isPermissionGranted;
    public GoogleMap mGoogleMap;
    SupportMapFragment supportMapFragment;
    ImageView location_photo, done_icon, backpage;
    EditText location_name, description;

    Double mLatitude, mLongitude;

    private Uri photoUri;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("posts_image");

        Intent intent = getIntent();
        mLatitude = Double.parseDouble(intent.getStringExtra("Latitude")); // get intent Latitude value
        mLongitude = Double.parseDouble(intent.getStringExtra("Longitude")); // get intent Longitude value

        location_photo = (ImageView) findViewById(R.id.location_photo);
        done_icon = (ImageView) findViewById(R.id.done_icon);
        location_name = (EditText) findViewById(R.id.location_name);
        description = (EditText) findViewById(R.id.description);
        backpage = (ImageView) findViewById(R.id.backpage);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostsActivity.this, MainActivity.class));
            }
        });

        location_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(PostsActivity.this);
            }
        });

        done_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPost();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true); //show my current location
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false); // disable dragging in Google Map Fragment

        LatLng sydney = new LatLng(mLatitude , mLongitude); // go to select location
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(mLatitude +  " : " + mLongitude));
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

    private boolean checkError(String str_location_name, String str_description){
        if(str_location_name.isEmpty()){ //check location name is empty show error message
            location_name.setError("Location name is required!");
            location_name.requestFocus();
            return true;
        }

        if(str_description.isEmpty()){ //check description is empty show error message
            description.setError("Description is required!");
            description.requestFocus();
            return true;
        }
        return false;
    }


    private void addPost() {
        String str_location_name = location_name.getText().toString();
        String str_description = description.getText().toString();

        if(checkError(str_location_name, str_description))
            return;

            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.photo_uploading));
            pd.show();
            if (photoUri != null) {
                StorageReference file_reference = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(photoUri));

                uploadTask = file_reference.putFile(photoUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful())
                            throw task.getException();

                        return file_reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri download_uri = task.getResult();
                            String str_photoUri = download_uri.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                            String postid = reference.push().getKey();
                            String userid = firebaseUser.getUid();
                            String strLatitude = Double.toString(mLatitude);
                            String strLongitude = Double.toString(mLongitude);

                            Posts posts = new Posts(postid, strLatitude, strLongitude, str_photoUri , str_location_name, str_description, userid);

                            reference.child(postid).setValue(posts);

                            pd.dismiss();

                            Intent intent = new Intent(PostsActivity.this, MainActivity.class); //back to Home page
                            startActivity(intent);
                        } else {
                            Toast.makeText(PostsActivity.this, getResources().getString(R.string.fail_update_photo), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(PostsActivity.this, getResources().getString(R.string.not_found_photo), Toast.LENGTH_SHORT).show();
            }

    }

    private String getFileExtension(Uri uri){
        Uri file = Uri.fromFile(new File(uri.toString()));
        return  MimeTypeMap.getFileExtensionFromUrl(file.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result != null){
                photoUri = result.getUri();
                Glide.with(this).load(photoUri).into(location_photo);
            }
        }
    }
}