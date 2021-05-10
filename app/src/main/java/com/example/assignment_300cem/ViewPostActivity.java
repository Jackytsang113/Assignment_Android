package com.example.assignment_300cem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.assignment_300cem.Adapter.PostsAdapter;
import com.example.assignment_300cem.GoogleMap.GoogleMapActivity;
import com.example.assignment_300cem.Model.Posts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewPostActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private List<Posts> postsList;

    private ImageView backpage;
    ProgressBar progress_circular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        backpage = (ImageView) findViewById(R.id.backpage);
        progress_circular = findViewById(R.id.progress_circular);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        postsList = new ArrayList<>();
        postsAdapter = new PostsAdapter(this, postsList);
        recyclerView.setAdapter(postsAdapter);

        readPosts();

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewPostActivity.this, MainActivity.class));
            }
        });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Posts post = snapshot.getValue(Posts.class);
                    postsList.add(post);

                }
                postsAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}