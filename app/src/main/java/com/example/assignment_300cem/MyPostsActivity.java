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
import com.example.assignment_300cem.Model.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private List<Posts> myPostsList;

    private ImageView backpage;
    ProgressBar progress_circular;

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        backpage = (ImageView) findViewById(R.id.backpage);
        progress_circular = findViewById(R.id.progress_circular);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myPostsList = new ArrayList<>();
        postsAdapter = new PostsAdapter(this, myPostsList);
        recyclerView.setAdapter(postsAdapter);

        readMyPosts();

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPostsActivity.this, MainActivity.class));
            }
        });
    }

    private void readMyPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPostsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Posts posts = snapshot.getValue(Posts.class);

                    if (posts.getSender().equals(firebaseUser.getUid()))
                        myPostsList.add(posts);
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