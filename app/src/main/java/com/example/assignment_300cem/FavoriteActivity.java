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

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private List<Posts> favoriteList;
    private List<String> favoritePosts;

    private ImageView backpage;
    ProgressBar progress_circular;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        backpage = (ImageView) findViewById(R.id.backpage);
        progress_circular = findViewById(R.id.progress_circular);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        favoriteList = new ArrayList<>();
        postsAdapter = new PostsAdapter(this, favoriteList);
        recyclerView.setAdapter(postsAdapter);

        myFavorite();

        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FavoriteActivity.this, MainActivity.class));
            }
        });
    }

    private void myFavorite(){
        favoritePosts = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Favorite")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoritePosts.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    favoritePosts.add(snapshot.getKey());
                }
                readFavorite();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readFavorite() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Posts posts = snapshot.getValue(Posts.class);
                    for(String postId : favoritePosts){
                        if(posts.getPostId().equals(postId))
                            favoriteList.add(posts);
                    }

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