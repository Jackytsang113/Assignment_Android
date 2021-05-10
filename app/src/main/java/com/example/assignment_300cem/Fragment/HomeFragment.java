package com.example.assignment_300cem.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assignment_300cem.FavoriteActivity;
import com.example.assignment_300cem.GoogleMap.GoogleMapActivity;
import com.example.assignment_300cem.Model.Users;
import com.example.assignment_300cem.MyPostsActivity;
import com.example.assignment_300cem.R;
import com.example.assignment_300cem.ViewPostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private LinearLayout layout_view_post, layout_post, layout_favorite, layout_myposts;
    private TextView username;

    private FirebaseUser firebaseUser;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        layout_view_post = view.findViewById(R.id.layout_view_post);
        layout_post  = view.findViewById(R.id.layout_post);
        layout_favorite = view.findViewById(R.id.layout_favorite);
        layout_myposts = view.findViewById(R.id.layout_myposts);
        username = view.findViewById(R.id.username_txt);

        layout_view_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ViewPostActivity.class));
            }
        });

        layout_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), GoogleMapActivity.class));
            }
        });

        layout_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FavoriteActivity.class));
            }
        });

        layout_myposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyPostsActivity.class));
            }
        });

        readUser();

        return view;
    }

    private void readUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}