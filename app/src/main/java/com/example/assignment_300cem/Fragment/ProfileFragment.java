package com.example.assignment_300cem.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment_300cem.Model.Users;
import com.example.assignment_300cem.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private CircleImageView image_profile;
    private TextView username_txt;
    private EditText email, username;
    private Button edit_btn, update_btn, cancel_btn;

    private Uri photoUri;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private StorageTask uploadTask;


    public ProfileFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("profile_image");

        image_profile = view.findViewById(R.id.image_profile);
        username_txt = view.findViewById(R.id.username_txt);
        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        edit_btn = view.findViewById(R.id.edit_btn);
        update_btn = view.findViewById(R.id.update_btn);
        cancel_btn = view.findViewById(R.id.cancel_btn);

        readUser();

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), ProfileFragment.this);
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_btn.setVisibility(View.GONE);
                update_btn.setVisibility(View.VISIBLE);
                cancel_btn.setVisibility(View.VISIBLE);
                username.setEnabled(true);
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_btn.setVisibility(View.VISIBLE);
                update_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                username.setEnabled(false);
                updateProfile();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_btn.setVisibility(View.VISIBLE);
                update_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                username.setEnabled(false);
                readUser();
            }
        });

        return view;
    }

    private void updateProfile() {
        String str_username = username.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        HashMap<String, Object> profile = new HashMap<>();
        profile.put("username", str_username);

        reference.updateChildren(profile);

        Toast.makeText(getContext(), getResources().getString(R.string.profile_success_updated), Toast.LENGTH_SHORT).show();
        readUser();
    }

    private void readUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                Glide.with(getContext()).load(users.getImage_url()).into(image_profile);
                username_txt.setText(users.getUsername());
                username.setText(users.getUsername());
                email.setText(users.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadPhoto() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getResources().getString(R.string.photo_uploading));
        pd.show();
        if(photoUri != null){
            StorageReference file_reference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(photoUri));

            uploadTask = file_reference.putFile(photoUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                        throw task.getException();

                    return file_reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri download_uri = task.getResult();
                        String str_photoUri = download_uri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());

                        HashMap<String, Object> update = new HashMap<>();
                        update.put("image_url", str_photoUri);

                        reference.updateChildren(update);
                        pd.dismiss();
                    }else{
                        Toast.makeText(getContext(), getResources().getString(R.string.fail_update_photo), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getContext(), getResources().getString(R.string.not_found_photo), Toast.LENGTH_SHORT).show();
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

                uploadPhoto();
            }
        }
    }
}